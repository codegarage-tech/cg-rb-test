package com.reversecoder.rb.toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.reversecoder.rb.R;
import com.reversecoder.rb.activity.HomeActivity;
import com.reversecoder.rb.adapter.DeleteFileWithAdvertiseAdapter;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class ToolbarActionModeCallback<T> implements ActionMode.Callback {

    private Context context;
    private DeleteFileWithAdvertiseAdapter deletedFileAdapter;

    public ToolbarActionModeCallback(Context context, DeleteFileWithAdvertiseAdapter listViewAdapter) {
        this.context = context;
        this.deletedFileAdapter = listViewAdapter;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_selected, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
        //So here show action menu according to SDK Levels
        if (Build.VERSION.SDK_INT < 11) {
            MenuItemCompat.setShowAsAction(menu.findItem(R.id.action_delete), MenuItemCompat.SHOW_AS_ACTION_NEVER);
        } else {
            menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_delete:
                showDeleteConfirmationDialog(menuItem);
                break;

        }
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {
        //When action mode destroyed remove selected selections and set action mode to null
        //First check current fragment action mode
        // remove selection
        deletedFileAdapter.removeSelection();
        ((HomeActivity) context).setNullToActionMode();
    }

    private void showDeleteConfirmationDialog(final MenuItem menuItem) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //animate menu item
                        ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Drawable drawable = menuItem.getIcon();
                                if (drawable instanceof Animatable) {
                                    ((Animatable) drawable).start();
                                }
                            }
                        }, 3000);

                        //delete recyclerview item
                        ((HomeActivity) context).deleteRows();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        String selectedItem = deletedFileAdapter.getSelectedCount() > 1 ? deletedFileAdapter.getSelectedCount() + " items?" : deletedFileAdapter.getSelectedCount() + " item?";

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.dialog_attention))
                .setMessage(context.getString(R.string.dialog_message) + " " + selectedItem)
                .setPositiveButton(context.getString(R.string.dialog_button_ok), dialogClickListener)
                .setNegativeButton(context.getString(R.string.dialog_button_cancel), dialogClickListener)
                .show();
    }
}
