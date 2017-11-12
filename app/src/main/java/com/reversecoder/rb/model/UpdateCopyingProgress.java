package com.reversecoder.rb.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class UpdateCopyingProgress implements Parcelable {

    private File file;
    private boolean isCopying = false;

    public UpdateCopyingProgress(File file, boolean isCopying) {
        this.file = file;
        this.isCopying = isCopying;
    }

    public UpdateCopyingProgress(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isCopying() {
        return isCopying;
    }

    public void setCopying(boolean copying) {
        isCopying = copying;
    }

    /**************************
     * Methods for parcelable *
     **************************/
    private UpdateCopyingProgress(Parcel in) {
        this.file = (File) in.readValue(File.class.getClassLoader());
        this.isCopying = in.readInt() == 1;
        ;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(file);
        dest.writeInt(isCopying ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UpdateCopyingProgress> CREATOR = new Creator<UpdateCopyingProgress>() {
        public UpdateCopyingProgress createFromParcel(Parcel in) {
            return new UpdateCopyingProgress(in);
        }

        public UpdateCopyingProgress[] newArray(int size) {
            return new UpdateCopyingProgress[size];
        }
    };
}
