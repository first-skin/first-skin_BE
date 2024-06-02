package firstskin.firstskin.user.api.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewResponseDto {
    private String content;
    private int score;
    private List<String> reviewImageUrls;
    private String nickname;

    public ReviewResponseDto(String content, int score, List<String> reviewImageUrls, String nickname) {
        this.content = content;
        this.score = score;
        this.reviewImageUrls = reviewImageUrls;
        this.nickname = nickname;
    }


    public ReviewResponseDto() {
    }
}
