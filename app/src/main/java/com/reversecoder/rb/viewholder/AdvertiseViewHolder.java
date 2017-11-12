package com.reversecoder.rb.viewholder;

import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.reversecoder.rb.R;
import com.reversecoder.rb.model.DeletedFileInfo;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class AdvertiseViewHolder extends BaseViewHolder<DeletedFileInfo> {

    public TextView txtAdvertiseName;

    public AdvertiseViewHolder(ViewGroup parent) {
        super(parent, R.layout.recyclerview_item_advertise);

        txtAdvertiseName = $(R.id.txt_advertise_name);
    }

    @Override
    public void setData(final DeletedFileInfo data) {
        txtAdvertiseName.setText(data.getOriginFileName());
    }
}
