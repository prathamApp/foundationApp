package com.pratham.foundation.view_holders;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.CertificateModelClass;
import com.pratham.foundation.ui.app_home.test_fragment.CertificateClicked;
import com.pratham.foundation.ui.test.certificate.CertificateActivity;

import java.util.Objects;

import static com.pratham.foundation.ui.test.certificate.CertificateAdapter.quesIndex;

public class CertificateDisplayViewHolder extends RecyclerView.ViewHolder {

    @Nullable
    public TextView title;
    @Nullable
    public RelativeLayout certificate_card;
    @Nullable
    public RatingBar ratingStars;

    private CertificateClicked certificateClicked;

    public CertificateDisplayViewHolder(View itemView, final CertificateClicked certificateClicked) {
        super(itemView);

        certificate_card = itemView.findViewById(R.id.certificate_card_view);
        title = itemView.findViewById(R.id.assess_data);
        ratingStars = itemView.findViewById(R.id.ratingStars);
        this.certificateClicked = certificateClicked;
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void setItem(CertificateModelClass certiItem, int position) {

        String ques = "";

        if (certiItem.getCodeCount() > 1) {
            quesIndex += 1;
            ques = "" + quesIndex + ". ";
        } else {
//            quesIndex = 0;
            ques = "";
        }

        if (!certiItem.isAsessmentGiven()) {
            ratingStars.setVisibility(View.GONE);
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("English"))
                Objects.requireNonNull(title).setText(ques + certiItem.getEnglishQues());
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Hindi"))
                Objects.requireNonNull(title).setText(ques + certiItem.getHindiQues());
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Marathi"))
                Objects.requireNonNull(title).setText(ques + certiItem.getMarathiQues());
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Gujarati")) {
                Typeface face = Typeface.createFromAsset(ApplicationClass.getInstance().getAssets(), "fonts/muktavaani_gujarati.ttf");
                title.setTypeface(face);
                Objects.requireNonNull(title).setText(ques + certiItem.getGujaratiQues());
            }
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Kannada"))
                Objects.requireNonNull(title).setText(ques + certiItem.getKannadaQues());
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Bengali"))
                Objects.requireNonNull(title).setText(ques + certiItem.getBengaliQues());
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Assamese")) {
                Typeface face = Typeface.createFromAsset(ApplicationClass.getInstance().getAssets(), "fonts/lohit_oriya.ttf");
                title.setTypeface(face);
                Objects.requireNonNull(title).setText(ques + certiItem.getAssameseQues());
            }
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Telugu"))
                Objects.requireNonNull(title).setText(ques + certiItem.getTeluguQues());
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Tamil"))
                Objects.requireNonNull(title).setText(ques + certiItem.getTamilQues());
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Odia")) {
                Typeface face = Typeface.createFromAsset(ApplicationClass.getInstance().getAssets(), "fonts/lohit_oriya.ttf");
                title.setTypeface(face);
                Objects.requireNonNull(title).setText(ques + certiItem.getOdiaQues());
            }
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Urdu"))
                Objects.requireNonNull(title).setText(ques + certiItem.getUrduQues());
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Punjabi")) {
                Typeface face = Typeface.createFromAsset(ApplicationClass.getInstance().getAssets(), "fonts/raavi_punjabi.ttf");
                title.setTypeface(face);
                Objects.requireNonNull(title).setText(ques + certiItem.getPunjabiQues());
            }

            certificate_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    certificateClicked.onCertificateOpenGame(position, certiItem.getNodeId());
                }
            });
        } else {
            Objects.requireNonNull(ratingStars).setVisibility(View.VISIBLE);
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("English"))
                Objects.requireNonNull(title).setText(ques + certiItem.getEnglishAnsw());
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Hindi"))
                Objects.requireNonNull(title).setText(ques + certiItem.getHindiAnsw());
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Marathi"))
                Objects.requireNonNull(title).setText(ques + certiItem.getMarathiAnsw());
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Gujarati")) {
                Typeface face = Typeface.createFromAsset(ApplicationClass.getInstance().getAssets(), "fonts/muktavaani_gujarati.ttf");
                Objects.requireNonNull(title).setTypeface(face);
                Objects.requireNonNull(title).setText(ques + certiItem.getGujaratiAnsw());
            }
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Kannada"))
                Objects.requireNonNull(title).setText(ques + certiItem.getKannadaAnsw());
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Bengali"))
                Objects.requireNonNull(title).setText(ques + certiItem.getBengaliAnsw());
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Assamese")) {
                Typeface face = Typeface.createFromAsset(ApplicationClass.getInstance().getAssets(), "fonts/geetl_assamese.ttf");
                Objects.requireNonNull(title).setTypeface(face);
                Objects.requireNonNull(title).setText(ques + certiItem.getAssameseAnsw());
            }
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Telugu"))
                Objects.requireNonNull(title).setText(ques + certiItem.getTeluguAnsw());
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Tamil"))
                Objects.requireNonNull(title).setText(ques + certiItem.getTamilAnsw());
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Odia")) {
                Typeface face = Typeface.createFromAsset(ApplicationClass.getInstance().getAssets(), "fonts/lohit_oriya.ttf");
                Objects.requireNonNull(title).setTypeface(face);
                Objects.requireNonNull(title).setText(ques + certiItem.getOdiaAnsw());
            }
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Urdu"))
                Objects.requireNonNull(title).setText(ques + certiItem.getUrduAnsw());
            if (CertificateActivity.certificateLanguage.equalsIgnoreCase("Punjabi")) {
                Typeface face = Typeface.createFromAsset(ApplicationClass.getInstance().getAssets(), "fonts/raavi_punjabi.ttf");
                Objects.requireNonNull(title).setTypeface(face);
                Objects.requireNonNull(title).setText(ques + certiItem.getPunjabiAnsw());
            }

            ratingStars.setRating(certiItem.getCertificateRating());

/*            certificate_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //certificateClicked.onCertificateOpenGame(position,certiItem.getNodeId());
                }
            });*/

        }
        setAnimations(certificate_card);
    }

    private void setAnimations(final View content_card_view) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(ApplicationClass.getInstance(), R.anim.item_fall_down);
        animation.setDuration(300);
        content_card_view.setVisibility(View.VISIBLE);
        content_card_view.setAnimation(animation);
    }
}