package com.pratham.foundation.ui.app_home.profile_new;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.foundation.R;


public class ProfileInnerDataAdapter extends RecyclerView.Adapter {

    private String[] itemsList;
    private String[] itemsImgList;
    private Context mContext;
    boolean dw_Ready = false;
    ProfileContract.ProfileItemClicked itemClicked;
    int parentPos = 0;
//    List maxScore;
    String parentName;
    private static final String TYPE_HEADER = "Header";
    private static final String TYPE_ITEM = "Resource";

    public ProfileInnerDataAdapter(Context context, String[] itemsList, String[] itemsImgList, ProfileContract.ProfileItemClicked profileItemClicked, int parentPos) {
        this.itemsList = itemsList;
        this.itemsImgList = itemsImgList;
        this.mContext = context;
        this.itemClicked = profileItemClicked;
        this.parentPos = parentPos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view;
        LayoutInflater header = LayoutInflater.from(viewGroup.getContext());
        view = header.inflate(R.layout.profile_item_card, viewGroup, false);
        return new ItemHolder(view);
/*        switch (viewtype) {
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
                view = file.inflate(R.layout.content_item_file_card, viewGroup, false);
                return new FileHolder(view);
            default:
                return null;
        }*/
    }

    @Override
    public int getItemViewType(int position) {
/*
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
*/
            return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ItemHolder itemHolder = (ItemHolder) viewHolder;
        itemHolder.tvTitle.setText(itemsList[i]);
        switch (itemsList[i]) {
            case "Certificate":
                itemHolder.item_Image.setImageResource(R.drawable.certificates);
                itemHolder.rl_root.setOnClickListener(v ->itemClicked.itemClicked("Certificate"));
                break;
            case "Projects":
                itemHolder.item_Image.setImageResource(R.drawable.lang_01);
                itemHolder.rl_root.setOnClickListener(v ->itemClicked.itemClicked("Projects"));
                break;
            case "Usage":
                itemHolder.item_Image.setImageResource(R.drawable.progress_report);
                itemHolder.rl_root.setOnClickListener(v ->itemClicked.itemClicked("Usage"));
                break;
            case "ImageQues":
                itemHolder.item_Image.setImageResource(R.drawable.ic_photo_camera_black);
                itemHolder.rl_root.setOnClickListener(v ->itemClicked.itemClicked("ImageQues"));
                break;
            case "Share Content":
                itemHolder.item_Image.setImageResource(R.drawable.ic_share_receive);
                itemHolder.rl_root.setOnClickListener(v ->itemClicked.itemClicked("Share Content"));
                break;
            case "Share App":
                itemHolder.item_Image.setImageResource(R.drawable.app_share);
                itemHolder.rl_root.setOnClickListener(v ->itemClicked.itemClicked("Share App"));
                break;
        }
    }

/*        switch (viewHolder.getItemViewType()) {
            case 1:
                //folder
                FolderHolder folderHolder = (FolderHolder) viewHolder;
//                int randomNo = getRandomDrawableGradiant();
//                folderHolder.rl_root.setBackground(mContext.getResources().getDrawable(randomNo));
                folderHolder.tvTitle.setText(itemsList.get(i).getNodeTitle());
                folderHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.colorText));
                folderHolder.progressLayout.setCurProgress(Integer.parseInt(itemsList.get(i).getNodePercentage()));
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
                File file;
                if (itemsList.get(i).getIsDownloaded().equalsIgnoreCase("1") ||
                        itemsList.get(i).getIsDownloaded().equalsIgnoreCase("true")) {

                    fileHolder.actionBtn.setVisibility(View.GONE);*//*setImageResource(R.drawable.ic_joystick);*//*
                    fileHolder.rl_root.setOnClickListener(new View.OnClickListener() {
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

                    fileHolder.rl_root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClicked.onContentDownloadClicked(itemsList.get(i),parentPos,i,""+ FC_Constants.SINGLE_RES_DOWNLOAD);
                        }
                    });

                }
                break;
        }*/
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
        public int getItemCount () {
            return itemsList.length;
        }

        public class ItemHolder extends RecyclerView.ViewHolder {

            protected TextView tvTitle;
            protected ImageView item_Image;
            protected RelativeLayout rl_root;

            public ItemHolder(View view) {
                super(view);
                this.tvTitle = view.findViewById(R.id.tv_title);
                this.item_Image = view.findViewById(R.id.item_Image);
                this.rl_root = view.findViewById(R.id.rl_root);
            }

        }

/*    public class FileHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected ImageView actionBtn;
        SimpleDraweeView itemImage;
        protected RelativeLayout rl_root;

        public FileHolder(View view) {
            super(view);
            this.tvTitle = view.findViewById(R.id.file_Title);
            this.itemImage = view.findViewById(R.id.file_Image);
            this.actionBtn = view.findViewById(R.id.btn_file_download);
            this.rl_root = view.findViewById(R.id.file_card_view);
        }

    }

    public class FolderHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        SimpleDraweeView itemImage;
        protected RelativeLayout rl_root;
        protected ProgressLayout progressLayout;

        public FolderHolder(View view) {
            super(view);
            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.itemImage = view.findViewById(R.id.item_Image);
            this.rl_root = view.findViewById(R.id.rl_root);
            progressLayout = view.findViewById(R.id.card_progressLayout);
        }

    }

    public class EmptyHolder extends RecyclerView.ViewHolder {
        public EmptyHolder(View view) {
            super(view);
        }

    }*/
}