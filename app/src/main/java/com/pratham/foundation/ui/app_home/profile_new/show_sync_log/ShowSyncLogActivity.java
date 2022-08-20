package com.pratham.foundation.ui.app_home.profile_new.show_sync_log;

import static com.pratham.foundation.utility.FC_Utility.dpToPx;

import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.database.domain.Modal_Log;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_show_sync_logs)
public class ShowSyncLogActivity extends BaseActivity implements
        ShowSyncLogContract.ShowSyncLogView {

    @Bean(ShowSyncLogPresenter.class)
    ShowSyncLogContract.ShowSyncLogPresenter presenter;

    @ViewById(R.id.tv_Topic)
    TextView tv_Topic;
    @ViewById(R.id.sync_recycler_view)
    RecyclerView recycler_view;
    String sub_Name;
    ShowSyncLogAdapter syncLogAdapter;
    List<Modal_Log> modal_logList;

    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        Configuration config = getResources().getConfiguration();
        FC_Constants.TAB_LAYOUT = config.smallestScreenWidthDp > 425;
        presenter.setView(ShowSyncLogActivity.this);
        presenter.getLogsData();
    }

    @UiThread
    @Override
    public void addToAdapter(List<Modal_Log> logList) {
        modal_logList = logList;
        String a = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI);
        Log.d("INSTRUCTIONFRAG", "Select Subj: " + a);
        FC_Utility.setAppLocal(this, a);
        if (syncLogAdapter == null) {
            syncLogAdapter = new ShowSyncLogAdapter(this, modal_logList);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            recycler_view.setLayoutManager(mLayoutManager);
            recycler_view.setNestedScrollingEnabled(false);
            recycler_view.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(this), true));
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setAdapter(syncLogAdapter);
        } else
            syncLogAdapter.notifyDataSetChanged();
    }

/*    public void takeScreenshot() {
        Bitmap bitmap = getScreenBitmap(); // Get the bitmap
        saveTheBitmap(bitmap);               // Save it to the external storage device.
    }

    public Bitmap getScreenBitmap() {
        View v = findViewById(android.R.id.content).getRootView();
        v.setDrawingCacheEnabled(true);
        v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false); // clear drawing cache
        return b;
    }

    private void saveTheBitmap(Bitmap image) {

        if (!new File(ApplicationClass.getStoragePath() + "/PrathamBackups").exists())
            new File(ApplicationClass.getStoragePath() + "/PrathamBackups").mkdirs();
        File pictureFile = new File(ApplicationClass.getStoragePath() + "/PrathamBackups/Test");
        if (!pictureFile.exists()) {
            pictureFile.mkdirs();
            if (pictureFile == null) {
                Log.d("TAG",
                        "Error creating media file, check storage permissions: ");// e.getMessage());
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                image.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    }*/

    @ViewById(R.id.rl_no_data)
    RelativeLayout rl_no_data;

    @UiThread
    @Override
    public void showNoData() {
        recycler_view.setVisibility(View.GONE);
        rl_no_data.setVisibility(View.VISIBLE);
    }

    @UiThread
    public void setStudentProfileImage(String sImage) {
    }

    @Click(R.id.main_back)
    public void pressedBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}