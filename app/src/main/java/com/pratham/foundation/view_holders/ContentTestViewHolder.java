package com.pratham.foundation.view_holders;

import static com.pratham.foundation.ApplicationClass.App_Thumbs_Path;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import com.google.android.material.card.MaterialCardView;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.ui.app_home.FragmentItemClicked;
import com.pratham.foundation.ui.app_home.display_content.ContentClicked;

import java.io.File;
import java.util.Objects;


public class ContentTestViewHolder extends RecyclerView.ViewHolder {

    /**
     * Created common holder which is used for cards.
     * Folder and file type of cards have different views
     */

    @Nullable
    TextView tvTitle;
    @Nullable
    TextView tv_progress;
    @Nullable
    SimpleDraweeView itemImage;
    @Nullable
    RelativeLayout rl_root;
    @Nullable
    ImageView iv_downld;
    @Nullable
    ImageView ib_update_btn;
    @Nullable
    MaterialCardView card_main;

    private ContentClicked contentClicked;
    private FragmentItemClicked itemClicked;
    String animType;

    public ContentTestViewHolder(View itemView, final ContentClicked contentClicked) {
        super(itemView);
//        contentClicked interface initilized for click events
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tv_progress = itemView.findViewById(R.id.tv_progress);
        itemImage = itemView.findViewById(R.id.item_Image);
        rl_root = itemView.findViewById(R.id.rl_root);
        card_main = itemView.findViewById(R.id.card_main);
        iv_downld = itemView.findViewById(R.id.iv_downld);
        ib_update_btn = itemView.findViewById(R.id.ib_update_btn);
        this.contentClicked = contentClicked;
    }

    public ContentTestViewHolder(View itemView, final FragmentItemClicked itemClicked) {
        super(itemView);
//        itemClicked interface initilized for click events
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tv_progress = itemView.findViewById(R.id.tv_progress);
        itemImage = itemView.findViewById(R.id.item_Image);
        rl_root = itemView.findViewById(R.id.rl_root);
        card_main = itemView.findViewById(R.id.card_main);
        iv_downld = itemView.findViewById(R.id.iv_downld);
        ib_update_btn = itemView.findViewById(R.id.ib_update_btn);
        this.itemClicked = itemClicked;
    }

    @SuppressLint("CheckResult")
    public void setActivityTestItem(ContentTable contentList, int position) {
        try {
//        add card and its click listners
            Objects.requireNonNull(card_main).setBackground(ApplicationClass.getInstance()
                    .getResources().getDrawable(R.drawable.card_test_bg));
            Objects.requireNonNull(tvTitle).setText(contentList.getNodeTitle());

            File file;
            if (contentList.getIsDownloaded().equalsIgnoreCase("1") ||
                    contentList.getIsDownloaded().equalsIgnoreCase("true")) {
                Objects.requireNonNull(iv_downld).setVisibility(View.GONE);
                if (contentList.isOnSDCard())
                    file = new File(ApplicationClass.contentSDPath +
                            "" + App_Thumbs_Path + contentList.getNodeImage());
                else
                    file = new File(ApplicationClass.foundationPath +
                            "" + App_Thumbs_Path + contentList.getNodeImage());
                if (file.exists())
                    Objects.requireNonNull(itemImage).setImageURI(Uri.fromFile(file));
                else
                    Objects.requireNonNull(itemImage).setActualImageResource(R.drawable.assessment_thumb);
            } else {
                ImageRequest imageRequest = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(contentList.getNodeServerImage()))
                        .setResizeOptions(new ResizeOptions(300, 300))
                        .setLocalThumbnailPreviewsEnabled(true)
                        .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imageRequest)
                        .setOldController(Objects.requireNonNull(itemImage).getController())
                        .build();
                itemImage.setController(controller);
            }
            Objects.requireNonNull(iv_downld).setVisibility(View.GONE);
            Objects.requireNonNull(ib_update_btn).setVisibility(View.GONE);
            Objects.requireNonNull(tv_progress).setVisibility(View.GONE);

            Objects.requireNonNull(card_main).setOnClickListener(v -> {
                if (contentList.getNodeType() != null) {
                    contentClicked.onTestContentClicked(position, contentList);
                }
            });
            setAnimations(card_main);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void setFragmentTestItem(ContentTable contentTable, int posi, String parentName, int parentPos) {
        try {
            Objects.requireNonNull(card_main).setBackground(ApplicationClass.getInstance().getResources().getDrawable(R.drawable.card_test_bg));
            Objects.requireNonNull(tvTitle).setText(contentTable.getNodeTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File file;
            if (contentTable.getIsDownloaded().equalsIgnoreCase("1") ||
                    contentTable.getIsDownloaded().equalsIgnoreCase("true")) {
                Objects.requireNonNull(iv_downld).setVisibility(View.GONE);
                if (contentTable.isOnSDCard())
                    file = new File(ApplicationClass.contentSDPath +
                            "" + App_Thumbs_Path + contentTable.getNodeImage());
                else
                    file = new File(ApplicationClass.foundationPath +
                            "" + App_Thumbs_Path + contentTable.getNodeImage());
                if (file.exists())
                    Objects.requireNonNull(itemImage).setImageURI(Uri.fromFile(file));
                else
                    Objects.requireNonNull(itemImage).setActualImageResource(R.drawable.assessment_thumb);

            } else {
                ImageRequest imageRequest = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(contentTable.getNodeServerImage()))
                        .setResizeOptions(new ResizeOptions(300, 300))
                        .setLocalThumbnailPreviewsEnabled(true)
                        .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imageRequest)
                        .setOldController(Objects.requireNonNull(itemImage).getController())
                        .build();
                itemImage.setController(controller);
            }
            Objects.requireNonNull(iv_downld).setVisibility(View.GONE);
            Objects.requireNonNull(ib_update_btn).setVisibility(View.GONE);
            Objects.requireNonNull(tv_progress).setVisibility(View.GONE);

            Objects.requireNonNull(card_main).setOnClickListener(v -> {
                if (contentTable.getNodeType() != null) {
                    itemClicked.onTestContentClicked(posi, contentTable);
                }
            });

            setSlideAnimations(card_main);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTestItem(ContentTable contentTable, int position) {
        Objects.requireNonNull(card_main).setBackground(ApplicationClass.getInstance().getResources().getDrawable(R.drawable.card_test_bg));
        Objects.requireNonNull(tvTitle).setText(contentTable.getNodeTitle());

        File file;
        if (contentTable.getIsDownloaded().equalsIgnoreCase("1") ||
                contentTable.getIsDownloaded().equalsIgnoreCase("true")) {
            Objects.requireNonNull(iv_downld).setVisibility(View.GONE);
            if (contentTable.isOnSDCard())
                file = new File(ApplicationClass.contentSDPath +
                        "" + App_Thumbs_Path + contentTable.getNodeImage());
            else
                file = new File(ApplicationClass.foundationPath +
                        "" + App_Thumbs_Path + contentTable.getNodeImage());
            if (file.exists())
                Objects.requireNonNull(itemImage).setImageURI(Uri.fromFile(file));
            else
                Objects.requireNonNull(itemImage).setActualImageResource(R.drawable.assessment_thumb);

        } else {
            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(contentTable.getNodeServerImage()))
                    .setResizeOptions(new ResizeOptions(300, 300))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setOldController(Objects.requireNonNull(itemImage).getController())
                    .build();
            itemImage.setController(controller);
        }
        Objects.requireNonNull(iv_downld).setVisibility(View.GONE);
        Objects.requireNonNull(ib_update_btn).setVisibility(View.GONE);
        Objects.requireNonNull(tv_progress).setVisibility(View.GONE);

        Objects.requireNonNull(card_main).setOnClickListener(v -> {
            if (contentTable.getNodeType() != null) {
                contentClicked.onTestContentClicked(position, contentTable);
            }
        });
        setAnimations(card_main);
    }
}