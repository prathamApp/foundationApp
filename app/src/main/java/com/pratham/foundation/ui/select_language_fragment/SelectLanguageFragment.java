package com.pratham.foundation.ui.select_language_fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.BlurPopupDialog.BlurPopupWindow;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.app_home.display_content.ContentAdapter;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.APP_LANGUAGE_SELECTED;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;


@EFragment(R.layout.fragment_select_language)
public class SelectLanguageFragment extends Fragment implements SelectLangContract.SelectLangView,
        SelectLangContract.LangItemClicked {

    @Bean(SelectLangPresenter.class)
    SelectLangContract.SelectLangPresenter presenter;

    @ViewById(R.id.btn_back)
    ImageView btn_back;
    @ViewById(R.id.rv_lang)
    RecyclerView recyclerView;

    List<ContentTable> contentTableList;
    private ContentAdapter contentAdapter;
    BlurPopupWindow errorDialog;
    public CustomLodingDialog myLoadingDialog;
    LanguageAdapter languageAdapter;
    Context context;

    @AfterViews
    public void initialize() {
        //get runtime objects and clear the unused
        Runtime rs = Runtime.getRuntime();
        rs.freeMemory();
        rs.gc();
        rs.freeMemory();
        context = getActivity();
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        contentTableList = new ArrayList<>();
        presenter.setView(SelectLanguageFragment.this);
        showLoader();
        presenter.getLanguage();
    }

    @Override
    public void updateLangList(List<ContentTable> langList) {
        contentTableList.clear();
        contentTableList.addAll(langList);
/*        contentTableList.addAll(langList);
        contentTableList.addAll(langList);
        contentTableList.addAll(langList);
        contentTableList.addAll(langList);
        contentTableList.addAll(langList);
        contentTableList.addAll(langList);
        contentTableList.addAll(langList);
        contentTableList.addAll(langList);
        contentTableList.addAll(langList);*/
    }

    @Override
    public void itemClicked(ContentTable contentItem, int pos) {
        //Get the selected langauge and set to app
        FastSave.getInstance().saveBoolean(APP_LANGUAGE_SELECTED, true);
        String language = contentItem.getNodeTitle();
        String languageNodeId = contentItem.getNodeId();
        FastSave.getInstance().saveString(FC_Constants.APP_LANGUAGE, "" + language);
        FastSave.getInstance().saveString(FC_Constants.APP_LANGUAGE_NODE_ID, "" + languageNodeId);
        FC_Utility.setAppLocal(context,language);
        btn_back.performClick();
    }

    @UiThread
    @Override
    public void notifyAdapter() {
        if (languageAdapter == null) {
            //Populate language list to recyclerview
            try {
                languageAdapter = new LanguageAdapter(getActivity(), contentTableList, SelectLanguageFragment.this);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 2);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(context), true));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(languageAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            languageAdapter.notifyDataSetChanged();
        dismissLoadingDialog();
    }

    @UiThread
    @SuppressLint("SetTextI18n")
    @Override
    public void connectToInternetDialog() {
        try {
            errorDialog = new BlurPopupWindow.Builder(getActivity())
                    .setContentView(R.layout.fc_custom_dialog)
                    .setGravity(Gravity.CENTER)
                    .setDismissOnTouchBackground(false)
                    .setDismissOnClickBack(true)
                    .setScaleRatio(0.2f)
                    .bindClickListener(v -> {
                        new Handler().postDelayed(() -> {
                            btn_back.performClick();
                            errorDialog.dismiss();
                        }, 200);
                    }, R.id.dia_btn_green)
                    .setBlurRadius(10)
                    .setTintColor(0x30000000)
                    .build();

            TextView title = errorDialog.findViewById(R.id.dia_title);
            Button btn_gree = errorDialog.findViewById(R.id.dia_btn_green);
            Button btn_yellow = errorDialog.findViewById(R.id.dia_btn_yellow);
            Button btn_red = errorDialog.findViewById(R.id.dia_btn_red);
            btn_gree.setText("Ok");
            title.setText("Connect to Internet");
            btn_red.setVisibility(View.GONE);
            btn_yellow.setVisibility(View.GONE);
            errorDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @UiThread
    @Override
    public void showLoader() {
        try {
            if (myLoadingDialog == null) {
                myLoadingDialog = new CustomLodingDialog(getActivity());
                myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                myLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myLoadingDialog.setContentView(R.layout.loading_dialog);
                myLoadingDialog.setCanceledOnTouchOutside(false);
                myLoadingDialog.show();
            } else if (!myLoadingDialog.isShowing())
                myLoadingDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void dismissLoadingDialog() {
        try {
            new Handler().postDelayed(() -> {
                if (myLoadingDialog != null && myLoadingDialog.isShowing())
                    myLoadingDialog.dismiss();
            }, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @SuppressLint("SetTextI18n")
    @Override
    public void serverIssueDialog() {
        errorDialog = new BlurPopupWindow.Builder(getActivity())
                .setContentView(R.layout.lottie_server_dialog)
                .bindClickListener(v -> {
                    btn_back.performClick();
                    errorDialog.dismiss();
                }, R.id.dia_btn_ok)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(true)
                .setDismissOnClickBack(true)
                .setScaleRatio(0.2f)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();
        errorDialog.show();
    }

    @Click(R.id.btn_back)
    public void selectedLangClicked(){
        getActivity().onBackPressed();
    }
}