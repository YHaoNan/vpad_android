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
    private int channel;

    public MidiMessage(int note, int velocity, int state, int channel) {
        this.op = Operations.OP_MIDIMESSAGE;
        this.note = note;
        this.velocity = velocity;
        this.state = state;
        this.channel = channel;
    }

    @Override
    byte[] bodyToBytes() {
        return new byte[] {(byte) this.note, (byte) this.velocity, (byte) this.state, (byte) this.channel};
    }

    @Override
    int bodyFromBytes(byte[] bytes, int offset) {
        this.note = bytes[offset];
        this.velocity = bytes[offset + 1];
        this.state = bytes[offset + 2];
        this.channel = bytes[offset + 3];
        return 4;
    }

    @Override
    public String toString() {
        return "MidiMessage{" +
                "note=" + note +
                ", velocity=" + velocity +
                ", state=" + state +
                ", channel=" + channel +
                '}';
    }
}
