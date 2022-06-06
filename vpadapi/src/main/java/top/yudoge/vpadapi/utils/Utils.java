package top.yudoge.vpadapi.utils;


import java.util.Arrays;
import java.util.List;

public class Utils {
    public static byte[] concatBytes(byte[] arr1, byte[] arr2) {
        byte[] arr3 = Arrays.copyOf(arr1, arr1.length + arr2.length);
        for (int i = 0; i < arr2.length; i++) {
            arr3[arr1.length + i] = arr2[i];
        }

        return arr3;
    }



    public static byte[] subBytes(byte[] bytes, int start, int length) {
        byte sub[] = new byte[length];
        for (int i = start; i < start + length; i++) {
            sub[i - start] = bytes[i];
        }
        return sub;
    }

    public static byte[] copyOf(List<byte[]> bytes, int length) {
        int tot_length = 0;
        for (int i = 0; i < bytes.size(); i++) tot_length += bytes.get(i).length;

        int offset = 0;
        byte result[] = new byte[length];
        for (int i = 0; i < bytes.size(); i++) {
            for (int j = 0; j < bytes.get(i).length; j++) {
                result[offset++] = bytes.get(i)[j];
            }
        }
        return result;
    }
}