package top.yudoge.vpadapi.structure;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import top.yudoge.vpadapi.utils.Utils;


// 长度0~128
public class VariLongString implements ValueBytable<String> {
    private String string;
    public VariLongString() {
    }
    public VariLongString(String string) {
        this.string = string;
    }

    @Override
    public byte[] toBytes() {
        return Utils.concatBytes(
                Int1.from((byte)string.length()).toBytes(),
                string.getBytes(Charset.forName("UTF-8"))
        );
    }

    @Override
    public int fromBytes(byte[] bytes, int offset) {
        Int1 int1 = new Int1();
        int1.fromBytes(bytes, offset);
        string = new String(bytes, offset+1, int1.value(), Charset.forName("UTF-8"));
        return 1 + int1.value();
    }

    @Override
    public String value() {
        return string;
    }


}
