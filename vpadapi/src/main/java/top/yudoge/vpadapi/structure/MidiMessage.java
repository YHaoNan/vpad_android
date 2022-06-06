package top.yudoge.vpadapi.structure;


import top.yudoge.vpadapi.Operations;

public class MidiMessage extends Message {
    public static final int STATE_OFF = 0;
    public static final int STATE_ON = 1;

    // 1字节
    private int note;
    // 1字节
    private int velocity;
    // 1字节
    private int state;

    public MidiMessage(int note, int velocity, int state) {
        this.op = Operations.OP_MIDIMESSAGE;
        this.note = note;
        this.velocity = velocity;
        this.state = state;
    }

    @Override
    byte[] bodyToBytes() {
        return new byte[] {(byte) this.note, (byte) this.velocity, (byte) this.state};
    }

    @Override
    int bodyFromBytes(byte[] bytes, int offset) {
        this.note = bytes[offset];
        this.velocity = bytes[offset + 1];
        this.state = bytes[offset + 2];
        return 3;
    }

    @Override
    public String toString() {
        return "MidiMessage{" +
                "note=" + note +
                ", velocity=" + velocity +
                ", state=" + state +
                '}';
    }
}
