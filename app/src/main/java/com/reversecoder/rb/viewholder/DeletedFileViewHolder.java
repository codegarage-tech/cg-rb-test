package com.reversecoder.rb.viewholder;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.reversecoder.rb.R;
import com.reversecoder.rb.model.DeletedFileInfo;
import com.reversecoder.rb.model.Tag;

import static com.reversecoder.rb.adapter.DeleteFileWithAdvertiseAdapter.mSelectedItemsIds;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class DeletedFileViewHolder extends BaseViewHolder<DeletedFileInfo> {

    TextView txtName;
    TextView txtLocation;
    LinearLayout imageBackground;

    public DeletedFileViewHolder(ViewGroup parent) {
        super(parent, R.layout.recyclerview_item_deleted_file);
        txtName = $(R.id.txt_name);
        txtLocation = $(R.id.txt_location);
        imageBackground = $(R.id.ll_image_bg);
    }

    @Override
    public void setData(final DeletedFileInfo deletedFile) {
        Log.i("ViewHolder", "position" + getDataPosition());
        txtName.setText(deletedFile.getOriginFileName());
        txtLocation.setText(deletedFile.getOriginFilePath());
        txtLocation.setSelected(true);

        if (deletedFile.getTags().size() > 0) {
            Tag firstTag = deletedFile.getTags().get(0);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(10);
            drawable.setColor(firstTag.getColor());
            imageBackground.setBackgroundDrawable(drawable);
        }

        //Toolbar ActionMode
        itemView.setBackgroundColor(mSelectedItemsIds.get(getDataPosition()) ? Color.parseColor("#212121") : Color.TRANSPARENT);
    }
}
