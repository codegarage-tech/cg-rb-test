package com.reversecoder.rb.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.FileObserver;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.reversecoder.rb.model.RecoveryFileInfo;
import com.reversecoder.rb.observer.FileRecoveryObserver;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

import static android.os.FileObserver.ALL_EVENTS;
import static com.reversecoder.rb.util.AllConstants.EXTRA_ACTION_START;
import static com.reversecoder.rb.util.AllConstants.EXTRA_ACTION_STOP;
import static com.reversecoder.rb.util.AllConstants.HIDDEN_DIRECTORY;
import static com.reversecoder.rb.util.AllConstants.KEY_INTENT_EXTRA_ACTION;
import static com.reversecoder.rb.util.AllConstants.KEY_INTENT_EXTRA_DIR_PATH;
import static com.reversecoder.rb.util.AllConstants.KEY_INTENT_EXTRA_MASK;

public class FileRecoveryService extends Service {

    public static String TAG = FileRecoveryService.class.getSimpleName();
    private String mDirPath = "";
    private int mMask = -1;
    private Context mContext;
    FileRecoveryObserver mFileRecoveryObserver;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        checkDirs();
    }

    private void checkDirs() {
        String directoryPath = HIDDEN_DIRECTORY;
        Log.d(TAG + "hiddenDirectory: ", directoryPath);
        File directoryFolder = new File(directoryPath);
        if (!directoryFolder.exists()) {
            if (directoryFolder.mkdir()) {
                Log.d(TAG + "hiddenDirectory: ", "Directory created");
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            int action = intent.getIntExtra(KEY_INTENT_EXTRA_ACTION, -1);
            switch (action) {
                case EXTRA_ACTION_START: {
                    mDirPath = intent.getExtras().getString(KEY_INTENT_EXTRA_DIR_PATH);
                    mMask = intent.getIntExtra(KEY_INTENT_EXTRA_MASK, ALL_EVENTS);

                    new doStartRecoveryObserver(mContext, mDirPath, mMask).execute();

                    break;
                }
                case EXTRA_ACTION_STOP: {

                    new doStopRecoveryObserver().execute();

                    break;
                }
                default: {
                    break;
                }
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void handleStartFileObserver(Context context, String dirPath, int mask) {
        if (mFileRecoveryObserver == null) {
            mFileRecoveryObserver = new FileRecoveryObserver(context, dirPath, mask, new FileRecoveryObserver.EventListener() {
                @Override
                public void onEvent(final int event, final File file) {
                    switch (event) {
                        case FileObserver.ATTRIB:
                            break;
                        case FileObserver.DELETE:
                            break;
                        case FileObserver.DELETE_SELF:

                            break;
                    }
                }
            });
        }
        mFileRecoveryObserver.startWatching();

        Log.d(TAG, "handleStartFileObserver: starting..");
    }

//    private void inputDeletedFileToDb(int event, File file) {
//        List<RecoveryFileInfo> deletedfile = DataSupport.where("originFilePath = ?", file.getAbsolutePath()).find(RecoveryFileInfo.class);
//        if (deletedfile.size() > 0 && deletedfile.get(0).getOriginFilePath().equalsIgnoreCase(file.getAbsolutePath())) {
//        } else {
//
//        }
//    }

    private void handleStopFileObserver() {
        if (mFileRecoveryObserver != null) {
            Log.d(TAG, "handleStopFileObserver: stopping..");
            mFileRecoveryObserver.stopWatching();
        }
    }

    class doStartRecoveryObserver extends AsyncTask<String, String, String> {

        private Context mContext;
        private String mDirPath;
        private int mMask;

        doStartRecoveryObserver(Context context, String dirPath, int mask) {
            mContext = context;
            mDirPath = dirPath;
            mMask = mask;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {

            handleStartFileObserver(mContext, mDirPath, mMask);

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

    class doStopRecoveryObserver extends AsyncTask<String, String, String> {

        doStopRecoveryObserver() {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {

            handleStopFileObserver();

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }
}
