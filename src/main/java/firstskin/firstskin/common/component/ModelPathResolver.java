package firstskin.firstskin.common.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class ModelPathResolver {

    // 각 모델 경로 및 패턴은 application.yml에서 설정
    @Value("${model.type}")
    private String typePath;

    @Value("${model.type_pattern}")
    private String typePattern;

    @Value("${model.trouble}")
    private String troublePath;

    @Value("${model.trouble_pattern}")
    private String troublePattern;

    @Value("${model.personal_color_male}")
    private String personalColorMalePath;

    @Value("${model.personal_color_male_pattern}")
    private String personalColorMalePattern;

    @Value("${model.personal_color_female}")
    private String personalColorFemalePath;

    @Value("${model.personal_color_female_pattern}")
    private String personalColorFemalePattern;


    public String resolveModelPath(String basePath, String pattern) {
        File dir = new File(basePath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalStateException("Directory does not exist: " + basePath);
        }
        Pattern regex = Pattern.compile(pattern.replace("*", ".*"));
        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(file -> regex.matcher(file.getName()).matches())
                .max(Comparator.comparingLong(File::lastModified))
                .map(File::getAbsolutePath)
                .orElseThrow(() -> new IllegalStateException("No model file found matching pattern: " + pattern));
    }

    // 각 모델 타입에 대한 특정 메소드를 제공하지 않고, 각 설정에 맞는 경로 해석을 수행
    public String resolveTypeModelPath() {
        return resolveModelPath(typePath, typePattern);
    }

    public String resolveTroubleModelPath() {
        return resolveModelPath(troublePath, troublePattern);
    }

    public String resolvePersonalColorMaleModelPath() {
        return resolveModelPath(personalColorMalePath, personalColorMalePattern);
    }
    public String resolvePersonalColorFemaleModelPath() {
        return resolveModelPath(personalColorFemalePath, personalColorFemalePattern);
    }
}
