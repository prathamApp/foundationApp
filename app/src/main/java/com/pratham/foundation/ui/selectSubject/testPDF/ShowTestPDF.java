package com.pratham.foundation.ui.selectSubject.testPDF;


import static com.pratham.foundation.utility.FC_Utility.dpToPx;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.modalclasses.Diagnostic_pdf_Modal;
import com.pratham.foundation.ui.contentPlayer.webviewpdf.PDFViewActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_show_test_pdf)
public class ShowTestPDF extends BaseActivity implements
        ShowTestPDFContract.TestPDFView, ShowTestPDFContract.ItemClicked {

    @Bean(ShowTestPDFPresenter.class)
    ShowTestPDFContract.TestPDFPresenter presenter;

    @ViewById(R.id.recycler_view)
    RecyclerView recycler_view;
    @ViewById(R.id.rl_no_data)
    RelativeLayout rl_no_data;

    List<Diagnostic_pdf_Modal> pdf_modalList;
    ShowTestPDFAdapter syncLogAdapter;

    @AfterViews
    public void initialize() {
        presenter.setView(ShowTestPDF.this);
        presenter.getPDFs();
    }

    @UiThread
    @Override
    public void setList(List<Diagnostic_pdf_Modal> pdf_modalList) {
        this.pdf_modalList = pdf_modalList;
    }

    @UiThread
    @Override
    public void openPDF(Diagnostic_pdf_Modal contentItem) {
        Toast.makeText(this, "OPEN PDF", Toast.LENGTH_SHORT).show();
        Intent intent2 = new Intent(this, PDFViewActivity_.class);
        intent2.putExtra("contentPath", contentItem.getFile_path());
        intent2.putExtra("dia", "dia");
        startActivity(intent2);

    }

    @UiThread
    @Override
    public void showNoData(){
        recycler_view.setVisibility(View.GONE);
        rl_no_data.setVisibility(View.VISIBLE);
    }

    @Override
    @UiThread
    public void setAdapter(){
        if(syncLogAdapter==null) {
            syncLogAdapter = new ShowTestPDFAdapter(this, pdf_modalList, this);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            recycler_view.setLayoutManager(mLayoutManager);
            recycler_view.setNestedScrollingEnabled(false);
            recycler_view.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(this), true));
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setAdapter(syncLogAdapter);
        }else{
            syncLogAdapter.notifyDataSetChanged();
        }

    }

    @UiThread
    @Click(R.id.main_back)
    public void updateClicked() {
        finish();
    }


}
