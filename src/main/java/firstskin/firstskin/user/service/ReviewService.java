package firstskin.firstskin.user.service;

import firstskin.firstskin.member.domain.Member;
import firstskin.firstskin.review.domain.Review;
import firstskin.firstskin.review.domain.ReviewImage;
import firstskin.firstskin.review.repository.ReviewImageRepository;
import firstskin.firstskin.review.repository.ReviewRepository;
import firstskin.firstskin.user.api.dto.UpdateReview;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        createDirIfNotExist(Paths.get(uploadPath));
    }


    public ReviewService(ReviewRepository reviewRepository, ReviewImageRepository reviewImageRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewImageRepository = reviewImageRepository;
    }

    public List<Review> getAllProductReviews(Long productId){
        return reviewRepository.findByProductId(productId);
    }

    public List<Review> getAllMemberReviews(Long memberId) {
        return reviewRepository.findByMember_MemberId(memberId);
    }

    @Transactional
    public void addReview(Member member, Long productId, String content, int score, List<MultipartFile> reviewImages) throws IOException {
        // 리뷰 생성
        Review newReview = new Review(member, productId, content, score, true);

        // 리뷰 이미지 저장 및 추가
        for (MultipartFile image : reviewImages) {
            String imageUrl = saveImage(image);
            ReviewImage reviewImage = new ReviewImage(newReview, imageUrl);
            reviewImageRepository.save(reviewImage);
        }

        reviewRepository.save(newReview);
    }

    public void updateReview(UpdateReview review){
        Review findReview = reviewRepository.findById(review.getReviewId())
                .orElseThrow(IllegalArgumentException::new);

        findReview.update(review.getContent(),review.getScore());

    }

    public void deleteReview(Long reviewId){
        reviewRepository.deleteById(reviewId);

    }

    public List<Review> getAllReviewsSortedByScore(Long productId) {
        return reviewRepository.findByProductId(productId, Sort.by(Sort.Direction.DESC, "score"));
    }

    private String saveImage(MultipartFile image) throws IOException {
        if (image.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어 있습니다.");
        }

        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFilename = UUID.randomUUID().toString() + extension;

        // 업로드 경로 설정
        Path reviewImagePath = Paths.get(uploadPath);
        createDirIfNotExist(reviewImagePath);

        // 파일 저장
        Path destinationFile = reviewImagePath.resolve(savedFilename);
        image.transferTo(destinationFile.toFile());

        return destinationFile.toString();
    }
    private void createDirIfNotExist(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new IllegalStateException("디렉토리를 생성할 수 없습니다: " + path, e);
            }
        }
    }
}
