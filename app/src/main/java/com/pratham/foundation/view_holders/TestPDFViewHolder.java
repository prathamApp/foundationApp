package com.pratham.foundation.view_holders;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.Diagnostic_pdf_Modal;
import com.pratham.foundation.modalclasses.SyncLogDataModal;
import com.pratham.foundation.ui.selectSubject.testPDF.ShowTestPDFContract;

public class TestPDFViewHolder extends RecyclerView.ViewHolder {

    /**
     * Created common holder which is used for cards.
     * Folder and file type of cards have different views
     */

    TextView tv_studentName, tv_id, tv_subject;
    MaterialCardView pdf_card_main;
    private final ShowTestPDFContract.ItemClicked itemClicked;

    public TestPDFViewHolder(View view, final ShowTestPDFContract.ItemClicked itemClicked) {
        super(view);
        tv_studentName = view.findViewById(R.id.tv_studentName);
        tv_id = view.findViewById(R.id.tv_id);
        tv_subject = view.findViewById(R.id.tv_subject);
        pdf_card_main = view.findViewById(R.id.pdf_card_main);
        this.itemClicked = itemClicked;
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void setTestPDFItem(Diagnostic_pdf_Modal contentItem, int position) {
        try {
            SyncLogDataModal logDataModal;
            tv_studentName.setText("Name: " + contentItem.getStudent_name());
            tv_id.setText("Id: " + contentItem.getEnrollment_id());
            tv_subject.setText("Subject: " + contentItem.getSubject_name());
            setAnimations(pdf_card_main, position);
            pdf_card_main.setOnClickListener(view -> itemClicked.openPDF(contentItem));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAnimations(final View content_card_view, final int position) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(ApplicationClass.getInstance(), R.anim.item_fall_down);
        animation.setDuration(500);
        content_card_view.setVisibility(View.VISIBLE);
        content_card_view.setAnimation(animation);
    }

}