package firstskin.firstskin.common.exception;

public class MissMatchType extends RuntimeException {
    public MissMatchType() {
        super("파일이 타입이 올바르지 않습니다.");
    }
    public MissMatchType(String message) {
        super(message);
    }
}
