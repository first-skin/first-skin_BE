package firstskin.firstskin.dianosis.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CosmeticResponse {

    private final String title;
    private final String link;
    private final String image;
    private final int lprice;
    private final long productId;
    private final String brand;

    private Double score;

    @Builder
    public CosmeticResponse(String title, String link, String image, int lprice, long productId, String brand, Double score) {
        this.title = title;
        this.link = link;
        this.image = image;
        this.lprice = lprice;
        this.productId = productId;
        this.brand = brand;
        this.score = score;
    }
}
