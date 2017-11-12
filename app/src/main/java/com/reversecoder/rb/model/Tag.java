package com.reversecoder.rb.model;

import android.support.annotation.NonNull;

import com.yalantis.filter.model.FilterModel;

import org.litepal.crud.DataSupport;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class Tag extends DataSupport implements FilterModel {

    private long id;
    private String text;
    private int color;
    private DeletedFileInfo deletedFileInfo;

    public Tag(String text, int color, DeletedFileInfo deletedFileInfo) {
        this.text = text;
        this.color = color;
        this.deletedFileInfo = deletedFileInfo;
    }

    public Tag(String text, int color) {
        this.text = text;
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public DeletedFileInfo getDeletedFileInfo() {
        return deletedFileInfo;
    }

    public void setDeletedFileInfo(DeletedFileInfo deletedFileInfo) {
        this.deletedFileInfo = deletedFileInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;

        Tag tag = (Tag) o;

        if (getColor() != tag.getColor()) return false;
        return getText().equals(tag.getText());

    }

    @Override
    public int hashCode() {
        int result = getText().hashCode();
        result = 31 * result + getColor();
        return result;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", color=" + color +
                ", deletedFileInfo=" + deletedFileInfo +
                '}';
    }
}
