package top.yudoge.vpadapi;

import top.yudoge.vpadapi.structure.Message;

@FunctionalInterface
public interface OnMessageArrivedListener {
    void onMessageArrived(Message message);
}
