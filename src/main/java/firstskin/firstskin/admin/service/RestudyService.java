package firstskin.firstskin.admin.service;

import firstskin.firstskin.admin.api.dto.request.RestudyRequest;
import firstskin.firstskin.admin.api.dto.response.RestudyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class RestudyService {

    @Value("${python.restudy_path}")
    private String restudyPath;

    public RestudyResponse restudy(RestudyRequest request) {
        log.info("{} 재학습 시작, 모델 경로: {}, df 경로: {}",
                request.getKind(), request.getModelPath(), request.getDfPath());

        List<String> logLines = new LinkedList<>();
        StringBuilder completeLog = new StringBuilder();
        String modelSavedPath = "";

        try {
            ProcessBuilder pb = new ProcessBuilder("python3", restudyPath,
                    request.getModelPath(), request.getDfPath(), request.getKind().toString());
            pb.redirectErrorStream(true);

            log.info("재학습 명령어: {}", pb.command());

            Process p = pb.start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    completeLog.append(line).append("\n");

                    logLines.add(line);

                    // 모델 저장 경로 추출
                    if (line.contains("Model saved as .pb in: ")) {
                        modelSavedPath = line.substring(line.indexOf("Model saved as .pb in: ") + 22).trim();
                    }
                }
            }

            int exitCode = p.waitFor();
            if (exitCode != 0) {
                throw new InternalError("재학습 실패. 파이썬 스크립트에서 오류가 발생했습니다. exitCode: " + exitCode);
            }

        } catch (IOException | InterruptedException exception) {
            log.error("재학습 실패", exception);
            throw new InternalError("서버 오류로 인한 재학습 실패.");
        } finally {
            log.info("재학습 전체 로그: \n{}", completeLog);
            log.info("재학습 종료");
        }

        // 로그에서 before, after 값을 추출하고 결과 메시지 생성
        Double before = null;
        Double after = null;
        StringBuilder result = new StringBuilder();

        // 마지막으로 유효한 두 줄을 사용하여 성능 결과를 추출
        if (logLines.size() >= 2) {
            try {
                // 로그의 마지막 두 줄이 성능 평가 결과일 수 있으므로 확인
                for (String line : logLines) {
                    if (line.contains("before") && line.contains("after")) {
                        String[] parts = line.split(",");
                        for (String part : parts) {
                            if (part.trim().startsWith("before")) {
                                before = Double.parseDouble(part.split(":")[1].trim());
                            } else if (part.trim().startsWith("after")) {
                                after = Double.parseDouble(part.split(":")[1].trim());
                            }
                        }
                    } else if (line.contains("성능 향상") || line.contains("성능 하락")) {
                        result = new StringBuilder(line.trim());
                    }else if (line.contains("Model saved as .pb in: ")){
                        String[] parts = line.split("/");
                        result.append(parts[parts.length - 1]);
                    }
                }


            } catch (Exception e) {
                log.error("로그 파싱 실패", e);
                throw new InternalError("로그 파싱 실패.");
            }
        }

        log.info("before: {}, after: {}, result: {}", before, after, result.toString());

        return RestudyResponse.builder()
                .before(before)
                .after(after)
                .result(result.toString())
                .build();
    }
}
