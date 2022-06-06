package top.yudoge.vpadapi.structure;

import org.jetbrains.annotations.NotNull;

public interface Bytable<T> {
    /**
     * 将自己转换成byte数组
     * @return
     */
    byte[] toBytes();

    /**
     * 从byte数组中装载自己
     * @param bytes byte数组
     * @param offset 从该byte数组的哪个位置开始
     * @return 本次装载读取的字节数
     */
    int fromBytes(@NotNull byte[] bytes, int offset);
}
