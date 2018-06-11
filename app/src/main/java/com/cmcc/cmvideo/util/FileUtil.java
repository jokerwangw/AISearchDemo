package com.cmcc.cmvideo.util;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class FileUtil {

    public static void printStringToFile(String str) {
        File file = new File(Environment.getExternalStorageDirectory(), "video.json");
        Log.d("video", "path = " + file.getAbsolutePath());
        FileWriter writer = null;
        try {
            Log.d("video", "createNewFile");
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new FileWriter(file, true);
            writer.write(str);
            writer.flush();
            writer.close();
            writer = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
