package com.cookandroid.reviewary;

import android.os.Handler;
import android.util.Log;

public class AppConstants {

    public static String BOOKDATABASE_NAME = "book.db";
    public static String MOVIEDATABASE_NAME = "movie.db";
    public static String PLAYDATABASE_NAME = "play.db";

    public static final int REQ_PHOTO_CAPTURE = 103;
    public static final int REQ_PHOTO_SELECTION = 104;

    public static final int CONTENT_PHOTO = 105;
    public static final int CONTENT_PHOTO_EX = 106;

    public static String FOLDER_PHOTO;

    private static Handler handler = new Handler();
    private static final String TAG = "AppConstants";
    public static void println(final String data) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, data);
            }
        });
    }

}
