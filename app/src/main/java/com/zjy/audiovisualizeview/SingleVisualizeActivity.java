package com.zjy.audiovisualizeview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.elvishew.xlog.XLog;
import com.zjy.audiovisualize.view.AudioVisualizeView;

public class SingleVisualizeActivity extends AppCompatActivity {

    private AudioVisualizeView vAudioVisualize;
    private Button btn_change_music;
    private int index = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_visualize);

        vAudioVisualize = findViewById(R.id.audio_visualize_view);
        btn_change_music = findViewById(R.id.btn_change_music);

        final int[] array = new int[]{R.raw.sound, R.raw.sound1, R.raw.sound2, R.raw.sound3};
        btn_change_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                if (index >= array.length) {
                    index = 0;
                }
                vAudioVisualize.doPlay(array[index]);
            }
        });
        XLog.d("start play!");
        vAudioVisualize.doPlay(array[index]);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vAudioVisualize != null) {
            vAudioVisualize.release();
        }
    }
}