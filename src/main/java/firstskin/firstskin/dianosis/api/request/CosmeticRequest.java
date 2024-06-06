package firstskin.firstskin.dianosis.api.request;

import firstskin.firstskin.skin.Kind;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static firstskin.firstskin.skin.Kind.PERSONAL_COLOR;

@Getter
@ToString
@Slf4j
public class CosmeticRequest {

    private final String query;
    private final Kind kind;
    private final Integer size; // 한 번에 표시할 검색 결과 수(1~100) display와 동일
    private final String sort; // 검색 결과 정렬 방법 (sim:정확도순, date:날짜순, asc:가격오름차순, dsc:가격내림차순)
    private final Integer start; // 검색 시작 위치

    @Builder
    public CosmeticRequest(Kind kind, String category, String query, Integer page, Integer size, String sort) {
        log.info("화장품 검색 kind : {}, category : {}, query : {}", kind, category, query);
        size = size == null ? 10 : size;
        this.kind = kind;
        String kindName = kind == null ? "" : kind.getDescription() + ":";
        if (kind == PERSONAL_COLOR) {
            kindName = "";
        }
        this.query = kindName + query + "%20" + category + "%20화장품/미용";
        this.size = size;
        this.sort = sort;
        this.start = Math.max((page == null ? 1 : page - 1) * size + 1, 1);
    }
}
