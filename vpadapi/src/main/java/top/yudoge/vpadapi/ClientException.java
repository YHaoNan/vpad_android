package top.yudoge.vpadapi;

public class ClientException extends RuntimeException {
    public ClientException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
