package firstskin.firstskin.common.exception;

public class FaceNotFound extends RuntimeException {
    public FaceNotFound() {
        super("얼굴 인식에 실패했습니다.");
    }
    public FaceNotFound(String message) {
        super(message);
    }
}
