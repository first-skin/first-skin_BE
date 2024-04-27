package firstskin.firstskin.dianosis.api.request;

import firstskin.firstskin.skin.Kind;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CosmeticRequest {

    private final String query;

    private final Integer size; // 한 번에 표시할 검색 결과 수(1~100) display와 동일
    private final String sort; // 검색 결과 정렬 방법 (sim:정확도순, date:날짜순, asc:가격오름차순, dsc:가격내림차순)
    private final Integer start; // 검색 시작 위치

    @Builder
    public CosmeticRequest(Kind kind, String category, String query, Integer page, Integer size, String sort) {
        this.query = kind.getDescription() + ":" + query + "%20" + category;
        this.size = size;
        this.sort = sort;
        this.start = (page - 1) * size + 1;
    }
}
