package top.yudoge.vpadapi.structure;


import top.yudoge.vpadapi.Operations;
import top.yudoge.vpadapi.utils.Utils;

public class HandShakeMessage extends Message {

    private String name;
    private String platform;

    public HandShakeMessage() {
        this(null, null);
    }
    public HandShakeMessage(String name, String platform) {
        this.op = Operations.OP_HANDSHAKE;
        this.name = name;
        this.platform = platform;
    }

    @Override
    byte[] bodyToBytes() {
        return Utils.concatBytes(
                new VariLongString(name).toBytes(),
                new VariLongString(platform).toBytes()
        );
    }

    @Override
    int bodyFromBytes(byte[] bytes, int offset) {
        int startOffset = offset;
        VariLongString temp = new VariLongString();
        offset += temp.fromBytes(bytes, offset);
        name = temp.value();
        offset += temp.fromBytes(bytes, offset);
        platform = temp.value();
        return offset - startOffset;
    }

    public String getName() {
        return name;
    }

    public String getPlatform() {
        return platform;
    }
}
