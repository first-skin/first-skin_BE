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
    public CosmeticPersonalRequest(String type, String personalColor, String trouble, String category, Integer page, Integer size, String sort) {
        size = size == null ? 10 : size;
        this.query = "피부타입:" + type + "%20" + "퍼스널컬러:" + personalColor + "%20" + "트러블:" + trouble + "%20" + category;
        this.size = size;
        this.sort = sort;
        this.start = Math.max((page == null ? 1 : page - 1) * size + 1, 1);
    }
}