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
                XLog.d("onWaveFormDataCapture " + samplingRate + " " + bytes.length + " " + Arrays.toString(bytes));
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] ffts,
                                         int samplingRate) {
                XLog.d("onFftDataCapture index=" + index + " " + samplingRate + " " + ffts.length + " " + Arrays.toString(ffts));
//                https://blog.csdn.net/gkw421178132/article/details/71081628
                //   直流      实部 虚部      频率范围 0-采样率/2 getSamplingRate()
                //    0    1    2    3    4    5       n-2       n-1        n=getCaptureSize()
                //    R0  Rn/2 R1   L1    R2   L2    R(n-1)/2  L(n-1)/2
                //   k次频率 = getSamplingRate() * k / (n/2)                   int getFft (byte[] fft)
                int frequencyCounts = ffts.length / 2 + 1; //  =513
                float frequencyEach = samplingRate / visualizer.getCaptureSize();  //86132  samplingRate=44,100,000 mHz  getCaptureSize()=1024
                float[] fft = new float[frequencyCounts];    //  (n-2)/2+2 = n/2+1  容量 = getCaptureSize()/2+
                float[] hz = new float[frequencyCounts];    //  (n-2)/2+2 = n/2+1  容量 = getCaptureSize()/2+
                fft[0] = Math.abs(ffts[0]);
                hz[0] = 0;
                for (int i = 1; i < frequencyCounts - 1; i++) {
                    fft[i] = (float) Math.hypot(ffts[2 * i], ffts[2 * i + 1]);
                    hz[i] = frequencyEach * i;
                }
                fft[frequencyCounts - 1] = Math.abs(ffts[1]);
                hz[frequencyCounts - 1] = frequencyEach * (frequencyCounts - 1);
                XLog.d("fft index=" + index + " " + fft.length + " max=" + findMax(fft) + " " + Arrays.toString(fft));
                if (!printHz) {
                    XLog.d("hz index=" + index + " " + hz.length + " " + Arrays.toString(hz));
                    printHz = true;
                }
                maxB[index] = findMax(fft);
                index++;
                if (mCallback != null) {
                    mCallback.onFftDataCapture(fft);
                }
            }
        }, Visualizer.getMaxCaptureRate() / 2, false, true);

        mVisualizer.setEnabled(true);
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
