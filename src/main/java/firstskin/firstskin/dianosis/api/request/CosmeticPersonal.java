package firstskin.firstskin.dianosis.api.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Slf4j
public class CosmeticPersonal {

    private Integer size;
    private String sort;
    private Integer start;
    private String category;

    public CosmeticPersonal(String category, Integer page, Integer size, String sort) {
        log.info("화장품 personal 검색 category : {}", category);
        size = size == null ? 10 : size;
        this.category = category == null ? "" : category;
        this.size = size;
        this.sort = sort;
        this.start = Math.max((page == null ? 1 : page - 1) * size + 1, 1);
    }
}
