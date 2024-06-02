package firstskin.firstskin.common.exception;

public class FileNotFound extends RuntimeException {
    public FileNotFound() {
        super("파일을 찾을 수 없습니다.");
    }
    public FileNotFound(String message) {
        super(message);
    }
}
