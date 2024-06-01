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
        log.info("{} 재학습 시작, 모델 경로: {}, df 경로: {}", request.getKind(), request.getModelPath(), request.getDfPath());

        List<String> lastTwoLines = new LinkedList<>();
        StringBuilder completeLog = new StringBuilder();

        try {
            ProcessBuilder pb = new ProcessBuilder("python3", restudyPath, request.getModelPath(), request.getDfPath(), request.getKind().toString());
            pb.redirectErrorStream(true);

            log.info("재학습 명령어: {}", pb.command());

            Process p = pb.start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    completeLog.append(line).append("\n");

                    // 마지막 두 줄을 추적
                    if (lastTwoLines.size() >= 2) {
                        lastTwoLines.remove(0);
                    }
                    lastTwoLines.add(line);
                }
            }

            int exitCode = p.waitFor();
            if (exitCode != 0) {
                throw new InternalError("재학습 실패. 파이썬 스크립트에서 오류가 발생했습니다. exitCode: " + exitCode);
            }

        } catch (IOException | InterruptedException exception) {
            log.error("재학습 실패", exception);
            throw new InternalError("서버 오류로 인한 재학습 실패.");
        }finally {
            log.info("재학습 전체 로그: \n{}", completeLog);
            log.info("재학습 종료");
        }

        // 로그의 마지막 두 줄에서 before, after 값을 추출하고 결과 메시지 생성
        Double before = null;
        Double after = null;
        String result = "";

        if (lastTwoLines.size() >= 2) {
            try {
                String metricsLine = lastTwoLines.get(0);
                String resultLine = lastTwoLines.get(1);

                String[] parts = metricsLine.split(",");
                for (String part : parts) {
                    if (part.trim().startsWith("before")) {
                        before = Double.parseDouble(part.split(":")[1].trim());
                    } else if (part.trim().startsWith("after")) {
                        after = Double.parseDouble(part.split(":")[1].trim());
                    }
                }

                result = resultLine.trim();

            } catch (Exception e) {
                log.error("로그 파싱 실패", e);
                throw new InternalError("로그 파싱 실패.");
            }
        }

        return RestudyResponse.builder()
                .before(before)
                .after(after)
                .result(result)
                .build();
    }
}
