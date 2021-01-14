package com.pratham.foundation.view_holders;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.ui.app_home.FragmentItemClicked;
import com.pratham.foundation.ui.app_home.learning_fragment.LearningInnerDataAdapter;
import com.pratham.foundation.utility.FC_Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentOuterViewHolder extends RecyclerView.ViewHolder {

    @Nullable
    TextView itemTitle;
    @Nullable
    RecyclerView recycler_view_list;
    @Nullable
    Button btnMore;
    @Nullable
    Button actionBtn;

    private FragmentItemClicked fragmentItemClicked;
    private List<ContentTable> sublistList;
    private int childCounter = 0;

    public FragmentOuterViewHolder(View itemView, final FragmentItemClicked fragmentItemClicked) {
        super(itemView);

        this.itemTitle = itemView.findViewById(R.id.itemTitle);
        this.recycler_view_list = itemView.findViewById(R.id.recycler_view_list);
        this.btnMore = itemView.findViewById(R.id.btnMore);
        this.actionBtn = itemView.findViewById(R.id.ib_action_btn);
        this.fragmentItemClicked = fragmentItemClicked;
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void setOuterItem(ContentTable contentTable, int position, String sec) {
        sublistList = new ArrayList<>();
        final String sectionName = contentTable.getNodeTitle();
        if (contentTable.getNodeType() != null) {
            if (contentTable.getNodeType().equalsIgnoreCase("PreResource")
                    && (contentTable.isDownloaded.equalsIgnoreCase("false"))) {
                Objects.requireNonNull(btnMore).setVisibility(View.GONE);
                Objects.requireNonNull(actionBtn).setVisibility(View.VISIBLE);
                actionBtn.setOnClickListener(v -> fragmentItemClicked.onContentDownloadClicked(contentTable,
                        position, 0, "" + FC_Constants.FULL_DOWNLOAD));
            } else if (contentTable.getNodeType().equalsIgnoreCase("PreResource")
                    && contentTable.isDownloaded.equalsIgnoreCase("true")
                    && contentTable.isNodeUpdate()) {
                Objects.requireNonNull(btnMore).setVisibility(View.GONE);
                Objects.requireNonNull(actionBtn).setVisibility(View.VISIBLE);
                Objects.requireNonNull(actionBtn).setText("UPDATE");
                Objects.requireNonNull(actionBtn).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_update2, 0);
                actionBtn.setOnClickListener(v -> fragmentItemClicked.onContentDownloadClicked(contentTable,
                        position, 0, "" + FC_Constants.FULL_DOWNLOAD));
            } else {
                int size = Objects.requireNonNull(contentTable.getNodelist()).size() - 1;
                int sizeRes = contentTable.getNodelist().size() - 2;
                Objects.requireNonNull(btnMore).setText("SEE ALL " + sizeRes);
                btnMore.setVisibility(View.GONE);
                if (size > 6) {
                    btnMore.setVisibility(View.VISIBLE);
                    btnMore.setOnClickListener(v -> fragmentItemClicked.seeMore(contentTable.getNodeId(),
                            contentTable.getNodeTitle()));
                }
                Objects.requireNonNull(actionBtn).setVisibility(View.GONE);
            }
        } else {
            int size = Objects.requireNonNull(contentTable.getNodelist()).size() - 1;
            int sizeRes = contentTable.getNodelist().size() - 2;
            Objects.requireNonNull(btnMore).setText("SEE ALL " + sizeRes);
            btnMore.setVisibility(View.GONE);
            if (size > 6) {
                btnMore.setVisibility(View.VISIBLE);
                btnMore.setOnClickListener(v -> fragmentItemClicked.seeMore(contentTable.getNodeId(),
                        contentTable.getNodeTitle()));
            }
            Objects.requireNonNull(actionBtn).setVisibility(View.GONE);
        }
        sublistList = getList(Objects.requireNonNull(contentTable.getNodelist()));
        Objects.requireNonNull(itemTitle).setText(sectionName);
        itemTitle.setSelected(true);
/*        if (sec.equalsIgnoreCase(sec_Fun)) {
            try {
                FunInnerDataAdapter learningInnerDataAdapter = new FunInnerDataAdapter(
                        ApplicationClass.getInstance(), sublistList, fragmentItemClicked, position, sectionName);
                Objects.requireNonNull(recycler_view_list).setLayoutManager(new LinearLayoutManager(
                        ApplicationClass.getInstance(), LinearLayoutManager.HORIZONTAL, false));
                recycler_view_list.setAdapter(learningInnerDataAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else*/
//        if (sec.equalsIgnoreCase(sec_Learning)) {
            try {
                LearningInnerDataAdapter learningInnerDataAdapter = new LearningInnerDataAdapter(
                        ApplicationClass.getInstance(), sublistList, fragmentItemClicked, position, sectionName);
                Objects.requireNonNull(recycler_view_list).setLayoutManager(new LinearLayoutManager(
                        ApplicationClass.getInstance(), LinearLayoutManager.HORIZONTAL, false));
                recycler_view_list.setAdapter(learningInnerDataAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
//        } else if (sec.equalsIgnoreCase(sec_Practice)) {
//            try {
//                PracticeInnerDataAdapter learningInnerDataAdapter = new PracticeInnerDataAdapter(
//                        ApplicationClass.getInstance(), sublistList, fragmentItemClicked, position, sectionName);
//                Objects.requireNonNull(recycler_view_list).setLayoutManager(new LinearLayoutManager(
//                        ApplicationClass.getInstance(), LinearLayoutManager.HORIZONTAL, false));
//                recycler_view_list.setAdapter(learningInnerDataAdapter);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        childCounter += 1;
    }

    private List<ContentTable> getList(List<ContentTable> nodelist) {
        List<ContentTable> tempList = new ArrayList<>();
        for (int i = 0; i < nodelist.size() && i < 6; i++)
            tempList.add(nodelist.get(i));
        return tempList;
    }
}