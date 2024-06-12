package firstskin.firstskin.dianosis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import firstskin.firstskin.common.component.ModelPathResolver;
import firstskin.firstskin.common.constants.DfPath;
import firstskin.firstskin.common.constants.Operation;
import firstskin.firstskin.common.exception.*;
import firstskin.firstskin.dianosis.DiagnosisRepository;
import firstskin.firstskin.dianosis.api.request.DiagnosisDto;
import firstskin.firstskin.dianosis.api.response.DiagnosisResponse;
import firstskin.firstskin.dianosis.domain.Diagnosis;
import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.member.repository.MemberRepository;
import firstskin.firstskin.skin.Kind;
import firstskin.firstskin.skin.Skin;
import firstskin.firstskin.skin.repository.SkinRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.StdArrays;
import org.tensorflow.types.TFloat32;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiagnosisService {

    private final String[] typeLabels = {"dry", "typenormal", "oily"};
    private final String[] troubleLabels = {"troublenormal", "acne", "redness"};

    private final String[] personalColorLabels = {"fall", "spring", "summer", "winter"};

    private final DiagnosisRepository diagnosisRepository;
    private final MemberRepository memberRepository;
    private final SkinRepository skinRepository;
    private final ModelPathResolver modelPathResolver;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${python.white_balance}")
    private String whiteBalancePythonPath;

    @Value("${python.detect_face_female}")
    private String detectFemaleFacePythonPath;

    @Value("${python.detect_face_male}")
    private String detectMaleFacePythonPath;

    private SavedModelBundle typeModel;
    private SavedModelBundle troubleModel;
    private SavedModelBundle personalColorMaleModel;
    private SavedModelBundle personalColorFemaleModel;

    @PostConstruct
    public void init() {
        String typeModelPath = modelPathResolver.resolveTypeModelPath();
        String troubleModelPath = modelPathResolver.resolveTroubleModelPath();
        String personalColorMaleModelPath = modelPathResolver.resolvePersonalColorMaleModelPath();
        String personalColorFemaleModelPath = modelPathResolver.resolvePersonalColorFemaleModelPath();

        typeModel = SavedModelBundle.load(typeModelPath, "serve");
        troubleModel = SavedModelBundle.load(troubleModelPath, "serve");
        personalColorMaleModel = SavedModelBundle.load(personalColorMaleModelPath, "serve");
        personalColorFemaleModel = SavedModelBundle.load(personalColorFemaleModelPath, "serve");
    }


    public DiagnosisResponse diagnosisSkin(DiagnosisDto request) throws Exception {

        log.info("진단 시작");
        log.info("진단 종류: {}", request.getKind());
        log.info("진단할 파일: {}", request.getFile().getOriginalFilename());
        log.info("진단할 회원: {}", request.getMemberId());
        log.info("진단할 회원의 성별: {}", request.getSex());
        String filePath = saveFile(request);

        TFloat32 preprocessedImage = null;

        List<Kind> preprocessKind = Arrays.asList(Kind.TYPE, Kind.TROUBLE);
        if (preprocessKind.contains(request.getKind())) {
            BufferedImage img = ImageIO.read(Paths.get(filePath).toFile());
            preprocessedImage = preprocessImage(img);
        }

        TFloat32 result;

        String resultLabel;
        // TensorFlow 진단

        if (request.getKind().equals(Kind.TYPE)) {
            // 피부 타입 진단
            log.info("피부 타입 진단 시작");
            result = (TFloat32) typeModel.session().runner()
                    .feed(Operation.TYPE_INPUT, preprocessedImage)
                    .fetch(Operation.TYPE_OUTPUT)
                    .run().get(0);

            float[] resultArray = getFloats(result, 3);

            int maxIndex = argMax(resultArray);
            resultLabel = typeLabels[maxIndex];

            updateTypeCsvFile(maxIndex, filePath);

        } else if (request.getKind().equals(Kind.TROUBLE)) {
            // 피부 트러블 진단
            log.info("피부 트러블 진단 시작");
            result = (TFloat32) troubleModel.session().runner()
                    .feed(Operation.TROUBLE_INPUT, preprocessedImage)
                    .fetch(Operation.TROUBLE_OUTPUT)
                    .run().get(0);

            log.info("Trouble model output1r: {}", result);
            log.info("Trouble model output2r: {}", result.shape());
            float[] resultArray = getFloats(result, 3);

            int maxIndex = argMax(resultArray);
            resultLabel = troubleLabels[maxIndex];

            log.info("Trouble model output: {}", Arrays.toString(resultArray));


            updateTroubleCsvFile(maxIndex, filePath);

        } else if (request.getKind().equals(Kind.PERSONAL_COLOR)) {
            // 퍼스널 컬러 진단
            log.info("퍼스널 컬러 진단 시작");
            // 화이트 밸런스 조정
            whiteBalance(filePath);

            // 얼굴 인식 및 퍼스널 컬러 코드 추출
            int[][] hsvArrayInt = new int[1][9];
            double[][][] hsvArray = detectFaceForPersonalColor(filePath, request.getSex());

            log.info("hsvArray: {}", Arrays.deepToString(hsvArray));

            // double[][] -> int[] 변환
            for (int i = 0; i < 9; i++) {
                hsvArrayInt[0][i] = (int) hsvArray[0][0][i];
            }

            // csv 파일에 저장되는 배열
            log.info("hsvArrayInt: {}", Arrays.deepToString(hsvArrayInt));
            if (Arrays.stream(hsvArrayInt[0]).allMatch(value -> value == 0)) {
                log.error("얼굴 인식 실패");
                throw new FaceNotFound("얼굴 인식에 실패했습니다.");
            }

            // 모델에 넣는 배열
            log.info("ScaledHsvArray : {}", Arrays.deepToString(hsvArray[1]));

            float[][] scaledHsvArray = new float[1][9];
            for (double[][] doubles : hsvArray) {
                for (int j = 0; j < doubles[0].length; j++) {
                    scaledHsvArray[0][j] = (float) doubles[0][j];
                }
            }

            TFloat32 scaledHsvTensor = TFloat32.tensorOf(StdArrays.ndCopyOf(scaledHsvArray));

            log.info("ScaledHsvTensor : {}", Arrays.deepToString(scaledHsvArray));
            // 퍼스널 컬러 모델 진단

            if (request.getSex().equals("male")) {
                result = (TFloat32) personalColorMaleModel.session().runner()
                        .feed(Operation.PERSONAL_COLOR_MALE_INPUT, scaledHsvTensor)
                        .fetch(Operation.PERSONAL_COLOR_OUTPUT)
                        .run().get(0);
            } else if (request.getSex().equals("female")) {
                result = (TFloat32) personalColorFemaleModel.session().runner()
                        .feed(Operation.PERSONAL_COLOR_FEMALE_INPUT, scaledHsvTensor)
                        .fetch(Operation.PERSONAL_COLOR_OUTPUT)
                        .run().get(0);
            } else {
                throw new BadRequestException("성별을 선택해주세요.");
            }
            float[] resultArray = getFloats(result, 4);

            log.info("Personal Color model output: {}", Arrays.toString(resultArray));
            int maxIndex = argMax(resultArray);
            resultLabel = personalColorLabels[maxIndex];

            updatePersonalColorCsvFile(maxIndex, filePath, hsvArrayInt[0], request.getSex());

        } else {
            throw new BadRequestException("TROUBLE, TYPE, PERSONAL_COLOR 중 하나를 선택해주세요.");
        }

        // DB에 저장
        Member findMember = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new UserNotFound(request.getMemberId() + " 회원을 찾을 수 없음"));

        String finalResultLabel = resultLabel;
        Skin findSkin = skinRepository.findByResult(resultLabel).orElseThrow(() -> new UserNotFound(finalResultLabel + " is not found"));

        Diagnosis diagnosisResult = Diagnosis.builder()
                .member(findMember)
                .skin(findSkin)
                .skinPictureUrl(filePath)
                .build();

        diagnosisRepository.save(diagnosisResult);

        return new DiagnosisResponse(resultLabel);
    }

    private static float[] getFloats(TFloat32 result, int x) {
        FloatNdArray floatNdArray = NdArrays.ofFloats(result.shape());

        log.info("floatNdArray: {}", floatNdArray.shape());
        log.info("floatNdArray: {}", floatNdArray);

        result.copyTo(floatNdArray);
        float[] resultArray = new float[x];
        floatNdArray.scalars().forEachIndexed((idx, flt) -> {
                    log.info("idx: {}", idx);
                    log.info("resultArray: {}", flt.getFloat());
                    resultArray[(int) idx[1]] = flt.getFloat();
                }
        );
        return resultArray;
    }

    private double[][][] detectFaceForPersonalColor(String filePath, String sex) throws Exception {
        log.info("얼굴 인식 시작");
        ProcessBuilder pb;
        if (sex.equals("male")) {
            pb = new ProcessBuilder("python3", detectMaleFacePythonPath, filePath);
        } else if (sex.equals("female")) {
            pb = new ProcessBuilder("python3", detectFemaleFacePythonPath, filePath);
        } else {
            throw new BadRequestException("성별을 선택해주세요.");
        }
        pb.redirectErrorStream(true);

        Process process = pb.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
        } catch (IOException e) {
            log.error("얼굴 인식 실패");
            throw new PythonScriptException("얼굴인식 실패");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            log.error("파이썬 에러 로그" + output);
            throw new PythonScriptException("Python script execution failed with exit code: " + exitCode);
        }

        String jsonOutput = output.toString();
        log.info("얼굴 인식 결과: {}", jsonOutput);
        return parseJsonTo2DArray(jsonOutput);
    }

    private static double[][][] parseJsonTo2DArray(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        double[][][] doubles = objectMapper.readValue(json, double[][][].class);
        log.info("얼굴 인식 결과Json 변환: {}", Arrays.deepToString(doubles));
        return doubles;
    }

    private void whiteBalance(String filePath) throws IOException, InterruptedException {

        log.info("화이트 밸런스 조정 시작");
        ProcessBuilder pb = new ProcessBuilder("python3", whiteBalancePythonPath, filePath);
        pb.redirectErrorStream(true);

        Process p = pb.start();
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            log.info("화이트 밸런스 조정 로그: {}", sb);
            log.info("화이트 밸런스 조정 완료");
        }

        int exitCode = p.waitFor();
        if (exitCode != 0) {
            log.error("화이트 밸런스 조정 실패");
            throw new RuntimeException("화이트 밸런스 조정 실패");
        }
        // 파일이 성공적으로 생성되었는지 확인
        File outputFile = new File(filePath);
        if (!outputFile.exists()) {
            log.error("화이트 밸런스 조정 후 파일이 존재하지 않습니다: {}", filePath);
            throw new RuntimeException("화이트 밸런스 조정 후 파일이 존재하지 않습니다.");
        }
    }

    private String saveFile(DiagnosisDto request) throws BadRequestException {

        if (request.getFile().isEmpty()) {
            throw new FileNotFound("업로드된 파일이 없습니다.");
        }

        // 이미지 파일이 아닐 경우 예외
        List<String> allowedContentType = Arrays.asList("png", "jpeg", "jpg");
        if (!allowedContentType.contains(Objects.requireNonNull(request.getFile().getOriginalFilename()).split("\\.")[1]))
            throw new MissMatchType("이미지 파일만 업로드 가능합니다.");

        MultipartFile file = request.getFile();
        String originalFilename = file.getOriginalFilename();

        // 달 별로 폴더 없으면 새로 생성
        Path path = getPath(request.getKind(), request.getSex());
        createDirIfNotExist(path);

        String modifiedFilename = UUID.randomUUID() + originalFilename;
        Path uploadPath = Paths.get(path.toString(), modifiedFilename);

        // 416 * 416 이미지로 변환 및 저장
        if (!(request.getKind().equals(Kind.PERSONAL_COLOR))) {
            changImageSize(file, uploadPath);
        } else {
            try {
                file.transferTo(uploadPath);
            } catch (IOException e) {
                throw new FileNotFound("이미지 업로드 실패");
            }
        }
        return uploadPath.toString();
    }

    private int argMax(float[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
    // 이미지 전처리

    private TFloat32 preprocessImage(BufferedImage img) {
        log.info("이미지 전처리 시작");
        int width = 224;
        int height = 224;

        // 이미지 크기를 변경합니다.
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(img, 0, 0, width, height, null);
        g2d.dispose();

        // 정규화를 위해 float 배열을 준비합니다.
        float[][][][] imageArray = new float[1][height][width][3];  // 변경: 4차원 배열로 수정
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = resizedImage.getRGB(x, y);
                imageArray[0][y][x][0] = ((rgb >> 16) & 0xFF) / 255.0f; // Red
                imageArray[0][y][x][1] = ((rgb >> 8) & 0xFF) / 255.0f;  // Green
                imageArray[0][y][x][2] = (rgb & 0xFF) / 255.0f;         // Blue
            }
        }

        // 평균과 표준편차를 계산하여 정규화
        float mean = 0f;
        float std = 0f;
        int count = width * height * 3;
        for (int b = 0; b < 1; b++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    for (int c = 0; c < 3; c++) {
                        mean += imageArray[b][y][x][c];
                    }
                }
            }
        }
        mean /= count;

        for (int b = 0; b < 1; b++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    for (int c = 0; c < 3; c++) {
                        std += (float) Math.pow(imageArray[b][y][x][c] - mean, 2);
                    }
                }
            }
        }
        std = (float) Math.sqrt(std / count);

        // 정규화 적용
        for (int b = 0; b < 1; b++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    for (int c = 0; c < 3; c++) {
                        imageArray[b][y][x][c] = (imageArray[b][y][x][c] - mean) / std;
                    }
                }
            }
        }
        log.info("이미지 전처리 완료");

        // 배열을 TFloat32 텐서로 변환합니다.
        return TFloat32.tensorOf(StdArrays.ndCopyOf(imageArray));
    }


    private Path getPath(Kind kind, String sex) throws BadRequestException {
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        String day = String.format("%02d", now.getDayOfMonth());

        if (kind.equals(Kind.TYPE)) {
            return Paths.get(uploadDir, "skintype", "customers", year, month, day);
        } else if (kind.equals(Kind.TROUBLE)) {
            return Paths.get(uploadDir, "skintrouble", "customers", year, month, day);
        } else if (kind.equals(Kind.PERSONAL_COLOR)) {
            if(sex.equals("male")) {
                return Paths.get(uploadDir, "personal_color", "male", "customers", year, month, day);
            } else if (sex.equals("female")) {
                return Paths.get(uploadDir, "personal_color", "female", "customers", year, month, day);
            } else {
                throw new BadRequestException("성별을 선택해주세요.");
            }
        } else {
            throw new MissMatchType("TROUBLE, TYPE, PERSONAL_COLOR 중 하나를 선택해주세요.");
        }
    }

    private static void createDirIfNotExist(Path path) {
        try {
            if (!path.toFile().exists()) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create directory", e);
        }
    }

    private static void changImageSize(MultipartFile file, Path uploadPath) {
        try {
            Thumbnails.of(file.getInputStream())
                    .size(416, 416)
                    .toFile(uploadPath.toFile());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot save file", e);
        }
    }

    private void updateTypeCsvFile(int labelIndex, String filePath) {
        Path csvFile = getTypeCsvFilePath();

        isAlreadyExist(labelIndex, filePath, csvFile);

    }

    private void updateTroubleCsvFile(int labelIndex, String filePath) {
        Path csvFile = getTroubleCsvFilePath();

        isAlreadyExist(labelIndex, filePath, csvFile);

    }

    private void updatePersonalColorCsvFile(int labelIndex, String filePath, int[] hsvArray, String sex) throws BadRequestException {
        Path csvFile = getPersonalColorCsvFilePath(sex);

        isAlreadyExist(labelIndex, filePath, csvFile, hsvArray);
    }

    private void isAlreadyExist(int labelIndex, String filePath, Path csvFile) {
        boolean fileAlreadyExists = Files.exists(csvFile);

        CSVFormat format = getCsvFormat(fileAlreadyExists);

        try (BufferedWriter writer = Files.newBufferedWriter(csvFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
             CSVPrinter csvPrinter = new CSVPrinter(writer, format)) {
            csvPrinter.printRecord(filePath, labelIndex);
            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException("csv 저장 실패");
        }
    }

    private void isAlreadyExist(int labelIndex, String filePath, Path csvFile, int[] hsvArray) {
        boolean fileAlreadyExists = Files.exists(csvFile);

        CSVFormat format = getCsvFormat(fileAlreadyExists);

        try (BufferedWriter writer = Files.newBufferedWriter(csvFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
             CSVPrinter csvPrinter = new CSVPrinter(writer, format)) {
            csvPrinter.printRecord(filePath, hsvArray[0], hsvArray[1], hsvArray[2], hsvArray[3],
                    hsvArray[4], hsvArray[5], hsvArray[6], hsvArray[7], hsvArray[8], labelIndex);
            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException("csv 저장 실패");
        }
    }

    private static CSVFormat getCsvFormat(boolean fileAlreadyExists) {
        return fileAlreadyExists ?
                CSVFormat.DEFAULT :
                CSVFormat.DEFAULT.builder().build();
    }

    private Path getTypeCsvFilePath() {
        // 폴더가 존재하지 않으면 생성
        typeMakeDirIfNotExist();

        return Paths.get(uploadDir, DfPath.TYPE_PATH);
    }

    private Path getTroubleCsvFilePath() {
        // 폴더가 존재하지 않으면 생성
        troubleMakeDirIfNotExist();

        return Paths.get(uploadDir, DfPath.TROUBLE_PATH);
    }

    private Path getPersonalColorCsvFilePath(String sex) throws BadRequestException {
        // 폴더가 존재하지 않으면 생성
        personalColorMakeDirIfNotExist();

        if(sex.equals("male")) {
            return Paths.get(uploadDir, DfPath.PERSONAL_COLOR_MALE_PATH);
        } else if (sex.equals("female")) {
            return Paths.get(uploadDir, DfPath.PERSONAL_COLOR_FEMALE_PATH);
        }else {
            throw new BadRequestException("성별을 선택해주세요.");
        }
    }

    private void typeMakeDirIfNotExist() {
        Path path = Paths.get(uploadDir, "skintype");
        createDirIfNotExist(path);
    }

    private void troubleMakeDirIfNotExist() {
        Path path = Paths.get(uploadDir, "skintrouble");
        createDirIfNotExist(path);
    }

    private void personalColorMakeDirIfNotExist() {
        Path femalePath = Paths.get(uploadDir, "personal_color" + File.separator + "female");
        Path malePath = Paths.get(uploadDir, "personal_color" + File.separator + "male");
        createDirIfNotExist(femalePath);
        createDirIfNotExist(malePath);
    }

}