package com.zjy.audiovisualize.visualize;

import android.media.audiofx.Visualizer;

import com.elvishew.xlog.XLog;

import java.util.Arrays;

/**
 * Date: 2020/11/24
 * Author: Yang
 * Describe: hold the visualizer and callback something
 */
public class VisualizerHelper {


    private Visualizer mVisualizer;
    private VisualizeCallback mCallback;
    private int mCount;

    public void setVisualCount(int count) {
        mCount = count;
    }

    public void setVisualizeCallback(VisualizeCallback callback) {
        mCallback = callback;
    }

    int index = 0;
    float[] maxB;
    boolean printHz = false;

    /**
     * Sets the audio session id for the currently playing audio
     *
     * @param audioSessionId of the media to be visualised
     */
    public void setAudioSessionId(int audioSessionId) {
        if (mVisualizer != null) {
            release();
        }

        mVisualizer = new Visualizer(audioSessionId);
        mVisualizer.setEnabled(false);
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        index = 0;
        maxB = new float[513];
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                XLog.d("onWaveFormDataCapture  index=" + index + " " + samplingRate + " " + bytes.length + " " + Arrays.toString(bytes));
//                XLog.d("2onWaveFormDataCapture  index=" + index + " " + samplingRate + " " + bytes.length + " " + Arrays.toString(trans(bytes)));
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] ffts,
                                         int samplingRate) {
                XLog.d("onFftDataCapture index=" + index + " " + samplingRate + " " + ffts.length + " " + Arrays.toString(ffts));
                //                int n = ffts.length;
                //                float[] magnitudes = new float[n / 2 + 1];
                //                float[] phases = new float[n / 2 + 1];
                //                magnitudes[0] = (float) Math.abs(ffts[0]);      // DC
                //                magnitudes[n / 2] = (float) Math.abs(ffts[1]);  // Nyquist
                //                phases[0] = phases[n / 2] = 0;
                //                for (int k = 1; k < n / 2; k++) {
                //                    int i = k * 2;
                //                    magnitudes[k] = (float) Math.hypot(ffts[i], ffts[i + 1]);
                //                    phases[k] = (float) Math.atan2(ffts[i + 1], ffts[i]);
                //                }
                //                if (mCallback != null) {
                //                    mCallback.onFftDataCapture(magnitudes);
                //                }


                //                https://blog.csdn.net/gkw421178132/article/details/71081628
                //   直流      实部 虚部      频率范围 0-采样率/2 getSamplingRate()
                //    0    1    2    3    4    5       n-2       n-1        n=getCaptureSize()
                //    R0  Rn/2 R1   L1    R2   L2    R(n-1)/2  L(n-1)/2
                //   k次频率 = getSamplingRate() * k / (n/2)                   int getFft (byte[] fft)
                int frequencyCounts = ffts.length / 2 + 1; //  =513
                float frequencyEach = samplingRate / visualizer.getCaptureSize();  //86132  samplingRate=44,100,000 mHz  getCaptureSize()=1024
                float[] fft = new float[frequencyCounts];    //  (n-2)/2+2 = n/2+1  容量 = getCaptureSize()/2+
                float[] hz = new float[frequencyCounts];    //  (n-2)/2+2 = n/2+1  容量 = getCaptureSize()/2+
                float[] phases = new float[frequencyCounts];
                fft[0] = Math.abs(ffts[0]);
                phases[0] = phases[ffts.length / 2] = 0;
                hz[0] = 0;
                for (int i = 1; i < frequencyCounts - 1; i++) {
                    fft[i] = (float) Math.hypot(ffts[2 * i], ffts[2 * i + 1]);
                    phases[i] = (float) Math.atan2(ffts[i + 1], ffts[i]);
                    hz[i] = frequencyEach * i;
                }
                fft[frequencyCounts - 1] = Math.abs(ffts[1]);
                hz[frequencyCounts - 1] = frequencyEach * (frequencyCounts - 1);
                XLog.d("fft index=" + index + " " + fft.length + " max=" + findMax(fft) + " " + Arrays.toString(fft));
                XLog.d("phases index=" + index + " " + phases.length + " max=" + findMax(phases) + " " + Arrays.toString(phases));
                if (!printHz) {
                    XLog.d("hz index=" + index + " " + hz.length + " " + Arrays.toString(hz));
                    printHz = true;
                }
                maxB[index] = findMax(fft);
                index++;
                if (mCallback != null) {
                    mCallback.onFftDataCapture(fft);
                }
//                double[] a = new double[64];
//                ByteArray2DoubleArray(a, ffts);
//                XLog.d("ByteArray2DoubleArray index= " + index + " " + Arrays.toString(a));
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, true);

        mVisualizer.setEnabled(true);
    }


    private void ByteArray2DoubleArray(double[] doubleArray, byte[] byteArray) {

        //        short s1 = (short) ((7 & 0x00FF) << 8 | -18 & 0x00FF);
        //        XLog.d("sdadsada"+s1 );
        for (int i = 0; i < doubleArray.length; i++) {
            byte bl = byteArray[2 * i];
            byte bh = byteArray[2 * i + 1];

            short s = (short) ((bh & 0x00FF) << 8 | bl & 0x00FF);
            /**
             * Java中short是2字节 1字节是8bit 这里为什么要加上“& 0x00FF”呢？ 这是为了把复数前面的“很多个F”去掉
             * 只取后8位的数据 防止相互影响
             */

            System.out.println("s_" + s);

            doubleArray[i] = s / 32768f; // 32768 = 2^15
        }
    }

    //    public static void main(String[] args){
    //        System.out.println("aaaaaa"+Arrays.toString(trans(new byte[]{1,2,6,127,-128})));
    //    }

    private static float[] trans(byte[] src) {
        float[] dest = new float[src.length];
        for (int i = 0; i < src.length; i++) {
            dest[i] = ((src[i] & 0x00FF) + 128) / 255f;
        }
        return dest;
    }

    public void printMax() {
        XLog.d("printMax " + findMax(maxB));
    }

    /**
     * Releases the visualizer
     */
    public void release() {
        if (mVisualizer != null) {
            mVisualizer.setEnabled(false);
            mVisualizer.release();
            mVisualizer = null;
        }
    }

    private float findMax(float[] mRawAudioBytes) {
        float max = mRawAudioBytes[0];
        for (float mRawAudioByte : mRawAudioBytes) {
            if (max < mRawAudioByte) {
                max = mRawAudioByte;
            }
        }
        return max;
    }
}
