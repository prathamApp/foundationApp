package com.pratham.foundation.ui.app_home;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.CertificateModelClass;
import com.pratham.foundation.ui.test.certificate.CertificateClicked;

import java.util.List;

/**
 * Created by Abc on 10-Jul-17.
 */

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.MyViewHolder> {

    private Context mContext;
    private int lastPos = -1;
    //private List<ContentView> gamesViewList;
    private List<CertificateModelClass> testList;
    CertificateClicked certificateClicked;
    public int quesIndex = 0;
    String language;

    public void initializeIndex() {
        quesIndex = 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public RelativeLayout certificate_card;
        public RatingBar ratingStars;

        public MyViewHolder(View view) {
            super(view);
            certificate_card = (RelativeLayout) view.findViewById(R.id.certificate_card_view);
            title = (TextView) view.findViewById(R.id.assess_data);
            ratingStars = (RatingBar) view.findViewById(R.id.ratingStars);
        }
    }

    public TestAdapter(Context mContext, List<CertificateModelClass> testList, String language) {
        this.mContext = mContext;
        this.testList = testList;
        this.certificateClicked = (CertificateClicked) mContext;
        this.language = language;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.certi_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //final ContentView gamesList = gamesViewList.get(position);

        Animation animation = null;
        animation = AnimationUtils.loadAnimation(mContext, R.anim.item_fall_down);
        animation.setDuration(500);

        String ques = "";

        if (testList.get(position).getCodeCount() > 1) {
            quesIndex += 1;
            ques = "" + quesIndex + ". ";
        } else {
//            quesIndex = 0;
            ques = "";
        }

        holder.title.setTypeface(null, Typeface.NORMAL);

        if (!testList.get(position).isAsessmentGiven()) {
            holder.certificate_card.setClickable(true);
            holder.ratingStars.setRating(0);
            if (language.equalsIgnoreCase("English"))
                holder.title.setText(ques + testList.get(position).getEnglishQues());
            if (language.equalsIgnoreCase("Hindi"))
                holder.title.setText(ques + testList.get(position).getHindiQues());
            if (language.equalsIgnoreCase("Marathi"))
                holder.title.setText(ques + testList.get(position).getMarathiQues());
            if (language.equalsIgnoreCase("Gujarati")) {
                Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/muktavaani_gujarati.ttf");
                holder.title.setTypeface(face);
                holder.title.setText(ques + testList.get(position).getGujaratiQues());
            }
            if (language.equalsIgnoreCase("Kannada"))
                holder.title.setText(ques + testList.get(position).getKannadaQues());
            if (language.equalsIgnoreCase("Bengali"))
                holder.title.setText(ques + testList.get(position).getBengaliQues());
            if (language.equalsIgnoreCase("Assamese")) {
                Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/lohit_oriya.ttf");
                holder.title.setTypeface(face);
                holder.title.setText(ques + testList.get(position).getAssameseQues());
            }
            if (language.equalsIgnoreCase("Telugu"))
                holder.title.setText(ques + testList.get(position).getTeluguQues());
            if (language.equalsIgnoreCase("Tamil"))
                holder.title.setText(ques + testList.get(position).getTamilQues());
            if (language.equalsIgnoreCase("Odiya")) {
                Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/lohit_oriya.ttf");
                holder.title.setTypeface(face);
                holder.title.setText(ques + testList.get(position).getOdiaQues());
            }
            if (language.equalsIgnoreCase("Malayalam"))
                holder.title.setText(ques + testList.get(position).getUrduQues());
            if (language.equalsIgnoreCase("Punjabi")) {
                Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/raavi_punjabi.ttf");
                holder.title.setTypeface(face);
                holder.title.setText(ques + testList.get(position).getPunjabiQues());
            }

            holder.certificate_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    certificateClicked.onCertificateOpenGame(position, testList.get(position).getNodeId());
                }
            });
        } else {
            holder.certificate_card.setClickable(false);
            holder.ratingStars.setVisibility(View.VISIBLE);
            if (language.equalsIgnoreCase("English"))
                holder.title.setText(ques + testList.get(position).getEnglishAnsw());
            if (language.equalsIgnoreCase("Hindi"))
                holder.title.setText(ques + testList.get(position).getHindiAnsw());
            if (language.equalsIgnoreCase("Marathi"))
                holder.title.setText(ques + testList.get(position).getMarathiAnsw());
            if (language.equalsIgnoreCase("Gujarati")) {
                Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/muktavaani_gujarati.ttf");
                holder.title.setTypeface(face);
                holder.title.setText(ques + testList.get(position).getGujaratiAnsw());
            }
            if (language.equalsIgnoreCase("Kannada"))
                holder.title.setText(ques + testList.get(position).getKannadaAnsw());
            if (language.equalsIgnoreCase("Bengali"))
                holder.title.setText(ques + testList.get(position).getBengaliAnsw());
            if (language.equalsIgnoreCase("Assamese")) {
                Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/geetl_assamese.ttf");
                holder.title.setTypeface(face);
                holder.title.setText(ques + testList.get(position).getAssameseAnsw());
            }
            if (language.equalsIgnoreCase("Telugu"))
                holder.title.setText(ques + testList.get(position).getTeluguAnsw());
            if (language.equalsIgnoreCase("Tamil"))
                holder.title.setText(ques + testList.get(position).getTamilAnsw());
            if (language.equalsIgnoreCase("Odia")) {
                Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/lohit_oriya.ttf");
                holder.title.setTypeface(face);
                holder.title.setText(ques + testList.get(position).getOdiaAnsw());
            }
            if (language.equalsIgnoreCase("Malayalam"))
                holder.title.setText(ques + testList.get(position).getUrduAnsw());
            if (language.equalsIgnoreCase("Punjabi")) {
                Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/raavi_punjabi.ttf");
                holder.title.setTypeface(face);
                holder.title.setText(ques + testList.get(position).getPunjabiAnsw());
            }
            holder.ratingStars.setRating(testList.get(position).getCertificateRating());

        }

    }

    private void setAnimations(final View content_card_view, final int position) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(mContext, R.anim.item_fall_down);
        animation.setDuration(500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                content_card_view.setVisibility(View.VISIBLE);
                content_card_view.setAnimation(animation);
                lastPos = position;
            }
        }, (long) (20));

    }


    @Override
    public int getItemCount() {
        return testList.size();
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
