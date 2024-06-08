package firstskin.firstskin.dianosis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import firstskin.firstskin.common.exception.UserNotFound;
import firstskin.firstskin.dianosis.api.request.CosmeticPersonal;
import firstskin.firstskin.dianosis.api.request.CosmeticPersonalRequest;
import firstskin.firstskin.dianosis.api.request.CosmeticRequest;
import firstskin.firstskin.dianosis.api.response.CosmeticPageResponse;
import firstskin.firstskin.dianosis.api.response.CosmeticResponse;
import firstskin.firstskin.dianosis.api.response.PersonalResult;
import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.member.repository.MemberRepository;
import firstskin.firstskin.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CosmeticService {

    @Value("${naver-shopping.url}")
    private String url;
    @Value("${naver-shopping.clientId}")
    private String clientId;
    @Value("${naver-shopping.clientSecret}")
    private String clientSecret;
    private final ObjectMapper objectMapper;

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;

    public CosmeticPageResponse searchCosmetics(CosmeticRequest request) throws JsonProcessingException {
        List<CosmeticResponse> finalCosmeticResponses = new ArrayList<>();
        int currentStart = request.getStart() != null ? request.getStart() : 1;
        int desiredSize = request.getSize() != null ? request.getSize() : 10;

        while (finalCosmeticResponses.size() < desiredSize) {
            // 현재 페이지 요청
            ResponseEntity<String> exchange = getStringResponseEntity(request);

            // DTO로 변환
            JsonNode jsonNode = objectMapper.readTree(exchange.getBody());
            JsonNode items = jsonNode.path("items");

            List<CosmeticResponse> cosmeticResponses = objectMapper.readValue(items.toString(), new TypeReference<>() {});

            // 링크가 있는 상품만 필터링
            List<CosmeticResponse> filteredCosmeticResponses = cosmeticResponses.stream()
                    .filter(cosmeticResponse -> cosmeticResponse.getLink() != null && !cosmeticResponse.getLink().isEmpty())
                    .collect(Collectors.toList());

            // <b> 태그 제거 및 평점 설정
            filteredCosmeticResponses.forEach(cosmeticResponse -> {
                Double avgScoreByProductId = reviewRepository.findAvgScoreByProductId(cosmeticResponse.getProductId());
                if (avgScoreByProductId == null) {
                    avgScoreByProductId = 4.9;
                }else {
                    avgScoreByProductId = (double) Math.round(avgScoreByProductId * 10) / 10.0;
                }
                cosmeticResponse.setTitle(removeBoldTags(cosmeticResponse.getTitle()));
                cosmeticResponse.setScore(avgScoreByProductId);
            });

            // 결과 리스트에 추가
            finalCosmeticResponses.addAll(filteredCosmeticResponses);

            // 다음 페이지로 이동
            currentStart += cosmeticResponses.size(); // 실제 요청한 수만큼 증가
            request.setStart(currentStart);

            // 필요 항목 수를 채웠거나 API가 더 이상 항목을 반환하지 않으면 루프 종료
            if (filteredCosmeticResponses.isEmpty()) {
                break;
            }
        }

        // 원하는 수량만큼 잘라서 반환
        List<CosmeticResponse> limitedResponses = finalCosmeticResponses.stream()
                .limit(desiredSize)
                .collect(Collectors.toList());

        return CosmeticPageResponse.builder()
                .total(limitedResponses.size())
                .size(desiredSize)
                .start(request.getStart())
                .display(limitedResponses.size())
                .content(limitedResponses)
                .build();
    }

    public CosmeticPageResponse searchPersonalCosmetics(Long memberId, CosmeticPersonal request) throws JsonProcessingException {
        Member member = memberRepository.findById(memberId).orElseThrow(UserNotFound::new);

        PersonalResult personalResults = memberRepository.getPersonalResults(member);
        String personalColor;
        String type;
        String trouble;

        if (personalResults == null) {
            personalColor = null;
            type = null;
            trouble = null;
        } else {
            personalColor = personalResults.getPersonalColor();
            type = personalResults.getType();
            trouble = personalResults.getTrouble();
        }

        List<CosmeticResponse> finalCosmeticResponses = new ArrayList<>();
        int currentStart = request.getStart() != null ? request.getStart() : 1;
        int desiredSize = request.getSize() != null ? request.getSize() : 10;

        while (finalCosmeticResponses.size() < desiredSize) {
            // 퍼스널컬러, 타입, 트러블에 맞는 화장품 검색
            ResponseEntity<String> exchange = getStringResponseEntity(CosmeticPersonalRequest.builder()
                    .type(type)
                    .personalColor(personalColor)
                    .trouble(trouble)
                    .category(request.getCategory())
                    .size(request.getSize())
                    .sort(request.getSort())
                    .start(currentStart)
                    .build());

            // DTO로 변환
            JsonNode jsonNode = objectMapper.readTree(exchange.getBody());
            JsonNode items = jsonNode.path("items");

            List<CosmeticResponse> cosmeticResponses = objectMapper.readValue(items.toString(), new TypeReference<>() {});

            // 링크가 있는 상품만 필터링
            List<CosmeticResponse> filteredCosmeticResponses = cosmeticResponses.stream()
                    .filter(cosmeticResponse -> cosmeticResponse.getLink() != null && !cosmeticResponse.getLink().isEmpty())
                    .collect(Collectors.toList());

            // <b> 태그 제거 및 평점 설정
            filteredCosmeticResponses.forEach(cosmeticResponse -> {
                Double avgScoreByProductId = reviewRepository.findAvgScoreByProductId(cosmeticResponse.getProductId());
                if (avgScoreByProductId == null) {
                    avgScoreByProductId = 4.9d;
                }else {
                    avgScoreByProductId = (double) Math.round(avgScoreByProductId * 10) / 10.0;
                }
                cosmeticResponse.setTitle(removeBoldTags(cosmeticResponse.getTitle()));
                cosmeticResponse.setScore(avgScoreByProductId);
            });

            // 결과 리스트에 추가
            finalCosmeticResponses.addAll(filteredCosmeticResponses);

            // 다음 페이지로 이동
            currentStart += cosmeticResponses.size(); // 실제 요청한 수만큼 증가
            request.setStart(currentStart);

            // 필요 항목 수를 채웠거나 API가 더 이상 항목을 반환하지 않으면 루프 종료
            if (filteredCosmeticResponses.isEmpty()) {
                break;
            }
        }

        // 원하는 수량만큼 잘라서 반환
        List<CosmeticResponse> limitedResponses = finalCosmeticResponses.stream()
                .limit(desiredSize)
                .collect(Collectors.toList());

        return CosmeticPageResponse.builder()
                .total(limitedResponses.size())
                .size(desiredSize)
                .start(request.getStart())
                .display(limitedResponses.size())
                .content(limitedResponses)
                .build();
    }

    private ResponseEntity<String> getStringResponseEntity(CosmeticRequest request) {
        URI uri = getUri(request);

        return getStringResponseEntity(uri);
    }

    private ResponseEntity<String> getStringResponseEntity(CosmeticPersonalRequest request) {
        URI uri = getUri(request);

        return getStringResponseEntity(uri);
    }

    private URI getUri(CosmeticRequest request) {

        return UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", request.getQuery())
                .queryParam("display", request.getSize() == null ? 10 : request.getSize())
                .queryParam("start", request.getStart() == null ? 1 : request.getStart())
                .queryParam("sort", request.getSort() == null ? "sim" : request.getSort())
                .build().encode().toUri();
    }

    private URI getUri(CosmeticPersonalRequest request) {

        return UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", request.getQuery())
                .queryParam("display", request.getSize() == null ? 10 : request.getSize())
                .queryParam("start", request.getStart() == null ? 1 : request.getStart())
                .queryParam("sort", request.getSort() == null ? "sim" : request.getSort())
                .build().encode().toUri();
    }

    private ResponseEntity<String> getStringResponseEntity(URI uri) {

        RequestEntity<Void> req = RequestEntity.get(uri)
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.exchange(req, String.class);
    }

    public PersonalResult getPersonalResults(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(UserNotFound::new);
        return memberRepository.getPersonalResults(member);
    }

    private String removeBoldTags(String input) {
        return input.replaceAll("</?b>", "");
    }
}
