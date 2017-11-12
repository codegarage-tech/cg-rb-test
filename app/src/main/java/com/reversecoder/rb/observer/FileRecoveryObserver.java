package com.reversecoder.rb.observer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.FileObserver;
import android.util.Log;

import com.reversecoder.rb.model.DeletedFileInfo;
import com.reversecoder.rb.model.RecoveryFileInfo;
import com.reversecoder.rb.model.Tag;
import com.reversecoder.rb.model.UpdateCopyingProgress;
import com.reversecoder.rb.model.UpdateScanningProgress;
import com.reversecoder.rb.util.AllConstants;
import com.reversecoder.rb.util.AppUtils;
import com.reversecoder.rb.util.MD5Manager;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import static com.reversecoder.rb.util.AllConstants.BUFFER_SIZE;
import static com.reversecoder.rb.util.AllConstants.HIDDEN_DIRECTORY;
import static com.reversecoder.rb.util.AllConstants.INTENT_FILTER_ACTIVITY_UPDATE;
import static com.reversecoder.rb.util.AllConstants.KEY_INTENT_EXTRA_UPDATE;

/**
 * A FileObserver that observes all the files/folders within given directory
 * recursively. It automatically starts/stops monitoring new folders/files
 * created after starting the isValidDirectory.
 *
 * @author Md. Rashadul Alam
 */
public class FileRecoveryObserver extends FileObserver {

    private String mPath;
    private int mMask;
    private Context mContext;
    private EventListener mListener;
    private String TAG = FileRecoveryObserver.class.getSimpleName();

    private final Map<File, FileObserver> mObservers = new HashMap<>();
    private static HashMap<File, FileObserver> myFileObserverHashMap = new HashMap<>();
    private static HashMap<String, RecoveryFileInfo> recoverInfoHashMap = new HashMap<>();
    private static ConcurrentHashMap<String, Boolean> mRecoveredFiles = new ConcurrentHashMap<>();

    Intent intentBroadCastActivityUpdate;
    //    boolean isCopying = false;
//    private int counterWatcher = 0, counterUpdate = 0;

    public interface EventListener {
        void onEvent(int event, File file);
    }

    public FileRecoveryObserver(Context context, String path, int mask, EventListener listener) {
        super(path, mask);

        mContext = context;
        mPath = path;
        mMask = mask | FileObserver.CREATE | FileObserver.DELETE_SELF;
        mListener = listener;
        intentBroadCastActivityUpdate = new Intent(INTENT_FILTER_ACTIVITY_UPDATE);

        mObservers.clear();
        mRecoveredFiles.clear();
        recoverInfoHashMap.clear();
        myFileObserverHashMap.clear();
    }

    private void startWatching(File file) {
        synchronized (mObservers) {
            FileObserver observer = mObservers.remove(file);
            if (observer != null) {
                observer.stopWatching();
            }
            observer = new SingleFileObserver(file.getAbsolutePath(), mMask);
            observer.startWatching();
            mObservers.put(file, observer);
            myFileObserverHashMap.put(file, observer);
        }
    }

    @Override
    public void startWatching() {
//        isCopying = true;
//        counterWatcher = 0;
//        counterUpdate = 0;

        Stack<String> stack = new Stack<>();
        stack.push(mPath);

        // Recursively isValidDirectory all child directories
        while (!stack.empty()) {
            String parent = stack.pop();
//            startWatching(parent);

            File path = new File(parent);
            File[] files = path.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (isValidDirectory(file)) {
                        stack.push(file.getAbsolutePath());
                    } else {
                        if (isValidFile(file)) {
//                            if ((stack.empty()) && (i == (files.length - 1))) {
//                                isCopying = false;
//                            }
                            new doObserve(mContext, file).execute();
                        }
                    }
                }
            }
//            counterWatcher = counterWatcher + 1;
        }
    }

    private boolean isValidDirectory(File file) {
        return file.isDirectory() && !file.getName().equals(".") && !file.getName().equals("..");
    }

    private boolean isValidFile(File file) {
        return
                !file.getAbsolutePath().contains(".db")
                        && !file.getAbsolutePath().contains("/.")
                        && !file.getAbsolutePath().contains("/WhatsApp/")
                        && !file.getAbsolutePath().contains("/dbFiles/")
                        && !file.getAbsolutePath().contains("/Android/data/")
                        && !file.getAbsolutePath().contains("/Android/framework/")
                        && !file.getAbsolutePath().contains("/LOST.DIR/")
                        && !file.getAbsolutePath().contains("/.thumbnails/")
                        && !file.getAbsolutePath().contains("/.dthumb/")
                        && !file.getAbsolutePath().contains("/.cache/")
                        && !file.getAbsolutePath().contains("/.trash/")
                        && !file.getAbsolutePath().contains("/.log/")
                        && !file.getAbsolutePath().contains("/color")
                        && !file.getAbsolutePath().contains("/drawable")
                        && !file.getAbsolutePath().contains("/Smartcache/")
                        && !file.getAbsolutePath().contains(AllConstants.HIDDEN_FOLDER_NAME)
//                file.getAbsolutePath().contains(".android_secure")
//                        && !f.getAbsolutePath().contains("/.log/")
//                        && !f.getAbsolutePath().contains("/.log/")
//                        && !f.getAbsolutePath().contains("/.log/")
//                        && !f.getAbsolutePath().contains("/.log/")
//                        && !f.getAbsolutePath().contains("/.log/")
//                        && !f.getAbsolutePath().contains("/.log/")
//                        && !f.getAbsolutePath().contains("/.log/")
//                        && !f.getAbsolutePath().contains("/.log/")
//                                && !fileItem.getAbsolutePath().contains("/.deletedFile")
                ;
    }

    class doObserve extends AsyncTask<String, UpdateScanningProgress, String> {

        private Context mContext;
        private File mFile;
//        private boolean mScanning = false;

        doObserve(Context context, File file) {
            mContext = context;
            mFile = file;
//            mScanning = isCopying;
        }

        @Override
        protected void onPreExecute() {
//            mScanning = false;
        }

        @Override
        protected String doInBackground(String... params) {
            startWatching(mFile);
            publishProgress(new UpdateScanningProgress(mFile));
            return "";
        }

        @Override
        protected void onProgressUpdate(UpdateScanningProgress... values) {
            sendScanningInfoToActivity(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

    private void stopWatching(String path) {
        synchronized (mObservers) {
            FileObserver observer = mObservers.remove(path);
            if (observer != null) {
                observer.stopWatching();
            }
        }
    }

    @Override
    public void stopWatching() {
        synchronized (mObservers) {
            for (FileObserver observer : mObservers.values()) {
                observer.stopWatching();
            }
            mObservers.clear();
        }
    }

    @Override
    public void onEvent(int event, String path) {
        File file;
        if (path == null) {
            file = new File(mPath);
        } else {
            file = new File(mPath, path);
        }
        notify(event, file);
//        printFileObserverMessage(event, path);
    }

    private void notify(int event, File file) {
        if (mListener != null) {
            mListener.onEvent(event & FileObserver.ALL_EVENTS, file);
        }
    }

    private void recover(RecoveryFileInfo recoveryFileInfo) {
//        for (String originPath : recoverInfoHashMap.keySet()) {
        Log.d(TAG, "recovering " + recoveryFileInfo.getOriginFilePath());
        RecoveryFileInfo info = recoverInfoHashMap.get(recoveryFileInfo.getOriginFilePath());
        File recoverFile = new File(info.getRecoveryFilePath());
        if (!recoverFile.exists()) {
            return;
        }
        String MD5 = null;
        try {
            MD5 = MD5Manager.getMd5ByFile(recoverFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (MD5 == null || !MD5.equals(info.getOriginMd5File())) {
            return;
        }
        String path = info.getOriginFilePath().substring(0, info.getOriginFilePath().lastIndexOf("/"));
        File pathFile = new File(path);
        pathFile.mkdirs();
        recoverFile.renameTo(new File(info.getOriginFilePath()));
        Log.d(TAG, info.getRecoveryFilePath() + " renameTo " + info.getOriginFilePath());
//        }
    }

    class doCopy extends AsyncTask<String, UpdateCopyingProgress, String> {

        private Context mContext;
        private RecoveryFileInfo mRecoveryFileInfo;

        doCopy(Context context, RecoveryFileInfo recoveryFileInfo) {
            mContext = context;
            mRecoveryFileInfo = recoveryFileInfo;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            //starting copying process
            Log.e(TAG, "copyFile " + mRecoveryFileInfo.getOriginFilePath() + " to " + mRecoveryFileInfo.getRecoveryFilePath());
            mRecoveredFiles.put(mRecoveryFileInfo.getOriginFilePath(), true);
            FileOutputStream fs = null;

            try {
                int bytesum = 0;
                int byteread = 0;
                fs = new FileOutputStream(mRecoveryFileInfo.getRecoveryFilePath());
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((byteread = mRecoveryFileInfo.getOriginFileInputStream().read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                try {
                    byte[] buffer = new byte[mRecoveryFileInfo.getOriginFileLength() % BUFFER_SIZE];
                    int byteread = mRecoveryFileInfo.getOriginFileInputStream().read(buffer);
                    if (byteread != -1 && fs != null) {
                        fs.write(buffer, 0, byteread);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            } finally {
                if (fs != null) {
                    try {
                        fs.flush();
                        mRecoveryFileInfo.getOriginFileInputStream().close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            String newMD5 = "";
            try {
                newMD5 = MD5Manager.getMd5ByFile(new File(mRecoveryFileInfo.getRecoveryFilePath()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return newMD5;
        }

        @Override
        protected void onProgressUpdate(UpdateCopyingProgress... values) {
        }

        @Override
        protected void onPostExecute(String result) {

            if (!AppUtils.isNullOrEmpty(result)) {

                Log.e(TAG, "copyFile Done " + mRecoveryFileInfo.getOriginFilePath() + " to " + mRecoveryFileInfo.getRecoveryFilePath());
                Log.e(TAG, "new md5 " + result);
                Log.e(TAG, "old md5 " + mRecoveryFileInfo.getOriginMd5File());
                Log.e(TAG, "md5 " + (result.equals(mRecoveryFileInfo.getOriginMd5File()) ? "same" : "diff"));


                if (result.equals(mRecoveryFileInfo.getOriginMd5File())) {

                    //prepare data for saving into database
                    DeletedFileInfo deletedFileInfo = new DeletedFileInfo(mRecoveryFileInfo.getOriginFileName(), mRecoveryFileInfo.getOriginFilePath(),
                            mRecoveryFileInfo.getOriginMd5File(), mRecoveryFileInfo.getOriginFileLength(), mRecoveryFileInfo.getRecoveryMD5FileName(),
                            result, mRecoveryFileInfo.getRecoveryFilePath(), new Date(), mRecoveryFileInfo.isFile());
                    Log.d(TAG, "before save " + deletedFileInfo.toString());

                    //save file to the database
                    if (deletedFileInfo.save()) {
                        Log.d(TAG, deletedFileInfo.getOriginFileName() + " data save successfully into database.");
                        Log.d(TAG, "after save " + deletedFileInfo.toString());
                        for (int i = 0; i < mRecoveryFileInfo.getTags().size(); i++) {
                            // save specific tag under deleted file
                            Tag tag = mRecoveryFileInfo.getTags().get(i);
                            tag.setDeletedFileInfo(deletedFileInfo);
                            if (tag.save()) {
                                Log.d(TAG, "Tag saved into database.");
                            }
                        }
                        List<DeletedFileInfo> files = DataSupport.findAll(DeletedFileInfo.class);
                        Log.d(TAG, "Table items count: " + files.size());
                        for (int i = 0; i < files.size(); i++) {
                            for (int j = 0; j < files.get(i).getTags().size(); j++) {
                                Log.d(TAG, "Tag after save: " + files.get(i).getTags().get(j).getText());
                            }
                        }
                    } else {
                        Log.d(TAG, deletedFileInfo.getOriginFileName() + " data didn\'t save successfully into database.");
                    }
                } else {
                    Log.d(TAG, "NO result found for copying");
                }
            }
        }
    }

    private void copyFile(RecoveryFileInfo recoveryFileInfo) {
        //ensure file exist into watched list
        Boolean doing = mRecoveredFiles.get(recoveryFileInfo.getOriginFilePath());
        if (doing != null && doing) {
            Log.v(TAG, "done");
            return;
        }

        //ensure copy directory
        String directoryPath = HIDDEN_DIRECTORY;
        Log.d(TAG + "Directory: ", directoryPath);
        File directoryFolder = new File(directoryPath);
        if (!directoryFolder.exists()) {
            if (directoryFolder.mkdir()) {
                Log.d(TAG + "Directory: ", "Directory created");
            }
        }

        //do copy
        new doCopy(mContext, recoveryFileInfo).execute();
    }

    class SingleFileObserver extends FileObserver {
        private String filePath;
        private String TAG = SingleFileObserver.class.getSimpleName();
        File fileItem;
        RecoveryFileInfo recoveryFileInfo;

        SingleFileObserver(String path, int mask) {
            super(path, mask);
            filePath = path;

            if (filePath != null) {
                Log.d(TAG, "filePath: " + filePath);
                fileItem = new File(filePath);
                //Store file info

                FileInputStream originFileInputStream = null;
                try {
                    String originFileName = fileItem.getName();
                    final String originFilePath = fileItem.getAbsolutePath();
                    String originMd5File = MD5Manager.getMd5ByFile(new File(fileItem.getAbsolutePath()));
                    originFileInputStream = new FileInputStream(fileItem.getAbsolutePath());
                    int originFileLength = originFileInputStream.available();
                    String recoveryMD5FileName = MD5Manager.getMD5Str(fileItem.getAbsolutePath());
                    String recoveryFilePath = AllConstants.HIDDEN_DIRECTORY + File.separator + recoveryMD5FileName;
                    //set tag
                    ArrayList<Tag> fileTag = new ArrayList<Tag>();
                    fileTag.add(AppUtils.getFileTag(mContext, originFilePath));
                    for (int i = 0; i < fileTag.size(); i++) {
                        Log.d(TAG, "Tag to be stored into hashmap: " + i + " is: " + fileTag.get(i));
                    }

                    recoveryFileInfo = new RecoveryFileInfo(originFileName, originFilePath, originMd5File,
                            originFileInputStream, originFileLength, recoveryMD5FileName, recoveryFilePath, new Date(), true, fileTag);
                    recoverInfoHashMap.put(originFilePath, recoveryFileInfo);
                    Log.d(TAG, recoveryFileInfo.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                //FileInputStream cann't be closed here, otherwise it will create different md5 due to "File descriptor closed"
            }
        }

        @Override
        public void onEvent(int event, String path) {
//            if (event == FileObserver.ACCESS) return;

            File file;
            if (path == null) {
                file = new File(filePath);
            } else {
                file = new File(filePath, path);
            }

            switch (event & FileObserver.ALL_EVENTS) {
                case FileObserver.ATTRIB:
                    copyFile(recoveryFileInfo);
//                    recover(recoveryFileInfo);
                    break;
                case FileObserver.DELETE:
                    copyFile(recoveryFileInfo);
//                    recover(recoveryFileInfo);
                    break;
                case FileObserver.DELETE_SELF:
                    copyFile(recoveryFileInfo);
//                    recover(recoveryFileInfo);
                    FileRecoveryObserver.this.stopWatching(filePath);
                    break;
                case CREATE:
                    if (isValidDirectory(file)) {
//                        FileRecoveryObserver.this.startWatching(file);
                        new doObserve(mContext, file).execute();
                    }
                    break;
                case 32768:
                    stopWatching();
                    if (file.exists()) {
//                        SingleFileObserver fileObserver = new SingleFileObserver(filePath, mMask);
//                        fileObserver.startWatching();
//                        myFileObserverHashMap.put(file, fileObserver);
                        new doObserve(mContext, file).execute();
                    } else {
                        myFileObserverHashMap.remove(file);
                    }
                    break;
                default:
                    break;
            }

            FileRecoveryObserver.this.notify(event, file);
        }
    }

    private synchronized void sendScanningInfoToActivity(UpdateScanningProgress updateScanningProgress) {
//        counterUpdate = counterUpdate + 1;
//        if (counterUpdate == counterWatcher) {
//            updateScanningProgress.setCopying(false);
//            counterUpdate = 0;
//            counterWatcher = 0;
//        } else {
//            updateScanningProgress.setCopying(true);
//        }
//        updateScanningProgress.setCopying(isCopying);
        intentBroadCastActivityUpdate.putExtra(KEY_INTENT_EXTRA_UPDATE, updateScanningProgress);
        mContext.sendBroadcast(intentBroadCastActivityUpdate);
    }
}