package com.reversecoder.rb.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.reversecoder.rb.R;
import com.reversecoder.rb.model.Tag;
import com.yalantis.filter.adapter.FilterAdapter;
import com.yalantis.filter.widget.FilterItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class DeletedFileFilterAdapter extends FilterAdapter<Tag> {

    private Context mContext;
    private int[] mColors;

    public DeletedFileFilterAdapter(Context context, @NotNull ArrayList<? extends Tag> items) {
        super(items);
        mContext = context;
        mColors = mContext.getResources().getIntArray(R.array.colors);
    }

    @NotNull
    @Override
    public FilterItem createView(int position, Tag item) {
        FilterItem filterItem = new FilterItem(mContext);

        filterItem.setStrokeColor(mColors[0]);
        filterItem.setTextColor(mColors[0]);
//            filterItem.setCornerRadius(14);
        filterItem.setCheckedTextColor(ContextCompat.getColor(mContext, android.R.color.white));
        filterItem.setColor(ContextCompat.getColor(mContext, android.R.color.white));
        filterItem.setCheckedColor(mColors[position]);
        filterItem.setText(item.getText());
        filterItem.deselect();

        return filterItem;
    }
}