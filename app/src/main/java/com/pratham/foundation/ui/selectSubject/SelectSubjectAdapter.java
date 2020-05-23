package com.pratham.foundation.ui.selectSubject;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.pratham.foundation.ApplicationClass.App_Thumbs_Path;

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
            File file;
            if (datalist.get(i).getIsDownloaded().equalsIgnoreCase("1") ||
                    datalist.get(i).getIsDownloaded().equalsIgnoreCase("true")) {

                if (datalist.get(i).isOnSDCard())
                    file = new File(ApplicationClass.contentSDPath +
                            "" + App_Thumbs_Path + datalist.get(i).getNodeImage());
                else
                    file = new File(ApplicationClass.foundationPath +
                            "" + App_Thumbs_Path + datalist.get(i).getNodeImage());
                if (file.exists())
                    myviewholder.content_thumbnail.setImageURI(Uri.fromFile(file));
            }else{
                try {
//                    String myUrl = "http://devpos.prathamopenschool.org/CourseContent/images/posLogo.png";
//                    String myUrl2 = ""+myUrl.lastIndexOf('/');
//                    Log.d("prathamschool", "onBindViewHolder : "+myUrl2);
//                    ImageRequest imageRequest = ImageRequestBuilder
//                            .newBuilderWithSource(Uri.parse(myUrl))
                    ImageRequest imageRequest = ImageRequestBuilder
                            .newBuilderWithSource(Uri.parse(datalist.get(i).getNodeServerImage()))
                            .setLocalThumbnailPreviewsEnabled(true)
                            .build();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(imageRequest)
                            .setOldController(myviewholder.content_thumbnail.getController())
                            .build();
                    myviewholder.content_thumbnail.setController(controller);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            myviewholder.content_thumbnail.setOnClickListener(v -> {
                itemClicked.onItemClicked(datalist.get(i));
            });
        } catch (Exception e) {
            e.printStackTrace();
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

        public SubjectHolder(@NonNull View itemView) {
            super(itemView);
            content_thumbnail = itemView.findViewById(R.id.content_thumbnail);
        }
    }

}
