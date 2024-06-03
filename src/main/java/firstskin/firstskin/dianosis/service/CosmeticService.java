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
import java.util.List;

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

        ResponseEntity<String> exchange = getStringResponseEntity(request);

        // DTO로 변환
        JsonNode jsonNode = objectMapper.readTree(exchange.getBody());
        JsonNode items = jsonNode.path("items");

        List<CosmeticResponse> cosmeticResponses = objectMapper.readValue(items
                .toString(), new TypeReference<>() {
        });

        cosmeticResponses.forEach(cosmeticResponse -> {
            // <b> 태그 제거
            cosmeticResponse.setTitle(removeBoldTags(cosmeticResponse.getTitle()));
            cosmeticResponse.setScore(reviewRepository.findAvgScoreByProductId(cosmeticResponse.getProductId()));
        });

        return CosmeticPageResponse.builder()
                .total(jsonNode.path("total").asLong())
                .size(request.getSize() == null ? 10 : request.getSize())
                .start(jsonNode.path("start").asInt())
                .display(jsonNode.path("display").asInt())
                .content(cosmeticResponses)
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

        // 퍼스널컬러, 타입, 트러블에 맞는 화장품 검색
        ResponseEntity<String> exchange = getStringResponseEntity(CosmeticPersonalRequest.builder()
                .type(type)
                .personalColor(personalColor)
                .trouble(trouble)
                .category(request.getCategory())
                .size(request.getSize())
                .sort(request.getSort())
                .start(request.getStart())
                .build());

        // DTO로 변환
        objectMapper.readTree(exchange.getBody());
        JsonNode items = objectMapper.readTree(exchange.getBody()).path("items");

        List<CosmeticResponse> cosmeticResponses = objectMapper.readValue(items.toString(), new TypeReference<>() {
        });

        cosmeticResponses
                .forEach(cosmeticResponse -> {
                    cosmeticResponse.setTitle(removeBoldTags(cosmeticResponse.getTitle()));
                    cosmeticResponse.setScore(reviewRepository.findAvgScoreByProductId(cosmeticResponse.getProductId()));
                });

        return CosmeticPageResponse.builder()
                .total(objectMapper.readTree(exchange.getBody()).path("total").asLong())
                .size(request.getSize() == null ? 10 : request.getSize())
                .start(objectMapper.readTree(exchange.getBody()).path("start").asInt())
                .display(objectMapper.readTree(exchange.getBody()).path("display").asInt())
                .content(cosmeticResponses)
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
