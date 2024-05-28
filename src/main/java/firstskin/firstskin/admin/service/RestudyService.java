package firstskin.firstskin.admin.service;

import firstskin.firstskin.admin.api.dto.request.RestudyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
@Slf4j
public class RestudyService {

    @Value("${python.restudy_path}")
    private String restudyPath;

    public void restudy(RestudyRequest request) {
        log.info("{} 재학습 시작, 모델 경로: {}, df 경로: {}", request.getKind(), request.getModelPath(), request.getDfPath());

        try {
            ProcessBuilder pb = new ProcessBuilder("python3", restudyPath, request.getModelPath(), request.getDfPath(), request.getKind().toString());
            pb.redirectErrorStream(true);

            log.info("재학습 명령어: {}", pb.command());

            Process p = pb.start();
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    log.info("재학습 로그: {}", line);
                    sb.append(line).append("\n");
                }
            }

            log.info("재학습 결과: {}", sb);

            int exitCode = p.waitFor();
            if (exitCode != 0) {
                throw new InternalError("재학습 실패. 파이썬 스크립트에서 오류가 발생했습니다. exitCode: " + exitCode);
            }

        } catch (IOException | InterruptedException exception) {
            log.error("재학습 실패", exception);
            throw new InternalError("서버 오류로 인한 재학습 실패.");
        }
    }
}
