package com.reversecoder.rb.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.alexvasilkov.foldablelayout.shading.GlanceFoldShading;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.rb.R;
import com.reversecoder.rb.adapter.DeleteFileWithAdvertiseAdapter;
import com.reversecoder.rb.adapter.DeletedFileFilterAdapter;
import com.reversecoder.rb.model.DeletedFileInfo;
import com.reversecoder.rb.model.Tag;
import com.reversecoder.rb.model.UpdateScanningProgress;
import com.reversecoder.rb.service.FileRecoveryService;
import com.reversecoder.rb.toolbar.ToolbarActionModeCallback;
import com.reversecoder.rb.util.AllConstants;
import com.yalantis.filter.animator.FiltersListItemAnimator;
import com.yalantis.filter.listener.FilterListener;
import com.yalantis.filter.widget.Filter;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import org.jetbrains.annotations.NotNull;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.reversecoder.rb.util.AllConstants.INTENT_FILTER_ACTIVITY_UPDATE;
import static com.reversecoder.rb.util.AllConstants.KEY_INTENT_EXTRA_UPDATE;
import static com.reversecoder.rb.util.AllConstants.isFreeApp;
import static com.reversecoder.rb.util.AppUtils.isServiceRunning;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class HomeActivity extends AppCompatActivity implements FilterListener<Tag> {

    private static final long RIPPLE_DURATION = 250;
    Toolbar toolbar;
    FrameLayout root;
    ImageView ivContentHamburger, ivContentBack, ivListToGrid;

    // Guillotine menu
    View guillotineMenu;
    GuillotineAnimation guillotineAnimation;
    TextView tvTitle;
    LinearLayout llLogOut, llHome;

    //list to grid
    EasyRecyclerView recyclerViewDeletedFile;
    DeleteFileWithAdvertiseAdapter deletedFileAdapter;
    GridLayoutManager gridLayoutManager;

    //filter
    private int[] mColors;
    private String[] mTitles;
    private Filter<Tag> mFilter;
    private ArrayList<DeletedFileInfo> mAllDeletedFiles;

    //toolbar actionmode
    private ActionMode mActionMode;

    //foldablelayout
    private View listTouchInterceptor;
    private LinearLayout detailsLayout;
    private UnfoldableView unfoldableView;

    //File observer service
    Intent intentOserverService;
    RelativeLayout relativeLayoutScanner;
    ImageView ivClose;
    TextView tvScanningTitle, tvScanningValue;

    String TAG = HomeActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUI();

        initAction();
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        root = (FrameLayout) findViewById(R.id.root);
        ivContentHamburger = (ImageView) findViewById(R.id.iv_content_hamburger);
        ivContentBack = (ImageView) findViewById(R.id.iv_content_back);
        ivListToGrid = (ImageView) findViewById(R.id.iv_list_to_grid);
        tvTitle = (TextView) findViewById(R.id.txt_title);

        ivContentHamburger.setVisibility(View.VISIBLE);
        tvTitle.setText(getString(R.string.title_activity_home));

        initMenu();

        initFilter();

        recyclerViewDeletedFile = (EasyRecyclerView) findViewById(R.id.rv_deleted_file);
        deletedFileAdapter = new DeleteFileWithAdvertiseAdapter(this);
        gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerViewDeletedFile.setLayoutManager(gridLayoutManager);
        recyclerViewDeletedFile.setAdapterWithProgress(deletedFileAdapter);
        deletedFileAdapter.addAll(mAllDeletedFiles = getAllDeletedDataWithAdvertise());
        deletedFileAdapter.notifyDataSetChanged();
        recyclerViewDeletedFile.setItemAnimator(new FiltersListItemAnimator());

        initFoldableLayout();

        initFileObserver();
    }

    private void initAction() {
        initMenuAction();
        iniToolbarAction();
        initRecyclerViewAction();
    }

    @Override
    public void onBackPressed() {
        if (unfoldableView != null && (unfoldableView.isUnfolded() || unfoldableView.isUnfolding())) {
            unfoldableView.foldBack();
        } else if (guillotineAnimation.isOpened()) {
            guillotineAnimation.close();
        } else {
            super.onBackPressed();
        }
    }

    /****************************
     * EasyRecyclerView methods *
     ****************************/
    private void initRecyclerViewAction() {
        deletedFileAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //toolbar actionmode
                if (deletedFileAdapter.getAllData().get(position).isFile()) {
                    if (mActionMode != null) {
                        onListItemSelect(position);
                    } else {
                        openDetails(detailsLayout, (view.findViewById(R.id.item_view)), deletedFileAdapter.getItem(position));
//                    Toast.makeText(HomeActivity.this, deletedFileAdapter.getAllData().get(position).getFileName(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        deletedFileAdapter.setOnItemLongClickListener(new RecyclerArrayAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                //toolbar actionmode
                if (deletedFileAdapter.getAllData().get(position).isFile()) {
                    onListItemSelect(position);
                }
                return true;
            }
        });
    }

    /****************
     * Menu methods *
     ****************/
    private void initMenu() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        guillotineMenu = LayoutInflater.from(this).inflate(R.layout.layout_menu, null);
        root.addView(guillotineMenu);

        guillotineAnimation = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.iv_menu_hamburger), ivContentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();

        llLogOut = (LinearLayout) findViewById(R.id.ll_logout);
        llHome = (LinearLayout) findViewById(R.id.ll_home);
    }

    private void initMenuAction() {
//        llLogOut.setOnClickListener(new OnSingleClickListener() {
//            @Override
//            public void onSingleClick(View view) {
//                SessionManager.setBooleanSetting(HomeActivity.this, SESSION_IS_USER_LOGGED_IN, false);
//                Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
//                startActivity(logoutIntent);
//                finish();
//            }
//        });
//
        llHome.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                closeMenu();
            }
        });
    }

    private void closeMenu() {
        if (guillotineAnimation != null) {
            if (guillotineAnimation.isOpened()) {
                guillotineAnimation.close();
            }
        }

        //Menu Action is again initialized, becuause after closing menu home ui lost previously set actions
        initMenuAction();
    }

    /************************
     * List to grid methods *
     ************************/
    private void iniToolbarAction() {
        ivListToGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((Animatable) view.getBackground()).isRunning()) {
                    if (gridLayoutManager.getSpanCount() == 1) {
                        view.setBackground(AnimatedVectorDrawableCompat.create(HomeActivity.this, R.drawable.avd_list_to_grid));
                        gridLayoutManager.setSpanCount(2);
                    } else {
                        view.setBackground(AnimatedVectorDrawableCompat.create(HomeActivity.this, R.drawable.avd_grid_to_list));
                        gridLayoutManager.setSpanCount(1);
                    }
                    ((Animatable) view.getBackground()).start();
//                    deletedFileAdapter.notifyItemRangeChanged(0, deletedFileAdapter.getItemCount());
                    deletedFileAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /*****************
     * Filter methods *
     ******************/
    private void initFilter() {
        mColors = getResources().getIntArray(R.array.colors);
        mTitles = getResources().getStringArray(R.array.deleted_file_tag);

        mFilter = (Filter<Tag>) findViewById(R.id.filter);
        mFilter.setAdapter(new DeletedFileFilterAdapter(HomeActivity.this, getTags()));
        mFilter.setListener(this);

        //the text to show when there's no selected items
        mFilter.setNoSelectedItemText(getString(R.string.str_all_selected));
        mFilter.build();
    }

    private void calculateDiff(final ArrayList<DeletedFileInfo> oldList, final ArrayList<DeletedFileInfo> newList) {
        DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }
        }).dispatchUpdatesTo(deletedFileAdapter);
    }

    private ArrayList<Tag> getTags() {
        ArrayList<Tag> tags = new ArrayList<Tag>();

        for (int i = 0; i < mTitles.length; ++i) {
            tags.add(new Tag(mTitles[i], mColors[i]));
        }

        return tags;
    }

    private ArrayList<DeletedFileInfo> findByTags(ArrayList<Tag> tags) {
        ArrayList<DeletedFileInfo> deletedFiles = new ArrayList<DeletedFileInfo>();

        for (DeletedFileInfo deletedFile : mAllDeletedFiles) {
            for (Tag tag : tags) {
                if (deletedFile.hasTag(tag.getText()) && !deletedFiles.contains(deletedFile)) {
                    deletedFiles.add(deletedFile);
                }
            }
        }

        return deletedFiles;
    }

    private ArrayList<DeletedFileInfo> getAllDeletedData() {

        List<DeletedFileInfo> mData = DataSupport.findAll(DeletedFileInfo.class);
        Log.d(TAG, "Deleted file from db: count " + mData.size() + "");
        for (int i = 0; i < mData.size(); i++) {
            Log.d(TAG, "for file " + i + ", tag size is: " + mData.get(i).getTags().size());
            for (int j = 0; j < mData.get(i).getTags().size(); j++) {
                Log.d(TAG, "for file " + i + ", tag " + j + " is " + mData.get(i).getTags().get(j).getText());
            }
        }

//        mData.add(new DeletedFileInfo("File 1", "/sdcard0/recyclebin/image/screenshot/deletedfile/file1.png", "", null, 12, "", "", "", true, new ArrayList<Tag>() {{
//            add(new Tag(mTitles[2], mColors[2]));
//            add(new Tag(mTitles[4], mColors[4]));
//        }}));
//
//        mData.add(new DeletedFileInfo("File 2", "/sdcard0/recyclebin/image/screenshot/deletedfile/file2.png", "", null, 14, "", "", "", true, new ArrayList<Tag>() {{
//            add(new Tag(mTitles[2], mColors[2]));
//            add(new Tag(mTitles[4], mColors[4]));
//        }}));
//
//        mData.add(new DeletedFileInfo("File 3", "/sdcard0/recyclebin/image/screenshot/deletedfile/file3.png", "", null, 142, "", "", "", true, new ArrayList<Tag>() {{
//            add(new Tag(mTitles[1], mColors[1]));
//            add(new Tag(mTitles[5], mColors[5]));
//        }}));
//
//        mData.add(new DeletedFileInfo("File 4", "/sdcard0/recyclebin/image/screenshot/deletedfile/file4.png", "", null, 40, "", "", "", true, new ArrayList<Tag>() {{
//            add(new Tag(mTitles[7], mColors[7]));
//            add(new Tag(mTitles[4], mColors[4]));
//        }}));
//
//        mData.add(new DeletedFileInfo("File 5", "/sdcard0/recyclebin/image/screenshot/deletedfile/file5.png", "", null, 100, "", "", "", true, new ArrayList<Tag>() {{
//            add(new Tag(mTitles[3], mColors[3]));
//            add(new Tag(mTitles[7], mColors[7]));
//        }}));
//
//        mData.add(new DeletedFileInfo("File 6", "/sdcard0/recyclebin/image/screenshot/deletedfile/file6.png", "", null, 12, "", "", "", true, new ArrayList<Tag>() {{
//            add(new Tag(mTitles[1], mColors[1]));
//            add(new Tag(mTitles[6], mColors[6]));
//        }}));

        return new ArrayList<DeletedFileInfo>(mData);
    }

    private ArrayList<DeletedFileInfo> getAllDeletedDataWithAdvertise() {
        ArrayList<DeletedFileInfo> arrAll = new ArrayList<DeletedFileInfo>();
        ArrayList<DeletedFileInfo> tempAll = getAllDeletedData();
        if (tempAll.size() > 0) {
            if (isFreeApp) {
                ArrayList<DeletedFileInfo> arrAd = getAdvertiseList();
                int index = 0;
                for (DeletedFileInfo deletedFile : tempAll) {
                    arrAll.add(deletedFile);
                    Double randomNumber = Math.random();
                    Log.d("randomnumber: ", randomNumber + "");
//            if (Math.random() < 0.2) {
                    if (randomNumber > 0.5) {
                        arrAll.add(arrAd.get(index % arrAd.size()));
                        index++;
                    }
                }
            } else {
                arrAll = tempAll;
            }
        }
        return arrAll;
    }

    private ArrayList<DeletedFileInfo> getAdvertiseList() {
        ArrayList<DeletedFileInfo> advertises = new ArrayList<DeletedFileInfo>();
        advertises.add(new DeletedFileInfo("Add 1", "", "", 0, "", "", "", new Date(), false));
        advertises.add(new DeletedFileInfo("Add 2", "", "", 0, "", "", "", new Date(), false));
        advertises.add(new DeletedFileInfo("Add 3", "", "", 0, "", "", "", new Date(), false));
        advertises.add(new DeletedFileInfo("Add 4", "", "", 0, "", "", "", new Date(), false));
        advertises.add(new DeletedFileInfo("Add 5", "", "", 0, "", "", "", new Date(), false));
        return advertises;
    }

    @Override
    public void onNothingSelected() {
        if (recyclerViewDeletedFile != null) {
            deletedFileAdapter.removeAll();
            deletedFileAdapter.addAll(mAllDeletedFiles);
            deletedFileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFiltersSelected(@NotNull ArrayList<Tag> filters) {
        ArrayList<DeletedFileInfo> newDeletedFiles = findByTags(filters);
        ArrayList<DeletedFileInfo> oldDeletedFiles = new ArrayList<DeletedFileInfo>(deletedFileAdapter.getAllData());
        deletedFileAdapter.removeAll();
        deletedFileAdapter.addAll(newDeletedFiles);
        deletedFileAdapter.notifyDataSetChanged();
        calculateDiff(oldDeletedFiles, newDeletedFiles);
    }

    @Override
    public void onFilterSelected(Tag item) {
        if (item.getText().equals(mTitles[0])) {
            mFilter.deselectAll();
            mFilter.collapse();
        }
    }

    @Override
    public void onFilterDeselected(Tag item) {

    }

    /******************************
     * Toolbar actionmode methods *
     ******************************/
    private void onListItemSelect(int position) {
        //Toggle the selection
        deletedFileAdapter.toggleSelection(position);
        //Check if any items are already selected or not
        boolean hasCheckedItems = deletedFileAdapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null) {
            //visible existing toolbar
            visibleExistingToolbar(false);

            // there are some selected items, start the actionMode
            mActionMode = startSupportActionMode(new ToolbarActionModeCallback(HomeActivity.this, deletedFileAdapter));
        } else if (!hasCheckedItems && mActionMode != null) {
            // there no selected items, finish the actionMode
            mActionMode.finish();

            //visible existing toolbar
            visibleExistingToolbar(true);
        }

        if (mActionMode != null) {
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(deletedFileAdapter.getSelectedCount()) + " selected");
        }
    }

    public void visibleExistingToolbar(boolean isVisible) {
        if (isVisible) {
            findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
//            findViewById(R.id.filter).setVisibility(View.VISIBLE);

//            FrameLayout.LayoutParams buttonLayoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            buttonLayoutParams.setMargins(0, 90, 0, 0);
//            findViewById(R.id.rv_deleted_file).setLayoutParams(buttonLayoutParams);
//            deletedFileAdapter.notifyDataSetChanged();
        } else {
            findViewById(R.id.toolbar).setVisibility(View.GONE);
//            findViewById(R.id.filter).setVisibility(View.GONE);
//
//            FrameLayout.LayoutParams buttonLayoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            buttonLayoutParams.setMargins(0, 10, 0, 0);
//            findViewById(R.id.rv_deleted_file).setLayoutParams(buttonLayoutParams);
//            deletedFileAdapter.notifyDataSetChanged();
        }
    }

    //Set action mode null after use
    public void setNullToActionMode() {
        if (mActionMode != null) {
            mActionMode = null;
            visibleExistingToolbar(true);
        }
    }

    //Delete selected rows
    public void deleteRows() {
        //Get selected ids
        SparseBooleanArray selected = deletedFileAdapter.getSelectedIds();

        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                //If current id is selected remove the item via key
                mAllDeletedFiles.remove(selected.keyAt(i));
                deletedFileAdapter.remove(selected.keyAt(i));
                //notify adapter
                deletedFileAdapter.notifyDataSetChanged();

            }
        }
        //Show Toast
        Toast.makeText(HomeActivity.this, selected.size() + " item deleted.", Toast.LENGTH_SHORT).show();
        //Finish action mode after use
        mActionMode.finish();

        //visible existing toolbar
        visibleExistingToolbar(true);
    }

    /******************
     * FoldableLayout *
     ******************/
    private void setBackButtonAction(final UnfoldableView unfoldableView) {
        ivContentBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (unfoldableView.isUnfolded() || unfoldableView.isUnfolding()) {
                    unfoldableView.foldBack();
                }
            }
        });
    }

    private void initFoldableLayout() {

        listTouchInterceptor = (View) findViewById(R.id.touch_interceptor_view);
        listTouchInterceptor.setClickable(false);

        detailsLayout = (LinearLayout) findViewById(R.id.details_layout);
        detailsLayout.setVisibility(View.INVISIBLE);

        unfoldableView = (UnfoldableView) findViewById(R.id.unfoldable_view);

        Bitmap glance = BitmapFactory.decodeResource(getResources(), R.drawable.unfold_glance);
        unfoldableView.setFoldShading(new GlanceFoldShading(glance));

        unfoldableView.setOnFoldingListener(new UnfoldableView.SimpleFoldingListener() {

            @Override
            public void onUnfolding(UnfoldableView unfoldableView) {
                listTouchInterceptor.setClickable(true);
                detailsLayout.setVisibility(View.VISIBLE);

                // change layout view while unfolding
                mFilter.setVisibility(View.INVISIBLE);
                ivListToGrid.setVisibility(View.INVISIBLE);
                tvTitle.setText(getString(R.string.title_activity_file_detail));
                ivContentHamburger.setVisibility(View.GONE);
                ivContentBack.setVisibility(View.VISIBLE);

                // after folding view it always lost action when it is unfolded again
                setBackButtonAction(unfoldableView);
            }

            @Override
            public void onUnfolded(UnfoldableView unfoldableView) {
                listTouchInterceptor.setClickable(false);
            }

            @Override
            public void onFoldingBack(UnfoldableView unfoldableView) {
                listTouchInterceptor.setClickable(true);
            }

            @Override
            public void onFoldedBack(UnfoldableView unfoldableView) {
                listTouchInterceptor.setClickable(false);
                detailsLayout.setVisibility(View.INVISIBLE);

                // change layout view while unfolding
                mFilter.setVisibility(View.VISIBLE);
                ivListToGrid.setVisibility(View.VISIBLE);
                tvTitle.setText(getString(R.string.title_activity_home));
                ivContentBack.setVisibility(View.GONE);
                ivContentHamburger.setVisibility(View.VISIBLE);
            }
        });
    }

    public void openDetails(View detailLayout, View rowItemView, DeletedFileInfo deletedFile) {

        LinearLayout llImageBackground = (LinearLayout) detailsLayout.findViewById(R.id.ll_image_bg);
        TextView tvDetailFileName = (TextView) detailsLayout.findViewById(R.id.tv_detail_file_name);
        TextView tvDetailFileLocation = (TextView) detailsLayout.findViewById(R.id.tv_detail_file_location);

        if (deletedFile.getTags().size() > 0) {
            Tag firstTag = deletedFile.getTags().get(0);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(firstTag.getColor());
            llImageBackground.setBackgroundDrawable(drawable);
        }

        tvDetailFileName.setText(deletedFile.getOriginFileName());
        tvDetailFileLocation.setText(deletedFile.getOriginFilePath());

        unfoldableView.unfold(rowItemView, detailsLayout);
    }

    /*****************
     * File observer *
     *****************/
    private void initFileObserver() {
        relativeLayoutScanner = (RelativeLayout) findViewById(R.id.rl_scanning);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        tvScanningTitle = (TextView) findViewById(R.id.tv_scanning_title);
        tvScanningValue = (TextView) findViewById(R.id.tv_scanning_value);

        if (!isServiceRunning(HomeActivity.this, FileRecoveryService.class)) {
            intentOserverService = new Intent(getBaseContext(), FileRecoveryService.class);
            intentOserverService.putExtra(AllConstants.KEY_INTENT_EXTRA_ACTION, AllConstants.EXTRA_ACTION_START);
            intentOserverService.putExtra(AllConstants.KEY_INTENT_EXTRA_DIR_PATH, Environment.getExternalStorageDirectory().getAbsolutePath());
            intentOserverService.putExtra(AllConstants.KEY_INTENT_EXTRA_MASK, FileObserver.ALL_EVENTS);
            startService(intentOserverService);
            relativeLayoutScanner.setVisibility(View.VISIBLE);
        } else {
            relativeLayoutScanner.setVisibility(View.GONE);
        }

        ivClose.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                relativeLayoutScanner.setVisibility(View.GONE);
            }
        });
    }

    private void updateUI(Intent intent) {
        if (intent != null && intent.getParcelableExtra(KEY_INTENT_EXTRA_UPDATE) != null) {
            UpdateScanningProgress updateScanningProgress = (UpdateScanningProgress) intent.getParcelableExtra(KEY_INTENT_EXTRA_UPDATE);
//            Toast.makeText(HomeActivity.this, fileName, Toast.LENGTH_SHORT).show();
            tvScanningValue.setText(updateScanningProgress.getFile().getName());

//            if (!updateScanningProgress.isCopying()) {
//                relativeLayoutScanner.setVisibility(View.GONE);
//            }
        }
    }

    public BroadcastReceiver fileRecoveryBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        try {
            registerReceiver(fileRecoveryBroadcastReceiver, new IntentFilter(INTENT_FILTER_ACTIVITY_UPDATE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            unregisterReceiver(fileRecoveryBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
