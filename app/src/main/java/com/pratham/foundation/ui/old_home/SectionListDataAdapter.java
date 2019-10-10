package com.pratham.foundation.ui.old_home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.custumView.progress_layout.ProgressLayout;
import com.pratham.foundation.database.domain.ContentTableNew;
import com.pratham.foundation.utility.FC_Constants;


import java.io.File;
import java.io.FileInputStream;
import java.util.List;


public class SectionListDataAdapter extends RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder> {

    private List<ContentTableNew> itemsList;
    private Context mContext;
    boolean dw_Ready = false;
    ItemClicked itemClicked;
    int parentPos = 0;
//    List maxScore;


    private static final String TYPE_HEADER = "Header";
    private static final String TYPE_ITEM = "Resource";


    public SectionListDataAdapter(Context context, List<ContentTableNew> itemsList, ItemClicked itemClicked, int parentPos) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.itemClicked = itemClicked;
        this.parentPos = parentPos;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = null;

        if (viewType == 0)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_item_file_header, null);
        if (viewType == 2)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_item_file_card, null);
        if (viewType == 1) {
            if (!FC_Constants.TAB_LAYOUT)
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_item_folder_card_tab, null);
            else
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_item_folder_card_tab, null);
        }
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
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
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {
        ContentTableNew singleItem = itemsList.get(i);

        if (singleItem.getNodeType() != null) {
            if (singleItem.getNodeType().equalsIgnoreCase(TYPE_HEADER)) {

            } else if (singleItem.getNodeType().equalsIgnoreCase("resList")) {

            } else {
                dw_Ready = false;
                holder.tvTitle.setText(singleItem.getNodeTitle());
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.place_holder_temp);
                requestOptions.error(R.drawable.place_holder_temp);
                if (singleItem.getIsDownloaded() == null)
                    singleItem.setIsDownloaded("false");

                if ((singleItem.getIsDownloaded().equalsIgnoreCase("true") ||
                        singleItem.getIsDownloaded().equalsIgnoreCase("1"))
                        && !singleItem.getNodeType().equalsIgnoreCase("Resource")) {
                    try {
                            holder.progressLayout.setCurProgress(Integer.parseInt(singleItem.getNodePercentage()));
                        File f;
                        if (singleItem.isOnSDCard()) {
                            f = new File(ApplicationClass.contentSDPath +
                                    "/.LLA/English/LLA_Thumbs/"+ singleItem.getNodeImage());
                            if (f.exists()) {
                                Bitmap bmImg = BitmapFactory.decodeFile(ApplicationClass.contentSDPath +
                                        "/.LLA/English/LLA_Thumbs/" + singleItem.getNodeImage());
                                BitmapFactory.decodeStream(new FileInputStream(ApplicationClass.contentSDPath +
                                        "/.LLA/English/LLA_Thumbs/" + singleItem.getNodeImage()));
                                holder.itemImage.setImageBitmap(bmImg);
                            } else
                                holder.itemImage.setImageResource(R.drawable.place_holder_temp);
                        } else {
                            f = new File(ApplicationClass.foundationPath +
                                    "/.LLA/English/LLA_Thumbs/" + singleItem.getNodeImage());
                            if (f.exists()) {
                                Bitmap bmImg = BitmapFactory.decodeFile(ApplicationClass.foundationPath +
                                        "/.LLA/English/LLA_Thumbs/" + singleItem.getNodeImage());
                                BitmapFactory.decodeStream(new FileInputStream(ApplicationClass.foundationPath +
                                        "/.LLA/English/LLA_Thumbs/" + singleItem.getNodeImage()));
                                holder.itemImage.setImageBitmap(bmImg);
                            } else
                                holder.itemImage.setImageResource(R.drawable.place_holder_temp);
                        }
                    } catch (Exception e) {
                        holder.itemImage.setImageResource(R.drawable.place_holder_temp);
                    }
                    holder.rl_root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClicked.onContentClicked(singleItem);
                        }
                    });
                    holder.actionBtn.setVisibility(View.GONE);/*setImageResource(R.drawable.ic_joystick);*/
                } else if ((singleItem.getIsDownloaded().equalsIgnoreCase("false") ||
                        singleItem.getIsDownloaded().equalsIgnoreCase("0"))
                        && !singleItem.getNodeType().equalsIgnoreCase("Resource")) {
                    dw_Ready = true;
                    holder.actionBtn.setImageResource(R.drawable.ic_download);
                    Glide.with(mContext).setDefaultRequestOptions(requestOptions)
                            .load(singleItem.getNodeServerImage())
                            .into(holder.itemImage);

                    holder.rl_root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClicked.onContentClicked(singleItem);
                        }
                    });
                }

                if (singleItem.getNodeType().equalsIgnoreCase("Resource")
                        && (singleItem.getIsDownloaded().equalsIgnoreCase("true") ||
                        singleItem.getIsDownloaded().equalsIgnoreCase("1"))) {
                    try {
                        File f;
                        if (singleItem.isOnSDCard()) {
                            f = new File(ApplicationClass.contentSDPath +
                                    "/.LLA/English/LLA_Thumbs/" + singleItem.getNodeImage());
                            if (f.exists()) {
                                Bitmap bmImg = BitmapFactory.decodeFile(ApplicationClass.contentSDPath +
                                        "/.LLA/English/LLA_Thumbs/" + singleItem.getNodeImage());
                                BitmapFactory.decodeStream(new FileInputStream(ApplicationClass.contentSDPath +
                                        "/.LLA/English/LLA_Thumbs/" + singleItem.getNodeImage()));
                                holder.itemImage.setImageBitmap(bmImg);
                            } else
                                holder.itemImage.setImageResource(R.drawable.place_holder_temp);
                        } else {
                            f = new File(ApplicationClass.foundationPath +
                                    "/.LLA/English/LLA_Thumbs/" + singleItem.getNodeImage());
                            if (f.exists()) {
                                Bitmap bmImg = BitmapFactory.decodeFile(ApplicationClass.foundationPath +
                                        "/.LLA/English/LLA_Thumbs/" + singleItem.getNodeImage());
                                BitmapFactory.decodeStream(new FileInputStream(ApplicationClass.foundationPath +
                                        "/.LLA/English/LLA_Thumbs/" + singleItem.getNodeImage()));
                                holder.itemImage.setImageBitmap(bmImg);
                            } else
                                holder.itemImage.setImageResource(R.drawable.place_holder_temp);
                        }
                    } catch (Exception e) {
                        holder.itemImage.setImageResource(R.drawable.place_holder_temp);
                    }
                    holder.actionBtn.setVisibility(View.GONE);/*setImageResource(R.drawable.ic_joystick);*/
                    holder.rl_root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClicked.onContentOpenClicked(singleItem);
                        }
                    });
                } else if (singleItem.getNodeType().equalsIgnoreCase("Resource")
                        && (singleItem.getIsDownloaded().equalsIgnoreCase("false") ||
                        singleItem.getIsDownloaded().equalsIgnoreCase("0"))) {
                    try {
                        Glide.with(mContext).setDefaultRequestOptions(requestOptions)
                                .load(singleItem.getNodeServerImage())
                                .into(holder.itemImage);
                    } catch (Exception e) {
                        holder.itemImage.setImageResource(R.drawable.place_holder_temp);
                    }
                    holder.rl_root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClicked.onContentDownloadClicked(singleItem, parentPos, i, "" + FC_Constants.SINGLE_RES_DOWNLOAD);
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected ImageView itemImage, actionBtn;
        protected RelativeLayout rl_root;
        protected ProgressLayout progressLayout;

        public SingleItemRowHolder(View view) {
            super(view);
            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            this.itemImage = (ImageView) view.findViewById(R.id.item_Image);
            this.actionBtn = (ImageView) view.findViewById(R.id.ib_action_btn);
            this.rl_root = (RelativeLayout) view.findViewById(R.id.rl_root);
            progressLayout = view.findViewById(R.id.card_progressLayout);
        }

    }

}