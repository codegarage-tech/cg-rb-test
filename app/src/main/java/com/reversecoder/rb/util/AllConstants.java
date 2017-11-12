package com.reversecoder.rb.util;

import android.os.Environment;

import java.io.File;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class AllConstants {
    public static boolean isFreeApp = true;

    public static final String HIDDEN_FOLDER_NAME = ".uffJhal";
    public static final String HIDDEN_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + HIDDEN_FOLDER_NAME;
    public static final int BUFFER_SIZE = 1024;

    public static final String KEY_INTENT_EXTRA_ACTION = "KEY_INTENT_EXTRA_ACTION";
    public static final String KEY_INTENT_EXTRA_DIR_PATH = "KEY_INTENT_EXTRA_DIR_PATH";
    public static final String KEY_INTENT_EXTRA_MASK = "KEY_INTENT_EXTRA_MASK";
    public static final String KEY_INTENT_EXTRA_UPDATE = "KEY_INTENT_EXTRA_UPDATE";

    public static final int EXTRA_ACTION_START = 0;
    public static final int EXTRA_ACTION_STOP = 1;

    public static final String INTENT_FILTER_ACTIVITY_UPDATE = "INTENT_FILTER_ACTIVITY_UPDATE";
}