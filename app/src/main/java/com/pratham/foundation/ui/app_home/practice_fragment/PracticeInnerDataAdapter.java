package com.pratham.foundation.ui.app_home.practice_fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.ui.app_home.FragmentItemClicked;
import com.pratham.foundation.view_holders.ContentFileViewHolder;
import com.pratham.foundation.view_holders.ContentFolderViewHolder;
import com.pratham.foundation.view_holders.EmptyHolder;

import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.TYPE_FOOTER;
import static com.pratham.foundation.utility.FC_Constants.TYPE_HEADER;
import static com.pratham.foundation.utility.FC_Constants.TYPE_ITEM;


public class PracticeInnerDataAdapter extends RecyclerView.Adapter {

    private List<ContentTable> itemsList;
    private Context mContext;
    boolean dw_Ready = false;
//    PracticeContract.PracticeItemClicked itemClicked;
    FragmentItemClicked itemClicked;
    int parentPos = 0;
//    List maxScore;
    String parentName;


    public PracticeInnerDataAdapter(Context context, List<ContentTable> itemsList,
                                    FragmentItemClicked itemClicked, int parentPos, String parentName) {
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
                return new ContentFolderViewHolder(view,itemClicked);
            case 2:
                LayoutInflater file = LayoutInflater.from(viewGroup.getContext());
                view = file.inflate(R.layout.content_item_file_card, viewGroup, false);
                return new ContentFileViewHolder(view,itemClicked);
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (itemsList.get(position).getNodeType() != null) {
            switch (itemsList.get(position).getNodeType()) {
                case TYPE_HEADER:
                case TYPE_FOOTER:
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
                ContentFolderViewHolder folderHolder = (ContentFolderViewHolder) viewHolder;
                folderHolder.setFragmentFolderItem(itemsList.get(i),i,parentName,parentPos);
//                FolderHolder folderHolder = (FolderHolder) viewHolder;
//                folderHolder.card_main.setBackground(mContext.getResources().getDrawable(getRandomCardColor()));
//                folderHolder.tvTitle.setText(itemsList.get(i).getNodeTitle());
//                folderHolder.tv_progress.setText(itemsList.get(i).getNodePercentage()+"%");
////                folderHolder.progressLayout.setCurProgress(Integer.parseInt(itemsList.get(i).getNodePercentage()));
//                File f;
//                if (itemsList.get(i).getIsDownloaded().equalsIgnoreCase("1") ||
//                        itemsList.get(i).getIsDownloaded().equalsIgnoreCase("true")) {
//                    if (itemsList.get(i).isOnSDCard())
//                        f = new File(ApplicationClass.contentSDPath +
//                                "" + App_Thumbs_Path + itemsList.get(i).getNodeImage());
//                    else
//                        f = new File(ApplicationClass.foundationPath +
//                                "" + App_Thumbs_Path + itemsList.get(i).getNodeImage());
//                    if (f.exists())
//                        folderHolder.itemImage.setImageURI(Uri.fromFile(f));
//                } else {
////                    String myUrl = "http://devpos.prathamopenschool.org/CourseContent/images/posLogo.png";
////                    String myUrl2 = ""+myUrl.lastIndexOf('/');
//
//                    ImageRequest imageRequest = ImageRequestBuilder
//                            .newBuilderWithSource(Uri.parse(itemsList.get(i).getNodeServerImage()))
////                    ImageRequest imageRequest = ImageRequestBuilder
////                            .newBuilderWithSource(Uri.parse(myUrl))
//                            .setResizeOptions(new ResizeOptions(300, 300))
//                            .build();
//                    DraweeController controller = Fresco.newDraweeControllerBuilder()
//                            .setImageRequest(imageRequest)
//                            .setOldController(folderHolder.itemImage.getController())
//                            .build();
//                    folderHolder.itemImage.setController(controller);
//                }
//
//                if (itemsList.get(i).getNodeType().equalsIgnoreCase("PreResource")) {
//                    if (itemsList.get(i).getIsDownloaded().equalsIgnoreCase("true")) {
//                        folderHolder.iv_downld.setVisibility(View.GONE);
//                    } else if (itemsList.get(i).getIsDownloaded().equalsIgnoreCase("false"))
//                        folderHolder.iv_downld.setVisibility(View.VISIBLE);
//                } else
//                    folderHolder.iv_downld.setVisibility(View.GONE);
//
//                folderHolder.card_main.setOnClickListener(v -> {
//                    if (itemsList.get(i).getNodeType() != null) {
//                        if (itemsList.get(i).getNodeType().equalsIgnoreCase("PreResource")) {
//                            if (itemsList.get(i).getIsDownloaded().equalsIgnoreCase("true")) {
//                                itemClicked.onPreResOpenClicked(i, itemsList.get(i).getNodeId(), itemsList.get(i).getNodeTitle(),itemsList.get(i).isOnSDCard());
//                            } else if (itemsList.get(i).getIsDownloaded().equalsIgnoreCase("false"))
//                                itemClicked.onContentDownloadClicked(itemsList.get(i),parentPos,i, SINGLE_RES_DOWNLOAD);
//                        } else
//                            itemClicked.onContentClicked(itemsList.get(i), parentName);
//                    }
//                });
////                folderHolder.rl_root.setOnClickListener(v -> itemClicked.onContentClicked(itemsList.get(i), parentName));
//                setAnimations(folderHolder.card_main);
                break;

            case 2:
                //file
                ContentFileViewHolder fileHolder = (ContentFileViewHolder) viewHolder;
                fileHolder.setFragmentFileItem(itemsList.get(i),i,parentName,parentPos);
//                FileHolder fileHolder = (FileHolder) viewHolder;
//                fileHolder.tvTitle.setText(itemsList.get(i).getNodeTitle());
//                fileHolder.tvTitle.setSelected(true);
//                fileHolder.content_card_view.setBackground(mContext.getResources().getDrawable(getRandomCardColor()));
//                File file;
//                if (itemsList.get(i).getIsDownloaded().equalsIgnoreCase("1") ||
//                        itemsList.get(i).getIsDownloaded().equalsIgnoreCase("true")) {
//
////                    fileHolder.actionBtn.setVisibility(View.GONE);
//                    if(itemsList.get(i).getResourceType().equalsIgnoreCase(FC_Constants.VIDEO))
//                        fileHolder.actionBtn.setImageResource(R.drawable.ic_video);
//                    else if(itemsList.get(i).getResourceType().toLowerCase().contains(FC_Constants.GAME))
//                        fileHolder.actionBtn.setImageResource(R.drawable.ic_joystick);
//                    else
//                        fileHolder.actionBtn.setImageResource(R.drawable.ic_android_act);
//
//                    fileHolder.content_card_view.setOnClickListener(v -> itemClicked.onContentOpenClicked(
//                            itemsList.get(i)));
//
//                    try {
//                        if (itemsList.get(i).isOnSDCard())
//                            file = new File(ApplicationClass.contentSDPath +
//                                    "" + App_Thumbs_Path + itemsList.get(i).getNodeImage());
//                        else
//                            file = new File(ApplicationClass.foundationPath +
//                                    "" + App_Thumbs_Path + itemsList.get(i).getNodeImage());
//                        if (file.exists())
//                            fileHolder.itemImage.setImageURI(Uri.fromFile(file));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }else {
//                    try {
////                        String myUrl = "http://devpos.prathamopenschool.org/CourseContent/images/posLogo.png";
////                        String myUrl2 = ""+myUrl.lastIndexOf('/');
//                        ImageRequest imageRequest = ImageRequestBuilder
//                                .newBuilderWithSource(Uri.parse(itemsList.get(i).getNodeServerImage()))
////                        ImageRequest imageRequest = ImageRequestBuilder
////                                .newBuilderWithSource(Uri.parse(myUrl))
//                                .setResizeOptions(new ResizeOptions(250, 170))
//                                .build();
//                        DraweeController controller = Fresco.newDraweeControllerBuilder()
//                                .setImageRequest(imageRequest)
//                                .setOldController(fileHolder.itemImage.getController())
//                                .build();
//                        fileHolder.itemImage.setController(controller);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    fileHolder.content_card_view.setOnClickListener(v ->
//                            itemClicked.onContentDownloadClicked(itemsList.get(i),
//                                    parentPos,i,""+ SINGLE_RES_DOWNLOAD));
//                }
//                setAnimations(fileHolder.content_card_view);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

/*    private void setAnimations(final View content_card_view) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_list);
        animation.setDuration(500);
        content_card_view.setVisibility(View.VISIBLE);
        content_card_view.setAnimation(animation);
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
        MaterialCardView card_main;
        ImageView iv_downld;

        FolderHolder(View view) {
            super(view);
            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.tv_progress = view.findViewById(R.id.tv_progress);
            this.itemImage = view.findViewById(R.id.item_Image);
            this.rl_root = view.findViewById(R.id.rl_root);
            this.card_main = view.findViewById(R.id.card_main);
            this.iv_downld = view.findViewById(R.id.iv_downld);
        }

    }

    public class EmptyHolder extends RecyclerView.ViewHolder {
        EmptyHolder(View view) {
            super(view);
        }
    }*/
}