package firstskin.firstskin.common.exception;

public class PythonScriptException extends RuntimeException {

    public PythonScriptException() {
        super("파이썬 스크립트에서 오류가 발생했습니다.");
    }

    public PythonScriptException(String message) {
        super(message);
    }
}
