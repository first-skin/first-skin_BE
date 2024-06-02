package firstskin.firstskin.dianosis.api.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CosmeticPersonal {

    private Integer size;
    private String sort;
    private Integer start;
    private String category;

    public CosmeticPersonal(String category, Integer page, Integer size, String sort) {
        size = size == null ? 10 : size;
        this.category = category == null ? "" : category;
        this.size = size;
        this.sort = sort;
        this.start = Math.max((page == null ? 1 : page - 1) * size + 1, 1);
    }
}
