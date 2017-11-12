package com.reversecoder.rb.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.reversecoder.rb.model.DeletedFileInfo;
import com.reversecoder.rb.viewholder.AdvertiseViewHolder;
import com.reversecoder.rb.viewholder.DeletedFileViewHolder;

import java.security.InvalidParameterException;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class DeleteFileWithAdvertiseAdapter extends RecyclerArrayAdapter<DeletedFileInfo> {

    public static final int TYPE_INVALID = 0;
    public static final int TYPE_ADMOB = 1;
    public static final int TYPE_DELETED_FILE = 2;

    //Toolbar ActionMode
    public static SparseBooleanArray mSelectedItemsIds;

    public DeleteFileWithAdvertiseAdapter(Context context) {
        super(context);

        //Toolbar ActionMode
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public int getViewType(int position) {
        DeletedFileInfo deletedFile = getItem(position);
        if (!deletedFile.isFile()) {
            return TYPE_ADMOB;
        } else if (deletedFile.isFile()) {
            return TYPE_DELETED_FILE;
        }
        return TYPE_INVALID;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_DELETED_FILE:
                return new DeletedFileViewHolder(parent);
            case TYPE_ADMOB:
                return new AdvertiseViewHolder(parent);
            default:
                throw new InvalidParameterException();
        }
    }

    /******************************
     * Toolbar actionmode methods *
     ******************************/

    //Toggle selection methods
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }


    //Remove selected selections
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    //Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}