package com.pratham.foundation.ui.selectSubject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
            String path="";
            File file;
            myviewholder.content_title.setVisibility(View.GONE);
            if (datalist.get(i).getIsDownloaded().equalsIgnoreCase("1") ||
                    datalist.get(i).getIsDownloaded().equalsIgnoreCase("true")) {

                if (datalist.get(i).isOnSDCard()) {
                    path = ApplicationClass.contentSDPath +
                            "" + App_Thumbs_Path + datalist.get(i).getNodeImage();
                }else {
                    path =ApplicationClass.foundationPath +
                            "" + App_Thumbs_Path + datalist.get(i).getNodeImage();
                }
                file = new File(path);
                if (file.exists() && isValidImage(path))
                    myviewholder.content_thumbnail.setImageURI(Uri.fromFile(file));

                else {
                    myviewholder.content_title.setVisibility(View.VISIBLE);
                    myviewholder.content_title.setText(datalist.get(i).getNodeTitle());
                }

            }else{
                try {
//                    String myUrl = "http://devpos.prathamopenschool.org/CourseContent/images/posLogo.png";
//                    String myUrl2 = ""+myUrl.lastIndexOf('/');
//                    Log.d("prathamschool", "onBindViewHolder : "+myUrl2);
//                    ImageRequest imageRequest = ImageRequestBuilder
//                            .newBuilderWithSource(Uri.parse(myUrl))
                    myviewholder.content_title.setText(datalist.get(i).getNodeTitle());
                    ImageRequest imageRequest = ImageRequestBuilder
                            .newBuilderWithSource(Uri.parse(datalist.get(i).getNodeServerImage()))
                            .setLocalThumbnailPreviewsEnabled(true)
                            .build();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(imageRequest)
                            .setOldController(myviewholder.content_thumbnail.getController())
                            .build();
                    if(controller!=null)
                        myviewholder.content_thumbnail.setController(controller);
                    else {
                        myviewholder.content_title.setVisibility(View.VISIBLE);
                        myviewholder.content_title.setText(datalist.get(i).getNodeTitle());
                    }
                } catch (Exception e) {
                    myviewholder.content_title.setVisibility(View.VISIBLE);
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

        public SubjectHolder(@NonNull View itemView) {
            super(itemView);
            content_title = itemView.findViewById(R.id.content_title);
            content_thumbnail = itemView.findViewById(R.id.content_thumbnail);
        }
    }

}
