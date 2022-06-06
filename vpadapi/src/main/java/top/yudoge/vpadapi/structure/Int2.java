package top.yudoge.vpadapi.structure;


public class Int2 implements ValueBytable<Short> {
    public Short num;

    @Override
    public byte[] toBytes() {
        return new byte[] { (byte) (num >> 8 & 0xff), (byte) (num & 0xff) };
    }

    @Override
    public int fromBytes(byte[] bytes, int offset) {
        num = (short) ((bytes[offset] & 0xff) << 8 | bytes[offset+1] & 0xff);
        return 2;
    }

    @Override
    public Short value() {
        return num;
    }
}
