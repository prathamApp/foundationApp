package com.pratham.foundation.view_holders;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.SyncLogDataModal;
import com.pratham.foundation.ui.contentPlayer.vocabulary_qa.ReadingVocabularyContract;

public class VocabCategoryViewHolder extends RecyclerView.ViewHolder {

    /**
     * Created common holder which is used for cards.
     * Folder and file type of cards have different views
     */

    TextView category_item;
    private final ReadingVocabularyContract.ReadingCateogryClick cateogryClick;

    public VocabCategoryViewHolder(View view, final ReadingVocabularyContract.ReadingCateogryClick cateogryClick) {
        super(view);
        category_item = view.findViewById(R.id.category_item);
        this.cateogryClick = cateogryClick;
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void setItem(String name, int position) {
        try {
            SyncLogDataModal logDataModal;
            category_item.setText("" + name);
            setAnimations(category_item, position);
            category_item.setOnClickListener(view -> cateogryClick.setCategory(name));
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