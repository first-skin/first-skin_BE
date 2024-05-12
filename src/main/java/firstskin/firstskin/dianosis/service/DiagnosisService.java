package firstskin.firstskin.dianosis.service;

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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final String[] labels = {"dry", "normal", "oily"};

    private final DiagnosisRepository diagnosisRepository;
    private final MemberRepository memberRepository;
    private final SkinRepository skinRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${model.type}")
    private String typeModelPath;

    private SavedModelBundle typeModel;

    @PostConstruct
    public void init() {
        typeModel = SavedModelBundle.load(typeModelPath, "serve");
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
            resultLabel = labels[maxIndex];

        } else if (request.getKind().equals(Kind.TROUBLE)) {
            // 피부 트러블 진단
            return null;
        } else if (request.getKind().equals(Kind.PERSONAL_COLOR)) {
            // 퍼스널 컬러 진단
            return null;
        } else {
            throw new IllegalStateException("Invalid kind");
        }

        // DB에 저장
        Member findMember = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalStateException(request.getMemberId() + " is not found"));

        Skin findSkin = skinRepository.findByResult(resultLabel).orElseThrow(() -> new IllegalStateException(resultLabel + " is not found"));

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
            throw new IllegalStateException("업로드된 파일이 없습니다.");
        }

        // 이미지 파일이 아닐 경우 예외
        if (!Objects.requireNonNull(request.getFile().getContentType()).startsWith("image")) {
            throw new IllegalStateException("이미지 파일이 아닙니다. contentType: " + request.getFile().getContentType());
        }

        MultipartFile file = request.getFile();
        String originalFilename = file.getOriginalFilename();

        // 달 별로 폴더 없으면 새로 생성
        Path path = getPath();
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


    private Path getPath() {
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        String day = String.format("%02d", now.getDayOfMonth());

        return Paths.get(uploadDir, year, month, day);
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
}