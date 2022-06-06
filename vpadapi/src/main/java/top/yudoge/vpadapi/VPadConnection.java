package top.yudoge.vpadapi;


import top.yudoge.vpadapi.structure.Message;

public interface VPadConnection {
    void disconnect();
    void sendMessage(Message msg);
    void readMessage(Message msg);
    boolean isClosed();
}
