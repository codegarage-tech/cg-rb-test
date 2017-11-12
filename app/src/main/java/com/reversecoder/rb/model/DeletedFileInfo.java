package com.reversecoder.rb.model;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeletedFileInfo extends DataSupport {

    private long id;
    private String originFileName;
    private String originFilePath;
    private String originMd5File;
    private int originFileLength = -1;
    private String recoveryMD5FileName;
    private String recoveryMD5File;
    private String recoveryFilePath;
    private Date deletedDate;
    private boolean isFile = true;
    private ArrayList<Tag> tags = new ArrayList<Tag>();

    public DeletedFileInfo(String originFileName, String originFilePath, String originMd5File, int originFileLength, String recoveryMD5FileName, String recoveryMD5File, String recoveryFilePath, Date deletedDate, boolean isFile) {
        this.originFileName = originFileName;
        this.originFilePath = originFilePath;
        this.originMd5File = originMd5File;
        this.originFileLength = originFileLength;
        this.recoveryMD5FileName = recoveryMD5FileName;
        this.recoveryMD5File = recoveryMD5File;
        this.recoveryFilePath = recoveryFilePath;
        this.deletedDate = deletedDate;
        this.isFile = isFile;
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

    public String getRecoveryMD5File() {
        return recoveryMD5File;
    }

    public void setRecoveryMD5File(String recoveryMD5File) {
        this.recoveryMD5File = recoveryMD5File;
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
        List<Tag> mTags= DataSupport.where("deletedFileInfo_id = ?", getId()+"").find(Tag.class);
        return new ArrayList<Tag>(mTags);
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public boolean hasTag(String string) {
        for (Tag tag : getTags()) {
            if (tag.getText().equals(string)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeletedFileInfo)) return false;

        DeletedFileInfo deletedFile = (DeletedFileInfo) o;

        if (deletedFile.isFile()) {

            if (getOriginFileName() != null ? !getOriginFileName().equals(deletedFile.getOriginFileName()) : deletedFile.getOriginFileName() != null)
                return false;
            if (getOriginFilePath() != null ? !getOriginFilePath().equals(deletedFile.getOriginFilePath()) : deletedFile.getOriginFilePath() != null)
                return false;
            if (getOriginMd5File() != null ? !getOriginMd5File().equals(deletedFile.getOriginMd5File()) : deletedFile.getOriginMd5File() != null)
                return false;
            if (getOriginFileLength() != -1 ? getOriginFileLength() != deletedFile.getOriginFileLength() : deletedFile.getOriginFileLength() != -1)
                return false;
            if (getRecoveryMD5FileName() != null ? !getRecoveryMD5FileName().equals(deletedFile.getRecoveryMD5FileName()) : deletedFile.getRecoveryMD5FileName() != null)
                return false;
            if (getRecoveryMD5File() != null ? !getRecoveryMD5File().equals(deletedFile.getRecoveryMD5File()) : deletedFile.getRecoveryMD5File() != null)
                return false;
            if (getRecoveryFilePath() != null ? !getRecoveryFilePath().equals(deletedFile.getRecoveryFilePath()) : deletedFile.getRecoveryFilePath() != null)
                return false;
            if (getDeletedDate() != null ? !getDeletedDate().equals(deletedFile.getDeletedDate()) : deletedFile.getDeletedDate() != null)
                return false;
//            if (!isFile() ? isFile() != deletedFile.isFile() : !deletedFile.isFile())
//                return false;
        }
        return getTags() != null ? getTags().equals(deletedFile.getTags()) : deletedFile.getTags() == null;

    }

    @Override
    public int hashCode() {
        int result = getOriginFileName() != null ? getOriginFileName().hashCode() : 0;
        result = 31 * result + (getOriginFilePath() != null ? getOriginFilePath().hashCode() : 0);
        result = 31 * result + (getOriginMd5File() != null ? getOriginMd5File().hashCode() : 0);
        result = 31 * result + (getOriginFileLength() != -1 ? getOriginFileLength() : 0);
        result = 31 * result + (getRecoveryMD5FileName() != null ? getRecoveryMD5FileName().hashCode() : 0);
        result = 31 * result + (getRecoveryMD5File() != null ? getRecoveryMD5File().hashCode() : 0);
        result = 31 * result + (getRecoveryFilePath() != null ? getRecoveryFilePath().hashCode() : 0);
        result = 31 * result + (getDeletedDate() != null ? getDeletedDate().hashCode() : 0);
        result = 31 * result + (getTags() != null ? getTags().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DeletedFileInfo{" +
                "id=" + id +
                ", originFileName='" + originFileName + '\'' +
                ", originFilePath='" + originFilePath + '\'' +
                ", originMd5File='" + originMd5File + '\'' +
                ", originFileLength=" + originFileLength +
                ", recoveryMD5FileName='" + recoveryMD5FileName + '\'' +
                ", recoveryMD5File='" + recoveryMD5File + '\'' +
                ", recoveryFilePath='" + recoveryFilePath + '\'' +
                ", deletedDate=" + deletedDate +
                ", isFile=" + isFile +
                ", tags=" + tags +
                '}';
    }
}