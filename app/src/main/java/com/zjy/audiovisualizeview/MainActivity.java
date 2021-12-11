package com.zjy.audiovisualizeview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.zjy.audiovisualizeview.utils.PermissionUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView vReflect, vCircle, vSingle, vWave, vNet, vGrain;
    private static final int RECORD_AUDIO = 10001;
    private static final int READ_EXTERNAL_STORAGE = 10002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vReflect = findViewById(R.id.reflect_entrance);
        vCircle = findViewById(R.id.circle_entrance);
        vSingle = findViewById(R.id.single_entrance);
        vWave = findViewById(R.id.wave_entrance);
        vNet = findViewById(R.id.net_entrance);
        vGrain = findViewById(R.id.grain_entrance);
        vSingle.setOnClickListener(this);
        vReflect.setOnClickListener(this);
        vCircle.setOnClickListener(this);
        vWave.setOnClickListener(this);
        vNet.setOnClickListener(this);
        vGrain.setOnClickListener(this);

        PermissionUtils.requestPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO, RECORD_AUDIO);
        PermissionUtils.requestPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE);

        initLog();
    }

    private void initLog() {
        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(BuildConfig.DEBUG ? LogLevel.ALL             // 指定日志级别，低于该级别的日志将不会被打印，默认为 LogLevel.ALL
                        : LogLevel.NONE)
                .tag("asuralxd")                                         // 指定 TAG，默认为 "X-LOG"
                //                .enableThreadInfo()                                    // 允许打印线程信息，默认禁止
//                .enableStackTrace(2)                                   // 允许打印深度为 2 的调用栈信息，默认禁止
//                .enableBorder()                                        // 允许打印日志边框，默认禁止
                //                .jsonFormatter(new MyJsonFormatter())                  // 指定 JSON 格式化器，默认为 DefaultJsonFormatter
                //                .xmlFormatter(new MyXmlFormatter())                    // 指定 XML 格式化器，默认为 DefaultXmlFormatter
                //                .throwableFormatter(new MyThrowableFormatter())        // 指定可抛出异常格式化器，默认为 DefaultThrowableFormatter
                //                .threadFormatter(new MyThreadFormatter())              // 指定线程信息格式化器，默认为 DefaultThreadFormatter
                //                .stackTraceFormatter(new MyStackTraceFormatter())      // 指定调用栈信息格式化器，默认为 DefaultStackTraceFormatter
                //                .borderFormatter(new MyBoardFormatter())               // 指定边框格式化器，默认为 DefaultBorderFormatter
                //                .addObjectFormatter(AnyClass.class,                    // 为指定类型添加对象格式化器
                //                        new AnyClassObjectFormatter())                     // 默认使用 Object.toString()
                //                .addInterceptor(new BlacklistTagsFilterInterceptor(    // 添加黑名单 TAG 过滤器
                //                        "blacklist1", "blacklist2", "blacklist3"))
                //                .addInterceptor(new MyInterceptor())                   // 添加一个日志拦截器
                .build();

        Printer androidPrinter = new AndroidPrinter(true);         // 通过 android.util.Log 打印日志的打印器
        //        Printer consolePrinter = new ConsolePrinter();             // 通过 System.out 打印日志到控制台的打印器
        Printer filePrinter = new FilePrinter                      // 打印日志到文件的打印器
                .Builder(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath())                             // 指定保存日志文件的路径
                .fileNameGenerator(new DateFileNameGenerator())        // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
                .backupStrategy(new NeverBackupStrategy())             // 指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
                //                .cleanStrategy(new FileLastModifiedCleanStrategy(MAX_TIME))     // 指定日志文件清除策略，默认为 NeverCleanStrategy()
                //                .flattener(new MyFlattener())                          // 指定日志平铺器，默认为 DefaultFlattener
                //                .writer(new MyWriter())                                // 指定日志写入器，默认为 SimpleWriter
                .build();

        XLog.init(                                                 // 初始化 XLog
                config,                                                // 指定日志配置，如果不指定，会默认使用 new LogConfiguration.Builder().build()
                androidPrinter,                                        // 添加任意多的打印器。如果没有添加任何打印器，会默认使用 AndroidPrinter(Android)/ConsolePrinter(java)
                //                consolePrinter,
                filePrinter);
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.single_entrance:
                intent = new Intent(MainActivity.this, SingleVisualizeActivity.class);
                break;
            case R.id.reflect_entrance:
                intent = new Intent(MainActivity.this, ReflectVisualizeActivity.class);
                break;
            case R.id.circle_entrance:
                intent = new Intent(MainActivity.this, CircleVisualizeActivity.class);
                break;
            case R.id.wave_entrance:
                intent = new Intent(MainActivity.this, WaveVisualizeActivity.class);
                break;
            case R.id.net_entrance:
                intent = new Intent(MainActivity.this, NetVisualizeActivity.class);
                break;
            case R.id.grain_entrance:
                intent = new Intent(MainActivity.this, GrainVisualizeActivity.class);
                break;
        }
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RECORD_AUDIO:
            case READ_EXTERNAL_STORAGE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}