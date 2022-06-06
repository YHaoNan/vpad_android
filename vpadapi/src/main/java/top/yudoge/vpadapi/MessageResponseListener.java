package top.yudoge.vpadapi;


import top.yudoge.vpadapi.structure.Message;

public interface MessageResponseListener {
    void onResponse(Message message);
}
