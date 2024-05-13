package firstskin.firstskin.dianosis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import firstskin.firstskin.dianosis.api.request.CosmeticRequest;
import firstskin.firstskin.dianosis.api.response.CosmeticPageResponse;
import firstskin.firstskin.dianosis.api.response.CosmeticResponse;
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

    public CosmeticPageResponse searchCosmetics(CosmeticRequest request) throws JsonProcessingException {

        ResponseEntity<String> exchange = getStringResponseEntity(request);
//        log.info("네이버 쇼핑 API 응답 === {}", exchange.getBody());

        // DTO로 변환
        JsonNode jsonNode = objectMapper.readTree(exchange.getBody());
        JsonNode items = jsonNode.path("items");

        List<CosmeticResponse> cosmeticResponses = objectMapper.readValue(items.toString(), new TypeReference<>() {
        });

        CosmeticPageResponse response = CosmeticPageResponse.builder()
                .total(jsonNode.path("total").asLong())
                .size(request.getSize() == null ? 10 : request.getSize())
                .start(jsonNode.path("start").asInt())
                .display(jsonNode.path("display").asInt())
                .content(cosmeticResponses)
                .build();

        log.info("화장품 검색 결과 === {}", response);
        return response;

    }

    private ResponseEntity<String> getStringResponseEntity(CosmeticRequest request) {
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

    private ResponseEntity<String> getStringResponseEntity(URI uri) {

        RequestEntity<Void> req = RequestEntity.get(uri)
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        RestTemplate restTemplate = new RestTemplate();


        return restTemplate.exchange(req, String.class);
    }
}
