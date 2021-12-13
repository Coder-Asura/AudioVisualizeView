package com.zjy.audiovisualize.view;

import static com.zjy.audiovisualize.constants.VisualizeMode.HORIZONTAL_LINE_BOTTOM;
import static com.zjy.audiovisualize.constants.VisualizeMode.HORIZONTAL_LINE_TOP;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.elvishew.xlog.XLog;
import com.zjy.audiovisualize.R;

import java.util.Arrays;

/**
 * Date: 2020/11/30
 * Author: Yang
 * Describe: In SINGLE mode, show spectrum base on a horizontal line, with jumping above of the center line , it's also default visualize mode.
 */
public class SingleVisualizeView extends AudioVisualizeView {

    private int mOrientation;


    public SingleVisualizeView(Context context) {
        super(context);
    }

    public SingleVisualizeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleVisualizeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void handleAttr(TypedArray typedArray) {
        mOrientation = typedArray.getInteger(R.styleable.AudioVisualizeView_visualize_orientation, HORIZONTAL_LINE_TOP);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void drawChild(Canvas canvas) {
        int step = mRawAudioBytes.length / mSpectrumCount;
        mStrokeWidth = (mRect.width() - (mSpectrumCount - 1) * mItemMargin) / mSpectrumCount * 1.0f;
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.FILL);
        //        Arrays.sort(mRawAudioBytes);
        XLog.d("drawChild " + Arrays.toString(mRawAudioBytes));
        for (int i = 0, j = 0; j < mRawAudioBytes.length; j += step, i++) {
            switch (mOrientation) {
                case HORIZONTAL_LINE_TOP:
                    int color = Color.HSVToColor(new float[]{mRawAudioBytes[j] * 360 / 80.3136f, 1, 1});
                    mPaint.setColor(color);
                    canvas.drawLine(mRect.width() * i / mSpectrumCount,
                            mRect.height() / 2,
                            mRect.width() * i / mSpectrumCount,
                            2 + mRect.height() / 2 - mRawAudioBytes[j] * 5,
                            mPaint);
                    break;
                case HORIZONTAL_LINE_BOTTOM:
                    canvas.drawLine(mRect.width() * i / mSpectrumCount,
                            mRect.height() / 2,
                            mRect.width() * i / mSpectrumCount,
                            2 + mRect.height() / 2 + mRawAudioBytes[i],
                            mPaint);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onComplete() {
        super.onComplete();
        visualizerHelper.printMax();
        visualizerHelper.release();
    }
}
