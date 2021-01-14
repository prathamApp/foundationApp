package com.pratham.foundation.ui.test.certificate;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.CertificateModelClass;
import com.pratham.foundation.view_holders.CertificateDisplayViewHolder;
import com.pratham.foundation.view_holders.CertificateSupervisorViewHolder;

import java.util.List;

/**
 * Created by Abc on 10-Jul-17.
 */

public class CertificateAdapter extends RecyclerView.Adapter{

    private Context mContext;
    private int lastPos = -1;
    //private List<ContentView> gamesViewList;
    private List<CertificateModelClass> certiViewList;
    CertificateClicked certificateClicked;
    public static int quesIndex = 0;

    public void initializeIndex(){
        quesIndex = 0;
    }

    public CertificateAdapter(Context mContext, List<CertificateModelClass> certiViewList, CertificateClicked certificateClicked) {
        this.mContext = mContext;
        this.certiViewList = certiViewList;
        this.certificateClicked = certificateClicked;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        // select view based on item type
        View view;
        switch (viewtype) {
            case 1:
                LayoutInflater file = LayoutInflater.from(viewGroup.getContext());
                view = file.inflate(R.layout.certi_row_new, viewGroup, false);
                return new CertificateDisplayViewHolder(view, certificateClicked);
            case 2:
                LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
                view = folder.inflate(R.layout.certi_supervisor_row, viewGroup, false);
                return new CertificateSupervisorViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (certiViewList.get(position).getNodeAge() != null) {
            switch (certiViewList.get(position).getNodeAge()) {
                case "SUP":
                    return 2;
                default:
                    return 1;
            }
        } else
            return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        //final ContentView gamesList = gamesViewList.get(position);
        final CertificateModelClass certiList = certiViewList.get(position);
        switch (certiList.getNodeAge()) {
            case "Q":
                //file
                CertificateDisplayViewHolder certificateDisplayViewHolder = (CertificateDisplayViewHolder) viewHolder;
//                set view holder for file type item
                certificateDisplayViewHolder.setItem(certiList, position);
                break;
            case "SUP":
                CertificateSupervisorViewHolder certificateSupervisorViewHolder = (CertificateSupervisorViewHolder) viewHolder;
//                set view holder for file type item
                certificateSupervisorViewHolder.setItem(certiList, position);
                break;
        }
    }

    private void setAnimations(final View content_card_view, final int position) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(mContext, R.anim.item_fall_down);
        animation.setDuration(500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*                if (position > lastPos
                ) {*/
                content_card_view.setVisibility(View.VISIBLE);
                content_card_view.setAnimation(animation);
                lastPos = position;
//                }
            }
        }, 20);

    }


    @Override
    public int getItemCount() {
        return certiViewList.size();
    }
}

/*private AppCompatActivity activity;
    private List<CertificatePercentModal> gameList;

    public CertificateAdapter(AppCompatActivity context,int resource, List<CertificatePercentModal> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.gameList = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.certi_row, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
            //holder.ratingBar.setTag(position);
        }

        final float growTo = 1f;
        final long duration = 2000;
        final AnimationSet growAndShrink;
        ScaleAnimation grow = new ScaleAnimation(0, growTo, 0, growTo, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        grow.setDuration(duration / 2);
*//*        ScaleAnimation shrink = new ScaleAnimation(growTo, 0, growTo, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(duration / 2);
        shrink.setStartOffset(duration / 2);*//*
        growAndShrink = new AnimationSet(true);
        growAndShrink.setInterpolator(new LinearInterpolator());
        growAndShrink.addAnimation(grow);
//        growAndShrink.addAnimation(shrink);

        holder.ratingBar.setTag(position);
        holder.ratingBar.setRating(getItem(position).getStudentPercentage()*//*test.getStudentPercentage()*//*);
        holder.assessData.setText(getItem(position).getCertitext());
        holder.ratingBar.startAnimation(growAndShrink);

        grow.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                *//*float current = holder.ratingBar.getRating();
                ObjectAnimator anim = ObjectAnimator.ofFloat(holder.ratingBar, "rating", current, getItem(position).getStudentPercentage());
                anim.setDuration(1500);
                anim.start();*//*
            }
        });


        return convertView;
    }

    private static class ViewHolder {
        private RatingBar ratingBar;
        private TextView assessData;

        public ViewHolder(View view) {
            ratingBar = (RatingBar) view.findViewById(R.id.RatingStars);
            assessData = (TextView) view.findViewById(R.id.assess_data);

        }
    }
}*/
