package com.pratham.foundation.ui.download_bottom_sheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.Modal_FileDownloading;
import com.pratham.foundation.ui.splash_activity.SplashActivity;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import static com.pratham.foundation.ApplicationClass.fileDownloadingList;


@EFragment(R.layout.download_list_fragment)
public class DownloadBottomSheetFragment extends BottomSheetDialogFragment
        implements DownloadBottomSheetContract.DownloadBottomSheetView {

    @Bean(DownloadBottomSheetPresenter.class)
    DownloadBottomSheetContract.DownloadBottomSheetPresenter presenter;

    @ViewById(R.id.rv_contentList)
    RecyclerView rv_contentList;

    DownloadListAdapter adapter;
    Gson gson;
    Context context;

    @AfterViews
    public void initialize() {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        presenter.setView(DownloadBottomSheetFragment.this);
        gson = new Gson();
        hideSystemUI();
        context = getActivity();
        notifyStudentAdapter();
    }

    private void hideSystemUI() {
        Objects.requireNonNull(getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageReceived(EventMessage message) {
        if (message != null) {
            Modal_FileDownloading modal_fileDownloading = message.getModal_fileDownloading();
             if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_UPDATE)) {
//                SET progress bar value
                 int ind =0;
                 for(int s=0; s<fileDownloadingList.size();s++)
                     if(fileDownloadingList.get(s).getDownloadId().equalsIgnoreCase(modal_fileDownloading.getDownloadId())) {
                         fileDownloadingList.get(s).setProgress(modal_fileDownloading.getProgress());
                         ind = s;
                         break;
                     }
                 adapter.notifyItemChanged(ind);
            }
        }
    }
    @UiThread
    @Override
    public void notifyStudentAdapter() {
        if (adapter == null) {
            adapter = new DownloadListAdapter(getActivity(), this, fileDownloadingList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.HORIZONTAL, false);
            rv_contentList.setLayoutManager(mLayoutManager);
            rv_contentList.setAdapter(adapter);
        } else
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        SplashActivity.fragmentBottomPauseFlg = true;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        try {
            EventMessage eventMessage = new EventMessage();
            eventMessage.setMessage(FC_Constants.BOTTOM_FRAGMENT_CLOSED);
            EventBus.getDefault().post(eventMessage);
//            Objects.requireNonNull(getActivity()).onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d("BottomSheetCancel", "onCancel: aaaaaaaaaaaaaaaaaaaa");
    }

    @Override
    public void onResume() {
        super.onResume();
        notifyStudentAdapter();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
