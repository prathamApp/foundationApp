package com.pratham.foundation.view_holders;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.app_home.FragmentItemClicked;
import com.pratham.foundation.ui.select_language_fragment.SelectLangContract;
import com.pratham.foundation.utility.FC_Constants;

import java.util.Objects;

import static com.pratham.foundation.ApplicationClass.getInstance;

public class SelectLangViewHolder extends RecyclerView.ViewHolder {

    @Nullable
    TextView title;
    @Nullable
    RelativeLayout rl_lang_select;

    private SelectLangContract.LangItemClicked contentClicked;
    private FragmentItemClicked itemClicked;

    public SelectLangViewHolder(View itemView, final SelectLangContract.LangItemClicked contentClicked) {
        super(itemView);

        title = itemView.findViewById(R.id.lang_item_tv);
        rl_lang_select = itemView.findViewById(R.id.rl_lang_select);
        this.contentClicked = contentClicked;
    }

    @SuppressLint("CheckResult")
    public void setItem(ContentTable contentList, int position) {

        String currentLang = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, "");

        if(currentLang.equalsIgnoreCase(contentList.getNodeTitle())) {
            Objects.requireNonNull(rl_lang_select).setBackgroundResource(R.drawable.lang_selected_bg);
            title.setTextColor(getInstance().getResources().getColor(R.color.white));
        }
        else {
            Objects.requireNonNull(rl_lang_select).setBackgroundResource(R.drawable.card_color_bg6);
            title.setTextColor(getInstance().getResources().getColor(R.color.dark_blue));
        }
        Objects.requireNonNull(title).setText(contentList.getNodeTitle());

        rl_lang_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentClicked.itemClicked(contentList, position);
            }
        });

        setAnimations(rl_lang_select);
    }

    private void setAnimations(final View content_card_view) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(ApplicationClass.getInstance(), R.anim.item_fall_down);
        animation.setDuration(300);
        content_card_view.setVisibility(View.VISIBLE);
        content_card_view.setAnimation(animation);
    }

    private void setSlideAnimations(final View content_card_view) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(ApplicationClass.getInstance(), R.anim.slide_list);
        animation.setDuration(300);
        content_card_view.setVisibility(View.VISIBLE);
        content_card_view.setAnimation(animation);
    }

}