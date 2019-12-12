package com.pratham.foundation.ui.student_profile.discription_adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.student_profile.Profile_Model;
import com.pratham.foundation.utility.FC_Constants;

import java.util.ArrayList;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.GROUP_LOGIN;


public class DiscriptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Profile_Model> dataLast;
    Context context;
    List maxScore = new ArrayList();
    final int summery = 1;
    final int progress = 2;
    final int certificates = 3;
    SummeryViewHolder summeryViewHolder;
    ProgressViewHolder progressViewHolder;
    CertificateViewHolder certificateViewHolder;
    //List maxScore;

    public DiscriptionAdapter(List<Profile_Model> dataLast, Context context) {
        this.dataLast = dataLast;
        this.context = context;
        //   maxScore = new ArrayList();
    }

    @Override
    public int getItemViewType(int position) {
        Profile_Model profile_model = dataLast.get(position);
        switch (profile_model.getType()) {
            case "Summary":
                return summery;
            case "Progress":
                return progress;
            case "Certificates":
                return certificates;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case summery:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.summery, viewGroup, false);
                return new SummeryViewHolder(view);
            case progress:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress, viewGroup, false);
                return new ProgressViewHolder(view);
            case certificates:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.certifiacates, viewGroup, false);
                return new CertificateViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Profile_Model profile_model = dataLast.get(viewHolder.getAdapterPosition());
        switch (profile_model.getType()) {
            case "Summary":
                summeryViewHolder = (SummeryViewHolder) viewHolder;
                summeryViewHolder.title.setText(profile_model.getTittle());
                summeryViewHolder.count.setText(String.valueOf(profile_model.getCount()));
                break;
            case "Progress":
                progressViewHolder = (ProgressViewHolder) viewHolder;
                progressViewHolder.title.setText(profile_model.getTittle());
                progressViewHolder.progress.setCurProgress(profile_model.getProgress());
                progressViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AsyncTask<Object, Void, Object>() {
                            @Override
                            protected Object doInBackground(Object... objects) {
                                List<ContentTable> childs = AppDatabase.getDatabaseInstance(context).getContentTableDao().getChildsOfParent(dataLast.get(viewHolder.getAdapterPosition()).getNode());
                                if (childs.size() > 0) {
                                    dataLast.clear();
                                    for (int i = 0; i < childs.size(); i++) {
                                        //getProgress
                                        if (!childs.get(i).getNodeId().equalsIgnoreCase("4033")) {
                                            //findMaxScore(childs.get(i).getNodeId());
                                            dataLast.add(new Profile_Model(childs.get(i).getNodeTitle(), "", context.getString(R.string.Progress), childs.get(i).getNodeId(), getWholePercentage(maxScore)));
                                        }
                                    }
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object o) {
                                super.onPostExecute(o);
                                DiscriptionAdapter.this.notifyDataSetChanged();
                                //discriptionRecycler.setAdapter(new DiscriptionAdapter(adapterList, context));
                            }
                        }.execute();
                    }
                });
                break;
            case "Certificates":
                certificateViewHolder = (CertificateViewHolder) viewHolder;
                new AsyncTask<Object, Void, Object>() {
                    List<Assessment> assessmentList;

                    @Override
                    protected Object doInBackground(Object... objects) {
                        if(GROUP_LOGIN)
                            assessmentList = AppDatabase.getDatabaseInstance(context).getAssessmentDao().getCertificatesGroups(FC_Constants.currentGroup, FC_Constants.CERTIFICATE_LBL);
                        else
                            assessmentList = AppDatabase.getDatabaseInstance(context).getAssessmentDao().getCertificates(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), FC_Constants.CERTIFICATE_LBL);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        CustomAdapterForCertificate adapter = new CustomAdapterForCertificate(context, assessmentList);
                        // certificate_progress.setText("" + assessmentList.size());
                        certificateViewHolder.lv_Leaderboard.setAdapter(adapter);
                    }
                }.execute();

                //getCertificates();
                break;
        }
    }

    public void findMaxScore(String nodeId) {
        List<ContentTable> childList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getChildsOfParent(nodeId);
        for (int childCnt = 0; childList.size() > childCnt; childCnt++) {
            if (childList.get(childCnt).getNodeType().equals("Resource")) {
                double maxScoreTemp = 0.0;
                List<ContentProgress> score = AppDatabase.getDatabaseInstance(context).getContentProgressDao().getProgressByStudIDAndResID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), childList.get(childCnt).getResourceId(), "resourceProgress");
                for (int cnt = 0; cnt < score.size(); cnt++) {
                    String d = score.get(cnt).getProgressPercentage();
                    double scoreTemp = Double.parseDouble(d);
                    if (maxScoreTemp < scoreTemp) {
                        maxScoreTemp = scoreTemp;
                    }
                }
                maxScore.add(maxScoreTemp);
            } else {
                findMaxScore(childList.get(childCnt).getNodeId());
            }
        }
    }

    private int getWholePercentage(List maxScore) {
        double totalScore = 0;
        for (int j = 0; maxScore.size() > j; j++) {
            totalScore = totalScore + Double.parseDouble(maxScore.get(j).toString());
        }
        if (maxScore.size() > 0) {
            int percent = (int) (totalScore / maxScore.size());
            return percent;
        } else {
            return 0;
        }
    }
  /*  public void findMaxScore(String nodeId, List maxScore) {
        List<ContentTable> childList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getChildsOfParent(nodeId);
        for (int childCnt = 0; childList.size() > childCnt; childCnt++) {
            if (childList.get(childCnt).getNodeType().equals("Resource")) {
                double maxScoreTemp = 0.0;
                // List<Score> score = AppDatabase.getDatabaseInstance(context).getScoreDao().getScoreByStudIDAndResID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), childList.get(childCnt).getResourceId(), "resourceProgress");
                List<ContentProgress> contentProgressList = AppDatabase.getDatabaseInstance(context).getContentProgressDao().getContentNodeProgress(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), childList.get(childCnt).getResourceId(), "resourceProgress");
                //List<Score> score = AppDatabase.getDatabaseInstance(context).getScoreDao().getScoreByStudIDAndResID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), childList.get(childCnt).getResourceId(), "resourceProgress");

                for (int cnt = 0; cnt < contentProgressList.size(); cnt++) {
                    String d = contentProgressList.get(cnt).getProgressPercentage();
                    double scoreTemp = Double.parseDouble(d);
                    if (maxScoreTemp < scoreTemp) {
                        maxScoreTemp = scoreTemp;
                    }
                }
                maxScore.add(maxScoreTemp);
            } else {
                findMaxScore(childList.get(childCnt).getNodeId(), maxScore);
            }
        }
    }

    private int getWholePercentage(List maxScore) {
        double totalScore = 0;
        for (int j = 0; maxScore.size() > j; j++) {
            totalScore = totalScore + Double.parseDouble(maxScore.get(j).toString());
        }
        if (maxScore.size() > 0) {
            int percent = (int) (totalScore / maxScore.size());
            return percent;
        } else {
            return 0;
        }
    }*/

  /*  private void getCertificates() {
        // Display Word Progress

        // List<Assessment> assessmentList = AppDatabase.getDatabaseInstance(ProfileActivity.this).getAssessmentDao().getCertificates(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), FC_Constants.CERTIFICATE_LBL);
        if (assessmentList != null) {
            //certificate_progress.setText("" + assessmentList.size());
        }
//        tv_Word.setText(Html.fromHtml("<sup>" + Math.round(wordsProgress) + "</sup>&frasl;<sub>" + totalWords + "</sub>"));
    }*/

    @Override
    public int getItemCount() {
        return dataLast.size();
    }


    class CustomAdapterForCertificate extends ArrayAdapter<Assessment> {
        private Context mContext;
        private List<Assessment> assessmentLists;
        public TextView tv_assessment;
        public TextView tv_timestamp;
        public RelativeLayout certi_root;

        CustomAdapterForCertificate(Context c, List<Assessment> assessmentLists) {
            super(c, R.layout.layout_certificate_row, assessmentLists);
            this.mContext = c;
            this.assessmentLists = assessmentLists;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = vi.inflate(R.layout.layout_certificate_row, parent, false);
            tv_assessment = row.findViewById(R.id.tv_assessment);
            tv_timestamp = row.findViewById(R.id.tv_timestamp);
            certi_root = row.findViewById(R.id.certi_root);

            String sName, sId = assessmentLists.get(position).getStartDateTimea().split("_")[0];
            if (GROUP_LOGIN) {
                sName = AppDatabase.getDatabaseInstance(context).getStudentDao().getGroupName(sId);
            } else
                sName = AppDatabase.getDatabaseInstance(context).getStudentDao().getFullName(sId);

            String cLevel = "" + assessmentLists.get(position).getLevela();
            String cTitle = "";

            if (cLevel.equalsIgnoreCase("0")) {
                cTitle = "Beginner";
                tv_assessment.setText("Certificate Level 1");
            } else if (cLevel.equalsIgnoreCase("1")) {
                cTitle = "Sub junior";
                tv_assessment.setText("Certificate Level 2");
            } else if (cLevel.equalsIgnoreCase("2")) {
                cTitle = "Junior";
                tv_assessment.setText("Certificate Level 3");
            } else if (cLevel.equalsIgnoreCase("3")) {
                cTitle = "Sub Senior";
                tv_assessment.setText("Certificate Level 4");
            }

            tv_timestamp.setText(assessmentLists.get(position).getEndDateTime());
            int finalPosition = position;
            certi_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoCertificate(assessmentLists.get(finalPosition));
                }
            });

            position++;
            return row;
        }
    }

    private void gotoCertificate(Assessment assessment) {
        //todo remove#
       /* Intent intent = new Intent(context, CertificateActivity.class);
        intent.putExtra("nodeId", "na");
        intent.putExtra("CertiTitle", "" + assessment.getLevela());
        intent.putExtra("display", "display");
        intent.putExtra("assessment", assessment);
        context.startActivity(intent);*/
    }
}
