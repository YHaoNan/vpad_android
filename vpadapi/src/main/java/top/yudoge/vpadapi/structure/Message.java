package top.yudoge.vpadapi.structure;


import org.jetbrains.annotations.NotNull;

import top.yudoge.vpadapi.utils.Utils;

public abstract class Message implements Bytable<Message> {
    // 操作
    protected int op;

    @Override
    public byte[] toBytes() {
        byte bodyBytes[] = bodyToBytes();
        Int2 messageLength = new Int2();
        messageLength.num = (short)(1 + bodyBytes.length);
        return Utils.concatBytes(
                messageLength.toBytes(),
                Utils.concatBytes(
                        new byte[] {(byte) op},
                        bodyToBytes()
                )
        );
    }


    abstract byte[] bodyToBytes();

    @Override
    public int fromBytes(@NotNull byte[] bytes, int offset) {
        Int2 messageLength = new Int2();
        messageLength.fromBytes(bytes, offset);
        offset += 2;
        return 3 + bodyFromBytes(bytes, offset + 1);
    }
    abstract int bodyFromBytes(byte[] bytes, int offset);

    @Override
    public String toString() {
        return "Message{" +
                "op=" + op +
                bodyToBytes() +
                '}';
    }
}
