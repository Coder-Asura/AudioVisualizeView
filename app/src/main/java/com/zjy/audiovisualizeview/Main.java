package com.zjy.audiovisualizeview;

import java.util.Arrays;

/**
 * Author: Asuraliu
 * Date: 2021/12/14 19:26
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * Asuraliu 2021/12/14 1.0 首次创建
 */
public class Main {
    public static void main(String[] args){
        System.out.println("aaaaaa"+ Arrays.toString(trans(new byte[]{1,2,6,127,-128})));
    }

    private static float[] trans(byte[] src) {
        float[] dest = new float[src.length];
        for (int i = 0; i < src.length; i++) {
            dest[i]= (src[i] & 0xFF)/128f;
        }
        return dest;
    }
}
