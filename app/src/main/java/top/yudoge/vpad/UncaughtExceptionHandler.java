package top.yudoge.vpad;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import top.yudoge.vpad.toplevel.Constants;
import top.yudoge.vpad.toplevel.ContextExtsKt;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Activity context;

    public UncaughtExceptionHandler(Activity context) {
        this.context = context;
    }
    private static final String TAG = UncaughtExceptionHandler.class.getSimpleName();

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        e.printStackTrace();

        SimpleDateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
        File crashLogFile = ContextExtsKt.getCrashLog(context);
        Log.i(TAG, "CRASH LOG FILE " + crashLogFile);
        if (crashLogFile == null) return;
        PrintWriter printWriter = null;
        try {
            if (!crashLogFile.exists()) {
                crashLogFile.createNewFile();
            }
            printWriter = new PrintWriter(new FileWriter(crashLogFile));
            printWriter.printf("Thread: %s\n", t.getName());
            printWriter.printf("LocalTime: %s\n", dataFormatter.format(new Date()));
            printWriter.printf("Model: %s\n", Build.MODEL);
            printWriter.printf("Manufacturer: %s\n", Build.MANUFACTURER);
            printWriter.printf("SDK Number: %s\n", Build.VERSION.SDK_INT);
            printWriter.printf("RELEASE Number: %s\n", Build.VERSION.RELEASE);
            printWriter.println();
            printWriter.println();
            e.printStackTrace(printWriter);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (printWriter != null) printWriter.close();
            context.finish();
        }
    }

}
