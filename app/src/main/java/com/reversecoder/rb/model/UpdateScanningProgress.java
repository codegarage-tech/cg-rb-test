package com.reversecoder.rb.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class UpdateScanningProgress implements Parcelable {

    private File file;
    private boolean isScanning = false;

    public UpdateScanningProgress(File file, boolean isScanning) {
        this.file = file;
        this.isScanning = isScanning;
    }

    public UpdateScanningProgress(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isScanning() {
        return isScanning;
    }

    public void setScanning(boolean scanning) {
        isScanning = scanning;
    }

    /**************************
     * Methods for parcelable *
     **************************/
    private UpdateScanningProgress(Parcel in) {
        this.file = (File) in.readValue(File.class.getClassLoader());
        this.isScanning = in.readInt() == 1;
        ;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(file);
        dest.writeInt(isScanning ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<UpdateScanningProgress> CREATOR = new Parcelable.Creator<UpdateScanningProgress>() {
        public UpdateScanningProgress createFromParcel(Parcel in) {
            return new UpdateScanningProgress(in);
        }

        public UpdateScanningProgress[] newArray(int size) {
            return new UpdateScanningProgress[size];
        }
    };
}
