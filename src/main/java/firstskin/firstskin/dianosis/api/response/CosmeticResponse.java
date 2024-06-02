package firstskin.firstskin.dianosis.api.response;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CosmeticResponse {

    private String title;
    private String link;
    private String image;
    private int lprice;
    private long productId;
    private String brand;

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

    public void setScore(Double score) {
        this.score = score;
    }
}
