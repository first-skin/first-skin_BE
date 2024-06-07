package firstskin.firstskin.common.exception;

public class NoSuchResourceException extends RuntimeException {
    public NoSuchResourceException(String message) {
        super(message);
    }

    public NoSuchResourceException() {
        super("존재하지 않는 페이지입니다.");
    }
}
