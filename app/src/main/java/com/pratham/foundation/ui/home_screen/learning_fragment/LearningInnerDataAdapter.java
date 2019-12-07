package com.pratham.foundation.ui.home_screen.learning_fragment;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.utility.FC_Constants;

import java.io.File;
import java.util.List;

import static com.pratham.foundation.ApplicationClass.App_Thumbs_Path;
import static com.pratham.foundation.utility.FC_Utility.getRandomCardColor;


public class LearningInnerDataAdapter extends RecyclerView.Adapter {

    private List<ContentTable> itemsList;
    private Context mContext;
    boolean dw_Ready = false;
    LearningContract.LearningItemClicked itemClicked;
    int parentPos = 0;
    String parentName;
    private static final String TYPE_HEADER = "Header";
    private static final String TYPE_ITEM = "Resource";

    public LearningInnerDataAdapter(Context context, List<ContentTable> itemsList, LearningContract.LearningItemClicked itemClicked, int parentPos, String parentName) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.itemClicked = itemClicked;
        this.parentPos = parentPos;
        this.parentName = parentName;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view;
        switch (viewtype) {
            case 0:
                LayoutInflater header = LayoutInflater.from(viewGroup.getContext());
                view = header.inflate(R.layout.content_item_file_header, viewGroup, false);
                return new EmptyHolder(view);
            case 1:
                LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
                view = folder.inflate(R.layout.content_item_folder_card_tab, viewGroup, false);
                return new FolderHolder(view);
            case 2:
                LayoutInflater file = LayoutInflater.from(viewGroup.getContext());
                view = file.inflate(R.layout.content_card, viewGroup, false);
                return new FileHolder(view);
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (itemsList.get(position).getNodeType() != null) {
            switch (itemsList.get(position).getNodeType()) {
                case TYPE_HEADER:
                    return 0;
                case TYPE_ITEM:
                    return 2;
                default:
                    return 1;
            }
        } else
            return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (viewHolder.getItemViewType()) {
            case 1:
                //folder
                FolderHolder folderHolder = (FolderHolder) viewHolder;
                folderHolder.card_main.setBackground(mContext.getResources().getDrawable(getRandomCardColor()));
                folderHolder.tvTitle.setText(itemsList.get(i).getNodeTitle());
                folderHolder.tv_progress.setText(itemsList.get(i).getNodePercentage()+"%");
//                folderHolder.progressLayout.setCurProgress(Integer.parseInt(itemsList.get(i).getNodePercentage()));
                File f;
                if (itemsList.get(i).getIsDownloaded().equalsIgnoreCase("1") ||
                        itemsList.get(i).getIsDownloaded().equalsIgnoreCase("true")) {
                    if (itemsList.get(i).isOnSDCard()) {
                        f = new File(ApplicationClass.contentSDPath +
                                "" + App_Thumbs_Path + itemsList.get(i).getNodeImage());
                        if (f.exists()) {
                            folderHolder.itemImage.setImageURI(Uri.fromFile(f));
                        }
                    } else {
                        f = new File(ApplicationClass.foundationPath +
                                "" + App_Thumbs_Path + itemsList.get(i).getNodeImage());
                        if (f.exists()) {
                            folderHolder.itemImage.setImageURI(Uri.fromFile(f));
                        }
                    }
                } else {
                    ImageRequest imageRequest = ImageRequestBuilder
                            .newBuilderWithSource(Uri.parse(itemsList.get(i).getNodeServerImage()))
                            .setResizeOptions(new ResizeOptions(300, 200))
                            .setLocalThumbnailPreviewsEnabled(true)
                            .build();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(imageRequest)
                            .setOldController(folderHolder.itemImage.getController())
                            .build();
                    folderHolder.itemImage.setController(controller);

                }
                folderHolder.rl_root.setOnClickListener(v -> itemClicked.onContentClicked(itemsList.get(i), parentName));

                break;
            case 2:
                //file
                FileHolder fileHolder = (FileHolder) viewHolder;
                fileHolder.tvTitle.setText(itemsList.get(i).getNodeTitle());
                fileHolder.content_card_view.setBackground(mContext.getResources().getDrawable(getRandomCardColor()));
                File file;
                if (itemsList.get(i).getIsDownloaded().equalsIgnoreCase("1") ||
                        itemsList.get(i).getIsDownloaded().equalsIgnoreCase("true")) {

                    fileHolder.actionBtn.setVisibility(View.GONE);/*setImageResource(R.drawable.ic_joystick);*/
                    fileHolder.content_card_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClicked.onContentOpenClicked(itemsList.get(i));
                        }
                    });

                    if (itemsList.get(i).isOnSDCard()) {
                        file = new File(ApplicationClass.contentSDPath +
                                "" + App_Thumbs_Path + itemsList.get(i).getNodeImage());
                        if (file.exists()) {
                            fileHolder.itemImage.setImageURI(Uri.fromFile(file));
                        }
                    } else {
                        file = new File(ApplicationClass.foundationPath +
                                "" + App_Thumbs_Path + itemsList.get(i).getNodeImage());
                        if (file.exists()) {
                            fileHolder.itemImage.setImageURI(Uri.fromFile(file));
                        }
                    }
                }else {
                    ImageRequest imageRequest = ImageRequestBuilder
                            .newBuilderWithSource(Uri.parse(itemsList.get(i).getNodeServerImage()))
                            .setResizeOptions(new ResizeOptions(300, 200))
                            .setLocalThumbnailPreviewsEnabled(true)
                            .build();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(imageRequest)
                            .setOldController(fileHolder.itemImage.getController())
                            .build();
                    fileHolder.itemImage.setController(controller);

                    fileHolder.content_card_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClicked.onContentDownloadClicked(itemsList.get(i),parentPos,i,""+ FC_Constants.SINGLE_RES_DOWNLOAD);
                        }
                    });

                }
                break;
        }
    }
//    ImageRequest imageRequest = ImageRequestBuilder
//            .newBuilderWithSource(Uri.parse(singleItem.getNodeServerImage()))
//            .setResizeOptions(new ResizeOptions(300, 200))
//            .setLocalThumbnailPreviewsEnabled(true)
//            .build();
//    DraweeController controller = Fresco.newDraweeControllerBuilder()
//            .setImageRequest(imageRequest)
//            .setOldController(holder.itemImage.getController())
//            .build();
//                        holder.itemImage.setController(controller);

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class FileHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView  actionBtn;
        SimpleDraweeView itemImage;
        public MaterialCardView content_card_view;

        FileHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.content_title);
            itemImage = view.findViewById(R.id.content_thumbnail);
            content_card_view = view.findViewById(R.id.content_card_view);
            actionBtn = view.findViewById(R.id.ib_action_btn);
        }
    }

    public class FolderHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        protected TextView tv_progress;
        SimpleDraweeView itemImage;
        RelativeLayout rl_root;
//        protected ProgressLayout progressLayout;
        MaterialCardView card_main;

        FolderHolder(View view) {
            super(view);
            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.tv_progress = view.findViewById(R.id.tv_progress);
            this.itemImage = view.findViewById(R.id.item_Image);
            this.rl_root = view.findViewById(R.id.rl_root);
            this.card_main = view.findViewById(R.id.card_main);
//            progressLayout = view.findViewById(R.id.card_progressLayout);
        }

    }

    public class EmptyHolder extends RecyclerView.ViewHolder {
        EmptyHolder(View view) {
            super(view);
        }

    }

}