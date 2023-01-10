package com.pratham.foundation.ui.contentPlayer.sequenceLayout;

import static com.pratham.foundation.ApplicationClass.App_Thumbs_Path;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;

import java.io.File;
import java.util.List;

public class SequenceGameAdapter extends RecyclerView.Adapter<SequenceGameAdapter.MyViewHolder> {
    private final Context context;
    List<ContentTable> gamesList;
    SequeanceLayoutContract.clickListner clickListner;

    public SequenceGameAdapter(Context context, List<ContentTable> gamesList, SequeanceLayoutContract.clickListner clickListner) {
        this.context = context;
        this.gamesList = gamesList;
        this.clickListner = clickListner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_card, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        ContentTable contentTable = gamesList.get(i);
        myViewHolder.content_card_view.setBackground(context.getResources().getDrawable(R.drawable.new_card_color_bg3));
        myViewHolder.title.setText(contentTable.getNodeTitle());
        myViewHolder.ib_action_btn.setVisibility(View.GONE);
        myViewHolder.tv_progress.setVisibility(View.GONE);

        File f;
        if (contentTable.getIsDownloaded().equalsIgnoreCase("1") ||
                contentTable.getIsDownloaded().equalsIgnoreCase("true")) {
            if (contentTable.isOnSDCard()) {
                f = new File(ApplicationClass.contentSDPath +
                        "" + App_Thumbs_Path + contentTable.getNodeImage());
                if (f.exists()) {
                    myViewHolder.thumbnail.setImageURI(Uri.fromFile(f));
                }
            } else {
                f = new File(ApplicationClass.foundationPath +
                        "" + App_Thumbs_Path + contentTable.getNodeImage());
                if (f.exists()) {
                    myViewHolder.thumbnail.setImageURI(Uri.fromFile(f));
                }
            }
        } else {
            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(contentTable.getNodeServerImage()))
                    .setResizeOptions(new ResizeOptions(300, 200))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setOldController(myViewHolder.thumbnail.getController())
                    .build();
            myViewHolder.thumbnail.setController(controller);
        }

        myViewHolder.content_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameConstatnts.currentGameAdapterposition=myViewHolder.getAdapterPosition();
                ContentTable contentTable1 = gamesList.get(myViewHolder.getAdapterPosition());
                clickListner.contentClicked(contentTable1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gamesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title,tv_progress;
        public ImageView ib_action_btn;
        public MaterialCardView content_card_view;
        SimpleDraweeView thumbnail;

        public MyViewHolder (View view) {
            super(view);
            title = view.findViewById(R.id.content_title);
            thumbnail = view.findViewById(R.id.content_image);
            content_card_view = view.findViewById(R.id.content_card_view);
            ib_action_btn = view.findViewById(R.id.ib_action_btn);
            tv_progress = view.findViewById(R.id.tv_progress);
        }
    }
}