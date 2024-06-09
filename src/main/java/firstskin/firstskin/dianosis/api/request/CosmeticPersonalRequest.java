package firstskin.firstskin.dianosis.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CosmeticPersonalRequest {

    private String query;
    private Integer size; // 한 번에 표시할 검색 결과 수(1~100) display와 동일
    private String sort; // 검색 결과 정렬 방법 (sim:정확도순, date:날짜순, asc:가격오름차순, dsc:가격내림차순)
    private Integer start; // 검색 시작 위치

    @Builder
    public CosmeticPersonalRequest(String type, String personalColor, String trouble, String category, Integer start, Integer size, String sort) {
        size = size == null ? 10 : size;
        type = type == null ? "" : "피부타입: " + type + "%20";
        personalColor = personalColor == null ? "" : personalColor + "%20";
        trouble = trouble == null ? "" : "트러블: " + trouble + "%20";
        category = category == null || category.isEmpty() ? "" : category + "%20";
        this.query = type + personalColor + trouble + category + "화장품/미용";
        this.size = size;
        this.sort = sort;
        this.start = start;
    }
}