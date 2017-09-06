package com.supets.map.led2.view;

import android.graphics.Path;
import android.support.annotation.NonNull;

public class BCD {

    public static final byte[] bcd = new byte[]{
            0X3F, 0X06, 0X5B, 0X4F, 0X66,
            0X6D, 0X7D, 0X07, 0X7F, 0X6F
    };


    public static boolean isBitLight(byte data, int bit) {
        return ((data >> bit) & 1) == 1;
    }

    public static byte getNum(int num) {
        return bcd[num];
    }

    public static byte am(byte data, boolean am) {
        return (byte) (data | (am ? 0x80 : 0x00));
    }

    public static byte dian(byte data, boolean dian) {
        return (byte) (data | (dian ? 0x80 : 0x00));
    }


    //宽22个单位
    //字宽W+H=3p  字高3h+2W  W=4H 关系。
    public static Path[] buildBCDW4H(int P, int x0, int y0) {
        int h = (int) (0.6 * P);
        int w = 4 * h;
        return getPaths(x0, y0, h, w);
    }

    //宽18个单位
    //字宽W+H=3p  字高3h+2W  W=5H 关系。
    public static Path[] buildBCDW5H(int P, int x0, int y0) {
        int h = (int) (0.5 * P);
        int w = 5 * h;
        return getPaths(x0, y0, h, w);
    }

    @NonNull
    private static Path[] getPaths(int x0, int y0, int h, int w) {
        Path[] paths = new Path[7];
        paths[0] = getHBCD(w, h, x0 + h / 2, y0 + h / 2);//A
        paths[3] = getHBCD(w, h, x0 + h / 2, y0 + w + h + w + h + h / 2);//D
        paths[6] = getHBCD(w, h, x0 + h / 2, y0 + h / 2 + w + h);//G
        paths[1] = getLBCD(w, h, x0 + w + h / 2, y0 + h);//B
        paths[2] = getLBCD(w, h, x0 + w + h / 2, y0 + w + h + h);//C
        paths[4] = getLBCD(w, h, x0 + h / 2, y0 + w + h + h);//E
        paths[5] = getLBCD(w, h, x0 + h / 2, y0 + h);//F
        return paths;
    }


    private static Path getHBCD(int w, int h, int x, int y) {
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x + h / 2, y + h / 2);
        path.lineTo(x + w - h / 2, y + h / 2);
        path.lineTo(x + w, y);
        path.lineTo(x + w - h / 2, y - h / 2);
        path.lineTo(x + h / 2, y - h / 2);
        path.close();
        return path;
    }

    private static Path getLBCD(int w, int h, int x, int y) {
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x - h / 2, y + h / 2);
        path.lineTo(x - h / 2, y + w - h / 2);
        path.lineTo(x, y + w);
        path.lineTo(x + h / 2, y + w - h / 2);
        path.lineTo(x + h / 2, y + h / 2);
        path.close();
        return path;
    }

}
