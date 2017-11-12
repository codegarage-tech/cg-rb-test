package com.reversecoder.rb.model;

import org.litepal.annotation.Column;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;

public class RecoveryFileInfo {

    private long id;
    private String originFileName;
    private String originFilePath;
    private String originMd5File;
    @Column(nullable = true)
    private FileInputStream originFileInputStream;
    private int originFileLength = -1;
    private String recoveryMD5FileName;
    private String recoveryFilePath;
    private Date deletedDate;
    private boolean isFile = true;
    private ArrayList<Tag> tags;

    public RecoveryFileInfo(String originFileName, String originFilePath, String originMd5File, FileInputStream originFileInputStream, int originFileLength, String recoveryMD5FileName, String recoveryFilePath, Date deletedDate, boolean isFile, ArrayList<Tag> tags) {
        this.originFileName = originFileName;
        this.originFilePath = originFilePath;
        this.originMd5File = originMd5File;
        this.originFileInputStream = originFileInputStream;
        this.originFileLength = originFileLength;
        this.recoveryMD5FileName = recoveryMD5FileName;
        this.recoveryFilePath = recoveryFilePath;
        this.deletedDate = deletedDate;
        this.isFile = isFile;
        this.tags = tags;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOriginFileName() {
        return originFileName;
    }

    public void setOriginFileName(String originFileName) {
        this.originFileName = originFileName;
    }

    public String getOriginFilePath() {
        return originFilePath;
    }

    public void setOriginFilePath(String originFilePath) {
        this.originFilePath = originFilePath;
    }

    public String getOriginMd5File() {
        return originMd5File;
    }

    public void setOriginMd5File(String originMd5File) {
        this.originMd5File = originMd5File;
    }

    public FileInputStream getOriginFileInputStream() {
        return originFileInputStream;
    }

    public void setOriginFileInputStream(FileInputStream originFileInputStream) {
        this.originFileInputStream = originFileInputStream;
    }

    public int getOriginFileLength() {
        return originFileLength;
    }

    public void setOriginFileLength(int originFileLength) {
        this.originFileLength = originFileLength;
    }

    public String getRecoveryMD5FileName() {
        return recoveryMD5FileName;
    }

    public void setRecoveryMD5FileName(String recoveryMD5FileName) {
        this.recoveryMD5FileName = recoveryMD5FileName;
    }

    public String getRecoveryFilePath() {
        return recoveryFilePath;
    }

    public void setRecoveryFilePath(String recoveryFilePath) {
        this.recoveryFilePath = recoveryFilePath;
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "RecoveryFileInfo{" +
                "id=" + id +
                ", originFileName='" + originFileName + '\'' +
                ", originFilePath='" + originFilePath + '\'' +
                ", originMd5File='" + originMd5File + '\'' +
                ", originFileInputStream=" + originFileInputStream +
                ", originFileLength=" + originFileLength +
                ", recoveryMD5FileName='" + recoveryMD5FileName + '\'' +
                ", recoveryFilePath='" + recoveryFilePath + '\'' +
                ", deletedDate=" + deletedDate +
                ", isFile=" + isFile +
                ", tags=" + tags +
                '}';
    }
}