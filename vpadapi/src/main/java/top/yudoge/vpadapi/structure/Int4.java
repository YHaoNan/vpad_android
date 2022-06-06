package top.yudoge.vpadapi.structure;

public class Int4 implements ValueBytable<Integer> {
    public Integer num;

    @Override
    public byte[] toBytes() {
        return new byte[] {
                (byte) (num >> 24 & 0xff),
                (byte) (num >> 16 & 0xff),
                (byte) (num >> 8 & 0xff) ,
                (byte) (num & 0xff)
        };
    }

    @Override
    public int fromBytes(byte[] bytes, int offset) {
        this.num = (bytes[offset] & 0xff) << 24 | (bytes[offset+1] & 0xff) << 16 | (bytes[offset+2] & 0xff) << 8 | bytes[offset+3] & 0xff;
        return 4;
    }


    @Override
    public Integer value() {
        return num;
    }
}
