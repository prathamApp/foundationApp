package com.pratham.foundation.view_holders;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.SupervisorData;
import com.pratham.foundation.modalclasses.CertificateModelClass;
import com.pratham.foundation.ui.test.certificate.CertificateClicked;

import java.util.Objects;

public class CertificateSupervisorViewHolder extends RecyclerView.ViewHolder {

    @Nullable
    public ImageView iv_photo;
    @Nullable
    public TextView tv_supervisor_name;
    @Nullable
    public RelativeLayout certificate_card;

    private CertificateClicked certificateClicked;

    public CertificateSupervisorViewHolder(View itemView) {
        super(itemView);

        certificate_card = itemView.findViewById(R.id.certificate_card_view);
        iv_photo = itemView.findViewById(R.id.iv_photo);
        tv_supervisor_name = itemView.findViewById(R.id.tv_supervisor_name);
    }

    @SuppressLint("CheckResult")
    public void setItem(CertificateModelClass certiItem, int position) {
        SupervisorData supervisorData;
        supervisorData = AppDatabase.getDatabaseInstance(ApplicationClass.getInstance()).getSupervisorDataDao()
                .getSupervisorById("" + certiItem.getResourceId());
        Bitmap bmImg = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString()
                + "/.FCAInternal/supervisorImages/" +
                supervisorData.getSupervisorPhoto());
        Objects.requireNonNull(iv_photo).setImageBitmap(bmImg);
        Objects.requireNonNull(tv_supervisor_name).setText(supervisorData.getSupervisorName());
        setAnimations(Objects.requireNonNull(certificate_card));
    }

    private void setAnimations(final View content_card_view) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(ApplicationClass.getInstance(), R.anim.item_fall_down);
        animation.setDuration(300);
        content_card_view.setVisibility(View.VISIBLE);
        content_card_view.setAnimation(animation);
    }
}