package com.pratham.foundation.ui.selectSubject;

import static com.pratham.foundation.ApplicationClass.App_Thumbs_Path;
import static com.pratham.foundation.utility.FC_Utility.getRandomOvalCard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.pratham.foundation.utility.FC_Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SelectSubjectAdapter extends RecyclerView.Adapter {
    Context context;
    List<ContentTable> datalist;
    List selectedOption = new ArrayList();
    SelectSubjectContract.ItemClicked itemClicked;


    public SelectSubjectAdapter(Context context, List<ContentTable> datalist) {
        this.context = context;
        this.datalist = datalist;
        itemClicked = (SelectSubjectContract.ItemClicked) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subject, viewGroup, false);
        return new SubjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder myViewHolder, int i) {
        try {
            SubjectHolder myviewholder = (SubjectHolder) myViewHolder;
            String path = "";
            File file;
            myviewholder.main_rl.setBackground(ApplicationClass.getInstance().getResources().getDrawable(getRandomOvalCard()));
            myviewholder.content_title.setText(datalist.get(i).getNodeTitle());
            if (datalist.get(i).getIsDownloaded().equalsIgnoreCase("1") ||
                    datalist.get(i).getIsDownloaded().equalsIgnoreCase("true")) {

                if (datalist.get(i).isOnSDCard()) {
                    path = ApplicationClass.contentSDPath +
                            "" + App_Thumbs_Path + datalist.get(i).getNodeImage();
                } else {
                    path = ApplicationClass.foundationPath +
                            "" + App_Thumbs_Path + datalist.get(i).getNodeImage();
                }
                file = new File(path);
                if (file.exists() && isValidImage(path))
                    myviewholder.content_thumbnail.setImageURI(Uri.fromFile(file));

            } else {
                try {
                    //Set name and image of subject
                    myviewholder.content_title.setText(datalist.get(i).getNodeTitle());
                    String thumbPath = "" + datalist.get(i).getNodeServerImage();
                    if (ApplicationClass.wiseF.isDeviceConnectedToMobileNetwork() || ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
                        if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                            String fileName = datalist.get(i).getNodeServerImage()
                                    .substring(datalist.get(i).getNodeServerImage().lastIndexOf('/') + 1);
                            thumbPath = FC_Constants.RASP_IP + FC_Constants.RASP_LOCAL_IMAGES + fileName;
                        } else {
                            thumbPath = "" + datalist.get(i).getNodeServerImage();
                        }
                    }
                    ImageRequest imageRequest = ImageRequestBuilder
                            .newBuilderWithSource(Uri.parse(thumbPath))
                            .setResizeOptions(new ResizeOptions(300, 300))
                            .build();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(imageRequest)
                            .setOldController(Objects.requireNonNull(myviewholder.content_thumbnail).getController())
                            .build();
                    if (controller != null)
                        myviewholder.content_thumbnail.setController(controller);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            myviewholder.main_rl.setOnClickListener(v -> {
                itemClicked.onItemClicked(datalist.get(i));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isValidImage(String f) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(f);
            return bitmap != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return (null != datalist ? datalist.size() : 0);
    }

    public List getSelectedOptionList() {
        return selectedOption;
    }

    public class SubjectHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView content_thumbnail;
        TextView content_title;
        RelativeLayout main_rl;

        public SubjectHolder(@NonNull View itemView) {
            super(itemView);
            content_title = itemView.findViewById(R.id.content_title);
            content_thumbnail = itemView.findViewById(R.id.content_thumbnail);
            main_rl = itemView.findViewById(R.id.main_rl);
        }
    }

}
