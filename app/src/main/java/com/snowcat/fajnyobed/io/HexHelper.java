package com.snowcat.fajnyobed.io;

/**
 * Created by AntikAdmin on 25. 9. 2014.
 */
public class HexHelper {

    public static int getValue(char ch) {
        if (ch >= '0' && ch <= '9')
            return ch - '0';
        else if (ch >= 'a' && ch <= 'z')
            return ch - 'a' + 10;
        else if (ch >= 'A' && ch <= 'Z')
            return ch - 'A' + 10;
        else
            return 0;
    }

    public static char toChar(int b) {
        if (b >= 0 && b <= 9)
            return (char) ('0' + b);
        else if (b >= 10)
            return (char) ('A' + b - 10);
        return '0';
    }

    public static byte[] toByteArray(String s) {
        int start, end = s.length();
        if (s.charAt(0) == '0' && s.charAt(1) == 'x')
            start = 2;
        else
            start = 0;

        byte[] result = new byte[(end - start) / 2];
        // Log.v("com.antik.tango", s + " " + String.valueOf(result.length));
        char[] c = s.toCharArray();
        for (int i = 0; i < (end - start) / 2; i++) {
            result[i] = (byte) ((getValue(c[i * 2 + start]) * 16 + getValue(c[i * 2 + 1 + start])) & 0xFF);
        }

        return result;
    }

    public static String toString(byte[] b) {
        char[] c = new char[b.length * 2];
        for (int i = 0; i < b.length; i++) {
            c[i * 2 + 0] = toChar((b[i] >> 4) & 0x0F);
            c[i * 2 + 1] = toChar((b[i]) & 0x0F);
        }
        return new String(c);
    }
}
