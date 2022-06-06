package top.yudoge.vpadapi.structure;

public class Int1 implements ValueBytable<Byte> {

    public Byte num;

    @Override
    public byte[] toBytes() {
        return new byte[] { num };
    }

    @Override
    public int fromBytes(byte[] bytes, int offset) {
        num = bytes[offset];
        return 1;
    }

    @Override
    public Byte value() {
        return num;
    }

    public static Int1 from(Byte b) {
        Int1 int1 = new Int1();
        int1.num = b;
        return int1;
    }
}
