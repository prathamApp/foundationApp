package com.pratham.foundation.view_holders;

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
import com.pratham.foundation.utility.FC_Constants;

import java.io.File;
import java.util.Objects;

import static com.pratham.foundation.ApplicationClass.App_Thumbs_Path;
import static com.pratham.foundation.utility.FC_Constants.SINGLE_RES_DOWNLOAD;
import static com.pratham.foundation.utility.FC_Utility.getRandomCardColor;

public class ContentFileViewHolder extends RecyclerView.ViewHolder {

    /**
     * Created common holder which is used for cards.
     * Folder and file type of cards have different views
     */

    @Nullable
    TextView title;
    @Nullable
    ImageView ib_action_btn;
    @Nullable
    ImageView ib_update_btn;
    @Nullable
    RelativeLayout rl_loader;
    @Nullable
    RelativeLayout rl_card;
    @Nullable
    MaterialCardView content_card_view;
    @Nullable
    SimpleDraweeView thumbnail;

    private ContentClicked contentClicked;
    private FragmentItemClicked itemClicked;

    public ContentFileViewHolder(View itemView, final ContentClicked contentClicked) {
        super(itemView);
//        contentClicked interface initilized for click events
        title = itemView.findViewById(R.id.content_title);
        thumbnail = itemView.findViewById(R.id.content_image);
        content_card_view = itemView.findViewById(R.id.content_card_view);
        rl_loader = itemView.findViewById(R.id.rl_loader);
        rl_card = itemView.findViewById(R.id.rl_card);
        ib_action_btn = itemView.findViewById(R.id.ib_action_btn);
        ib_update_btn = itemView.findViewById(R.id.ib_update_btn);

        this.contentClicked = contentClicked;
    }

    public ContentFileViewHolder(View itemView, FragmentItemClicked itemClicked) {
        super(itemView);
//        itemClicked interface initilized for click events
        title = itemView.findViewById(R.id.content_title);
        thumbnail = itemView.findViewById(R.id.content_image);
        content_card_view = itemView.findViewById(R.id.content_card_view);
        rl_loader = itemView.findViewById(R.id.rl_loader);
        rl_card = itemView.findViewById(R.id.rl_card);
        ib_action_btn = itemView.findViewById(R.id.ib_action_btn);
        ib_update_btn = itemView.findViewById(R.id.ib_update_btn);

        this.itemClicked = itemClicked;
    }

    @SuppressLint("CheckResult")
    public void setFileItem(ContentTable contentList, int position) {

//        add card and its click listners
        Objects.requireNonNull(content_card_view).setBackground(ApplicationClass.getInstance()
                .getResources().getDrawable(getRandomCardColor()));
        Objects.requireNonNull(title).setText(contentList.getNodeTitle());
        title.setSelected(true);
        Objects.requireNonNull(rl_card).setVisibility(View.VISIBLE);
        Objects.requireNonNull(rl_loader).setVisibility(View.GONE);

        Objects.requireNonNull(ib_action_btn).setVisibility(View.GONE);
        if (contentList.getIsDownloaded().equalsIgnoreCase("false")) {
            if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.YOUTUBE_LINK)) {
                ib_action_btn.setImageResource(R.drawable.ic_youtube);
                ib_action_btn.setVisibility(View.VISIBLE);
                ib_action_btn.setClickable(true);
            } else {
                ib_action_btn.setVisibility(View.VISIBLE);
                ib_action_btn.setImageResource(R.drawable.ic_download_2);//setVisibility(View.VISIBLE);
                ib_action_btn.setClickable(false);
            }
        } else if (contentList.getIsDownloaded().equalsIgnoreCase("true")) {
            ib_action_btn.setVisibility(View.VISIBLE);
            ib_action_btn.setImageResource(R.drawable.ic_joystick);
            ib_action_btn.setClickable(true);
            if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.PDF))
                ib_action_btn.setImageResource(R.drawable.ic_pdf);
            else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.YOUTUBE_LINK))
                ib_action_btn.setImageResource(R.drawable.ic_youtube);
            else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.VIDEO))
                ib_action_btn.setImageResource(R.drawable.ic_video);
            else if (contentList.getResourceType().toLowerCase().contains(FC_Constants.GAME))
                ib_action_btn.setImageResource(R.drawable.ic_joystick);
            else
                ib_action_btn.setImageResource(R.drawable.ic_android_act);
        }

        File f;
        if (contentList.getIsDownloaded().equalsIgnoreCase("1") ||
                contentList.getIsDownloaded().equalsIgnoreCase("true")) {
            if (contentList.isOnSDCard())
                f = new File(ApplicationClass.contentSDPath +
                        "" + App_Thumbs_Path + contentList.getNodeImage());
            else
                f = new File(ApplicationClass.foundationPath +
                        "" + App_Thumbs_Path + contentList.getNodeImage());
            if (f.exists()) {
                ImageRequest imageRequest = ImageRequestBuilder
                        .newBuilderWithSource(Uri.fromFile(f))
                        .setResizeOptions(new ResizeOptions(250, 170))
                        .setLocalThumbnailPreviewsEnabled(true)
                        .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imageRequest)
                        .setAutoPlayAnimations(true)// if gif, it will play.
                        .setOldController(Objects.requireNonNull(thumbnail).getController())
                        .build();
                thumbnail.setController(controller);
            }
        } else {
            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(contentList.getNodeServerImage()))
                    .setResizeOptions(new ResizeOptions(250, 170))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setOldController(Objects.requireNonNull(thumbnail).getController())
                    .build();
            thumbnail.setController(controller);

        }

        content_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contentList.getNodeType() != null) {
                    if (contentList.getIsDownloaded().equalsIgnoreCase("true")) {
                        contentClicked.onContentOpenClicked(position, contentList.getNodeId());
                    } else if (contentList.getIsDownloaded().equalsIgnoreCase("false")) {
                        if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.YOUTUBE_LINK))
                            contentClicked.onContentOpenClicked(position, contentList.getNodeId());
                        else
                            contentClicked.onContentDownloadClicked(position, contentList.nodeId);
                    }
                }
            }
        });

        if (contentList.isNodeUpdate())
            Objects.requireNonNull(ib_update_btn).setVisibility(View.VISIBLE);
        else
            Objects.requireNonNull(ib_update_btn).setVisibility(View.GONE);

        ib_update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentClicked.onContentDownloadClicked(position, contentList.nodeId);
            }
        });
        setAnimations(content_card_view);
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

    public void setFragmentFileItem(ContentTable contentTable, int i, String parentName, int parentPos) {

        Objects.requireNonNull(title).setText(contentTable.getNodeTitle());
        title.setSelected(true);
        Objects.requireNonNull(rl_card).setVisibility(View.VISIBLE);
        Objects.requireNonNull(rl_loader).setVisibility(View.GONE);
        Objects.requireNonNull(content_card_view).setBackground(ApplicationClass.getInstance().getResources().getDrawable(getRandomCardColor()));
        File file;
        if (contentTable.getIsDownloaded().equalsIgnoreCase("1") ||
                contentTable.getIsDownloaded().equalsIgnoreCase("true")) {
//                    ib_action_btn.setVisibility(View.GONE);
            if (contentTable.getResourceType().equalsIgnoreCase(FC_Constants.PDF))
                Objects.requireNonNull(ib_action_btn).setImageResource(R.drawable.ic_pdf);
            else if (contentTable.getResourceType().equalsIgnoreCase(FC_Constants.YOUTUBE_LINK))
                Objects.requireNonNull(ib_action_btn).setImageResource(R.drawable.ic_youtube);
            else if (contentTable.getResourceType().equalsIgnoreCase(FC_Constants.VIDEO))
                Objects.requireNonNull(ib_action_btn).setImageResource(R.drawable.ic_video);
            else if (contentTable.getResourceType().toLowerCase().contains(FC_Constants.GAME))
                Objects.requireNonNull(ib_action_btn).setImageResource(R.drawable.ic_joystick);
            else
                Objects.requireNonNull(ib_action_btn).setImageResource(R.drawable.ic_android_act);

            content_card_view.setOnClickListener(v -> itemClicked.onContentOpenClicked(
                    contentTable));

            try {
                if (contentTable.isOnSDCard())
                    file = new File(ApplicationClass.contentSDPath +
                            "" + App_Thumbs_Path + contentTable.getNodeImage());
                else
                    file = new File(ApplicationClass.foundationPath +
                            "" + App_Thumbs_Path + contentTable.getNodeImage());
                if (file.exists())
                    Objects.requireNonNull(thumbnail).setImageURI(Uri.fromFile(file));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (contentTable.isNodeUpdate())
                Objects.requireNonNull(ib_update_btn).setVisibility(View.VISIBLE);
            else
                Objects.requireNonNull(ib_update_btn).setVisibility(View.GONE);

            ib_update_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClicked.onContentDownloadClicked(contentTable,
                            parentPos, i, "" + SINGLE_RES_DOWNLOAD);
                }
            });

        } else {
            try {
                ImageRequest imageRequest = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(contentTable.getNodeServerImage()))
                        .setResizeOptions(new ResizeOptions(250, 170))
                        .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imageRequest)
                        .setOldController(Objects.requireNonNull(thumbnail).getController())
                        .build();
                thumbnail.setController(controller);

                if (contentTable.getResourceType().equalsIgnoreCase(FC_Constants.YOUTUBE_LINK)) {
                    Objects.requireNonNull(ib_action_btn).setImageResource(R.drawable.ic_youtube);
                    content_card_view.setOnClickListener(v -> itemClicked.onContentOpenClicked(contentTable));
                } else
                    content_card_view.setOnClickListener(v ->
                            itemClicked.onContentDownloadClicked(contentTable,
                                    parentPos, i, "" + SINGLE_RES_DOWNLOAD));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        setSlideAnimations(content_card_view);
    }
}