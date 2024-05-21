package firstskin.firstskin.dianosis.service;

import firstskin.firstskin.common.exception.FileNotFound;
import firstskin.firstskin.common.exception.MissMatchType;
import firstskin.firstskin.common.exception.UserNotFound;
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
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final String[] typeLabels = {"dry", "normal", "oily"};
    private final String[] troubleLabels = {"normal", "trouble"};

    private final DiagnosisRepository diagnosisRepository;
    private final MemberRepository memberRepository;
    private final SkinRepository skinRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${model.type}")
    private String typeModelPath;

    @Value("${model.trouble}")
    private String troubleModelPath;

    @Value("${model.detect_trouble}")
    private String detectTroubleModelPath;

    private SavedModelBundle typeModel;
    private SavedModelBundle troubleModel;
    private SavedModelBundle detectTroubleModel;

    @PostConstruct
    public void init() {
        typeModel = SavedModelBundle.load(typeModelPath, "serve");
        troubleModel = SavedModelBundle.load(troubleModelPath, "serve");
        detectTroubleModel = SavedModelBundle.load(detectTroubleModelPath, "serve");
    }


    public DiagnosisResponse diagnosisSkin(DiagnosisDto request) throws IOException {

        String filePath = saveFile(request);

        BufferedImage img = ImageIO.read(Paths.get(filePath).toFile());
        TFloat32 preprocessedImage = preprocessImage(img);

        TFloat32 result;

        String resultLabel;
        // TensorFlow 진단

        if (request.getKind().equals(Kind.TYPE)) {
            // 피부 타입 진단
            result = (TFloat32) typeModel.session().runner()
                    .feed("serving_default_input_17", preprocessedImage)
                    .fetch("StatefulPartitionedCall:0")
                    .run().get(0);

            FloatNdArray floatNdArray = NdArrays.ofFloats(result.shape());

            result.copyTo(floatNdArray);
            float[] resultArray = new float[3];
            floatNdArray.scalars().forEachIndexed((idx, flt) -> resultArray[(int) idx[1]] = flt.getFloat());

            int maxIndex = argMax(resultArray);
            resultLabel = typeLabels[maxIndex];

            updateTypeCsvFile(maxIndex, filePath);

        } else if (request.getKind().equals(Kind.TROUBLE)) {
            // 피부 트러블 진단
            result = (TFloat32) troubleModel.session().runner()
                    .feed("serving_default_input_6", preprocessedImage)
                    .fetch("StatefulPartitionedCall:0")
                    .run().get(0);

            FloatNdArray floatNdArray = NdArrays.ofFloats(result.shape());
            result.copyTo(floatNdArray);
            float[] resultArray = new float[2];
            floatNdArray.scalars().forEachIndexed((idx, flt) -> resultArray[(int) idx[1]] = flt.getFloat());

            int maxIndex = argMax(resultArray);
            resultLabel = troubleLabels[maxIndex];

            if (resultLabel.equals("trouble")) {
                // 트러블이면 여드름/홍조 구분
                TFloat32 troubleResultDetail = (TFloat32) detectTroubleModel.session().runner()
                        .feed("serving_default_input_6", preprocessedImage)
                        .fetch("StatefulPartitionedCall:1")
                        .run().get(0);

                FloatNdArray troubleDetailFloatNdArray = NdArrays.ofFloats(troubleResultDetail.shape());
                troubleResultDetail.copyTo(troubleDetailFloatNdArray);
                float[] troubleDetailResultArray = new float[2];
                troubleDetailFloatNdArray.scalars().forEachIndexed((idx, flt) -> troubleDetailResultArray[(int) idx[1]] = flt.getFloat());

                int detectTroubleMaxIndex = argMax(troubleDetailResultArray);
                resultLabel = detectTroubleMaxIndex == 0 ? "acne" : "redness";

                // 트러블 위치 진단
                TFloat32 detectTroubleResult = (TFloat32) detectTroubleModel.session().runner()
                        .feed("serving_default_input_6", preprocessedImage)
                        .fetch("StatefulPartitionedCall:0")
                        .run().get(0);

                FloatNdArray detectTroubleFloatNdArray = NdArrays.ofFloats(detectTroubleResult.shape());
                detectTroubleResult.copyTo(detectTroubleFloatNdArray);
                float[] detectTroubleResultArray = new float[4];
                detectTroubleFloatNdArray.scalars().forEachIndexed((idx, flt) -> detectTroubleResultArray[(int) idx[1]] = flt.getFloat());

//                filePath = runPythonScript(filePath, detectTroubleH5ModelPath);
            }

            updateTroubleCsvFile(maxIndex, filePath);

        } else if (request.getKind().equals(Kind.PERSONAL_COLOR)) {
            // 퍼스널 컬러 진단
            throw new BadRequestException("퍼스널 컬러 진단은 준비 중입니다.");
        } else {
            throw new BadRequestException("TROUBLE, TYPE, PERSONAL_COLOR 중 하나를 선택해주세요.");
        }

        // DB에 저장
        Member findMember = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new UserNotFound(request.getMemberId() + " 회원을 찾을 수 없음"));

        String finalResultLabel = resultLabel;
        Skin findSkin = skinRepository.findByResult(resultLabel).orElseThrow(() -> new IllegalStateException(finalResultLabel + " is not found"));

        Diagnosis diagnosisResult = Diagnosis.builder()
                .member(findMember)
                .skin(findSkin)
                .skinPictureUrl(filePath)
                .build();

        diagnosisRepository.save(diagnosisResult);

        return new DiagnosisResponse(resultLabel);
    }

    private String saveFile(DiagnosisDto request) {

        if (request.getFile().isEmpty()) {
            throw new FileNotFound("업로드된 파일이 없습니다.");
        }

        // 이미지 파일이 아닐 경우 예외
        List<String> allowedContentType = Arrays.asList("png", "jpeg", "jpg");
        if(!allowedContentType.contains(request.getFile().getOriginalFilename().split("\\.")[1]))
            throw new MissMatchType("이미지 파일만 업로드 가능합니다.");

        MultipartFile file = request.getFile();
        String originalFilename = file.getOriginalFilename();

        // 달 별로 폴더 없으면 새로 생성
        Path path = getPath(request.getKind());
        createDirIfNotExist(path);

        String modifiedFilename = UUID.randomUUID() + originalFilename;
        Path uploadPath = Paths.get(path.toString(), modifiedFilename);

        // 416 * 416 이미지로 변환 및 저장
        changImageSize(file, uploadPath);

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

        // 배열을 TFloat32 텐서로 변환합니다.
        return TFloat32.tensorOf(StdArrays.ndCopyOf(imageArray));
    }


    private Path getPath(Kind kind) {
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        String day = String.format("%02d", now.getDayOfMonth());

        if (kind.equals(Kind.TYPE)) {
            return Paths.get(uploadDir, "skintype", "customers", year, month, day);
        } else if (kind.equals(Kind.TROUBLE)) {
            return Paths.get(uploadDir, "skintrouble", "customers", year, month, day);
        } else {
            throw new MissMatchType("TROUBLE, TYPE 중 하나를 선택해주세요.");
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

    private void isAlreadyExist(int labelIndex, String filePath, Path csvFile) {
        boolean fileAlreadyExists = Files.exists(csvFile);

        CSVFormat format = fileAlreadyExists ?
                CSVFormat.DEFAULT :
                CSVFormat.DEFAULT.builder().build();

        try (BufferedWriter writer = Files.newBufferedWriter(csvFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
             CSVPrinter csvPrinter = new CSVPrinter(writer, format)) {
            csvPrinter.printRecord(filePath, labelIndex);
            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException("csv 저장 실패");
        }
    }

    private Path getTypeCsvFilePath() {
        // 폴더가 존재하지 않으면 생성
        typeMakeDirIfNotExist();

        return Paths.get(uploadDir, "skintype/skintype_df.csv");
    }

    private Path getTroubleCsvFilePath() {
        // 폴더가 존재하지 않으면 생성
        troubleMakeDirIfNotExist();

        return Paths.get(uploadDir, "skintrouble/skintrouble_df.csv");
    }

    private void typeMakeDirIfNotExist() {
        Path path = Paths.get(uploadDir, "skintype");
        createDirIfNotExist(path);
    }

    private void troubleMakeDirIfNotExist() {
        Path path = Paths.get(uploadDir, "skintrouble");
        createDirIfNotExist(path);
    }

//    // 파이썬 스크립트 실행
//    private String runPythonScript(String imagePath, String modelPath) {
//        String path = uploadDir + "detect_trouble/";
//        String pythonScriptPath = path + "detect_trouble.py";
//        String outputImagePath = generateOutputImagePath(imagePath);
//        StringBuilder result = new StringBuilder();
//
//        try {
//            // 가상 환경 경로 설정
//            String venvPath = "/Users/wonu/Desktop/t24122/myenv";  // Linux/MacOS
//
//            // 가상 환경을 활성화하고 Python 스크립트를 실행하는 명령어
//            String[] command = {
//                    "/bin/bash", "-c",
//                    "source " + venvPath + "/bin/activate && python " + pythonScriptPath + " " + imagePath + " " + modelPath + " " + outputImagePath
//            };
//
//            // ProcessBuilder를 사용하여 명령어 실행
//            ProcessBuilder processBuilder = new ProcessBuilder(command);
//            processBuilder.redirectErrorStream(true);
//            Process process = processBuilder.start();
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                result.append(line).append("\n");
//            }
//
//            int exitCode = process.waitFor();
//            result.append("Exited with code: ").append(exitCode);
//
//            if (exitCode != 0) {
//                System.err.println(result.toString());  // 에러 메시지 출력
//                throw new RuntimeException("Python script execution failed with exit code " + exitCode);
//            }
//
//        } catch (IOException | InterruptedException e) {
//            throw new RuntimeException("Failed to run Python script", e);
//        }
//
//
//        return outputImagePath;
//    }
//
//    private String generateOutputImagePath(String imagePath) {
//        int dotIndex = imagePath.lastIndexOf('.');
//        if (dotIndex == -1) {
//            throw new IllegalArgumentException("Invalid image file path: " + imagePath);
//        }
//        String base = imagePath.substring(0, dotIndex);
//        String extension = imagePath.substring(dotIndex);
//        return base + "_detected" + extension;
//    }
}