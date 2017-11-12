package com.reversecoder.rb.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.FileObserver;
import android.util.Log;

import com.reversecoder.rb.R;
import com.reversecoder.rb.enumeration.TagType;
import com.reversecoder.rb.model.Tag;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class AppUtils {

    public static boolean isNullOrEmpty(String myString) {
        return myString == null ? true : myString.length() == 0 || myString.equalsIgnoreCase("null") || myString.equalsIgnoreCase("");
    }

    public static String getTagName(Class<?> cls) {
        return cls.getSimpleName();
    }

    public static String printFileObserverMessage(int event, String path) {
        String message = "No message";
        switch (event) {
            case FileObserver.ATTRIB:
                message = "event = " + "File Attrib" + "\npath = " + path;
                break;
            case FileObserver.CREATE:
                message = "event = " + "File Created" + "\npath = " + path;
                break;
            case FileObserver.DELETE:
                message = "event = " + "File Deleted" + "\npath = " + path;
                break;
            case FileObserver.DELETE_SELF:
                message = "event = " + "File Deleted Self" + "\npath = " + path;
                break;
            case FileObserver.MODIFY:
                message = "event = " + "File Modified" + "\npath = " + path;
                break;
            case FileObserver.MOVED_FROM:
                message = "event = " + "File Moved From" + "\npath = " + path;
                break;
            case FileObserver.MOVED_TO:
                message = "event = " + "File Moved To" + "path = " + path;
                break;
            case FileObserver.MOVE_SELF:
                message = "event = " + "File Moved Self" + "\npath = " + path;
                break;
        }

        Log.d("FileRecoveryObserver: ", message);

        return message;
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getFileExtension(String filePath) {

        final char EXTENSION_SEPARATOR = '.';
        final char UNIX_SEPARATOR = '/';
        final char WINDOWS_SEPARATOR = '\\';

        if (filePath != null) {

            int lastUnixPos = filePath.lastIndexOf(UNIX_SEPARATOR);
            int lastWindowsPos = filePath.lastIndexOf(WINDOWS_SEPARATOR);

            int extensionPos = filePath.lastIndexOf(EXTENSION_SEPARATOR);
            int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);

            int index = lastSeparator > extensionPos ? -1 : extensionPos;
            if (index == -1) {
                return "";
            } else {
                return filePath.substring(index + 1);
            }
        }
        return null;
    }

    public static boolean isDesiredFileType(String filePath, TagType tagType) {
        switch (tagType) {
            case IMAGE:
                if (filePath.endsWith(".png") || filePath.endsWith(".jpg")
                        || filePath.endsWith(".jpeg") || filePath.endsWith(".gif")) {
                    return true;
                }
                break;
            case MUSIC:
                if (filePath.endsWith(".mp3") || filePath.endsWith(".aac")
                        || filePath.endsWith(".amr") || filePath.endsWith(".m4r")) {
                    return true;
                }
                break;
            case MOVIE:
                if (filePath.endsWith(".mp4") || filePath.endsWith(".flv")
                        || filePath.endsWith(".avi") || filePath.endsWith(".mkv")
                        || filePath.endsWith(".wmv") || filePath.endsWith(".webm")
                        || filePath.endsWith(".3gp") || filePath.endsWith(".dat")
                        || filePath.endsWith(".swf") || filePath.endsWith(".mov")
                        || filePath.endsWith(".vob") || filePath.endsWith(".mpg")
                        || filePath.endsWith(".mpeg") || filePath.endsWith(".mpeg1")
                        || filePath.endsWith(".mpeg2") || filePath.endsWith(".mpeg3")
                        || filePath.endsWith(".mpeg3") || filePath.endsWith(".m4v")) {
                    return true;
                }
                break;
            case DOCUMENT:
                if (filePath.endsWith(".pdf") || filePath.endsWith(".txt")
                        || filePath.endsWith(".doc") || filePath.endsWith(".docx")
                        || filePath.endsWith(".xls") || filePath.endsWith(".xlsx")
                        || filePath.endsWith(".ppt") || filePath.endsWith(".pptx")
                        || filePath.endsWith(".odt") || filePath.endsWith(".ini")
                        || filePath.endsWith(".java") || filePath.endsWith(".html")
                        || filePath.endsWith(".htm") || filePath.endsWith(".css")
                        || filePath.endsWith(".cpp") || filePath.endsWith(".cs")
                        || filePath.endsWith(".php") || filePath.endsWith(".xml")
                        || filePath.endsWith(".ods") || filePath.endsWith(".csv")
                        || filePath.endsWith(".db") || filePath.endsWith(".srt")
                        || filePath.endsWith(".js") || filePath.endsWith(".php")) {
                    return true;
                }
                break;
            case APK:
                if (filePath.endsWith(".apk")) {
                    return true;
                }
                break;
            case ZIP:
                if (filePath.endsWith(".zip")) {
                    return true;
                }
                break;
            case OTHER:
                if ((!isDesiredFileType(filePath, TagType.IMAGE)) && (!isDesiredFileType(filePath, TagType.MUSIC))
                        && (!isDesiredFileType(filePath, TagType.MOVIE)) && (!isDesiredFileType(filePath, TagType.DOCUMENT))
                        && (!isDesiredFileType(filePath, TagType.ZIP)) && (!isDesiredFileType(filePath, TagType.APK))) {
                    return true;
                }
        }
        return false;
    }

    public static TagType getTagType(String filePath) {
        TagType tagType = TagType.OTHER;
        if (isDesiredFileType(filePath, TagType.IMAGE)) {
            tagType = TagType.IMAGE;
        } else if (isDesiredFileType(filePath, TagType.MUSIC)) {
            tagType = TagType.MUSIC;
        } else if (isDesiredFileType(filePath, TagType.MOVIE)) {
            tagType = TagType.MOVIE;
        } else if (isDesiredFileType(filePath, TagType.DOCUMENT)) {
            tagType = TagType.DOCUMENT;
        } else if (isDesiredFileType(filePath, TagType.ZIP)) {
            tagType = TagType.ZIP;
        } else if (isDesiredFileType(filePath, TagType.APK)) {
            tagType = TagType.APK;
        } else if (isDesiredFileType(filePath, TagType.OTHER)) {
            tagType = TagType.OTHER;
        }
        return tagType;
    }

    public static Tag getFileTag(Context context, String filePath) {

        int[] mColors = context.getResources().getIntArray(R.array.colors);
        String[] mTitles = context.getResources().getStringArray(R.array.deleted_file_tag);

        Tag tag = null;
        switch (getTagType(filePath)) {
            case ALL_CATEGORIES:
                tag = new Tag(mTitles[0], mColors[0]);
                break;
            case IMAGE:
                tag = new Tag(mTitles[1], mColors[1]);
                break;
            case MUSIC:
                tag = new Tag(mTitles[2], mColors[2]);
                break;
            case MOVIE:
                tag = new Tag(mTitles[3], mColors[3]);
                break;
            case DOCUMENT:
                tag = new Tag(mTitles[4], mColors[4]);
                break;
            case ZIP:
                tag = new Tag(mTitles[5], mColors[5]);
                break;
            case APK:
                tag = new Tag(mTitles[6], mColors[6]);
                break;
            case OTHER:
                tag = new Tag(mTitles[7], mColors[7]);
                break;
        }
        return tag;
    }

    public static String convertInputStreamToString(InputStream inputStream) {

        String strInputStream = "";
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            strInputStream = sb.toString();
        } catch (Exception ex) {
            strInputStream = "";
            ex.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return strInputStream;
    }

    public static InputStream convertStringToInputStream(String text) {
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(text.getBytes("UTF-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return inputStream;
    }
}
