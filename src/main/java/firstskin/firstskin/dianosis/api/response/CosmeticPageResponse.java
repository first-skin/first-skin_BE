package firstskin.firstskin.dianosis.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class CosmeticPageResponse {

    private boolean last;
    private int totalPages;

    private int number;
    private boolean first;
    private int numberOfElements;

    private long totalElement;
    private int size;
    private int display;

    private List<CosmeticResponse> content;

    @Builder
    public CosmeticPageResponse(long total, int size, int start, int display, List<CosmeticResponse> content) {
        this.last = ((start + display) >= total) || start == 100;
        this.totalPages = (int) Math.ceil((double) total / display);
        this.number = start / display;
        this.first = start == 1;
        this.numberOfElements = content.size();
        this.totalElement = total;
        this.size = size;
        this.display = display;
        this.content = content;
    }
}
