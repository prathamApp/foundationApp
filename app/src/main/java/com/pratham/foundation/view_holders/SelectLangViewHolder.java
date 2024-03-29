package com.pratham.foundation.view_holders;

import static com.pratham.foundation.ApplicationClass.getInstance;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.app_home.FragmentItemClicked;
import com.pratham.foundation.ui.select_language_fragment.SelectLangContract;
import com.pratham.foundation.utility.FC_Constants;

import java.util.Objects;

public class SelectLangViewHolder extends RecyclerView.ViewHolder {

    @Nullable
    TextView title;
    @Nullable
    RelativeLayout rl_lang_select;

    private final SelectLangContract.LangItemClicked contentClicked;
    private FragmentItemClicked itemClicked;
    private final SimpleDraweeView content_image;

    public SelectLangViewHolder(View itemView, final SelectLangContract.LangItemClicked contentClicked) {
        super(itemView);

        title = itemView.findViewById(R.id.lang_item_tv);
        rl_lang_select = itemView.findViewById(R.id.rl_lang_select);
        content_image = itemView.findViewById(R.id.content_image);
        this.contentClicked = contentClicked;
    }

    @SuppressLint("CheckResult")
    public void setItem(ContentTable contentList, int position) {

        String currentLang = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, "");
        if (currentLang.equalsIgnoreCase(contentList.getNodeTitle())) {
            Objects.requireNonNull(rl_lang_select).setBackgroundResource(R.drawable.card_color_bg1);
            Objects.requireNonNull(title).setTextColor(getInstance().getResources().getColor(R.color.white));
        } else {
            Objects.requireNonNull(rl_lang_select).setBackgroundResource(R.drawable.card_color_bg6);
            Objects.requireNonNull(title).setTextColor(getInstance().getResources().getColor(R.color.dark_blue));
        }
        Objects.requireNonNull(title).setText(contentList.getNodeTitle());
        title.setSelected(true);
/*        File f;
        if (contentList.getIsDownloaded().equalsIgnoreCase("1") ||
                contentList.getIsDownloaded().equalsIgnoreCase("true")) {
            if (contentList.isOnSDCard())
                f = new File(ApplicationClass.contentSDPath +
                        "" + App_Thumbs_Path + contentList.getNodeImage());
            else
                f = new File(ApplicationClass.foundationPath +
                        "" + App_Thumbs_Path + contentList.getNodeImage());
            if (f.exists())
                Objects.requireNonNull(content_image).setImageURI(Uri.fromFile(f));
        } else {*/
            String thumbPath = "" + contentList.getNodeServerImage();
            if (ApplicationClass.wiseF.isDeviceConnectedToMobileNetwork() || ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
                if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                    String fileName = contentList.getNodeServerImage()
                            .substring(contentList.getNodeServerImage().lastIndexOf('/') + 1);
                    thumbPath = FC_Constants.RASP_IP + FC_Constants.RASP_LOCAL_IMAGES + fileName;
                } else {
                    thumbPath = "" + contentList.getNodeServerImage();
                }
            }
            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(thumbPath))
                    .setResizeOptions(new ResizeOptions(300, 300))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setOldController(Objects.requireNonNull(content_image).getController())
                    .build();
            content_image.setController(controller);
//        }


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