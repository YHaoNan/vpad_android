package top.yudoge.vpadapi;

public class ClientException extends RuntimeException {
    public ClientException(String msg) {
        super(msg);
    }
    public ClientException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
