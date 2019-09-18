package com.pratham.foundation.ui.selectSubject;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pratham.foundation.R;
import com.pratham.foundation.custumView.SansTextView;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.modalclasses.OptionKeyMap;
import com.pratham.foundation.ui.app_home.HomeActivity_;

import java.util.ArrayList;
import java.util.List;

public class SelectSubjectAdapter extends RecyclerView.Adapter<SelectSubjectAdapter.Myviewholder> {
    Context context;
    List<ContentTable> datalist;
    List selectedOption = new ArrayList();

    public SelectSubjectAdapter(Context context, List<ContentTable> datalist) {
        this.context = context;
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public Myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_keyword, viewGroup, false);
        return new Myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myviewholder myviewholder, int i) {
        myviewholder.textView.setText(datalist.get(i).getNodeTitle());
        myviewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, HomeActivity_.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class Myviewholder extends RecyclerView.ViewHolder {
        SansTextView textView;

        public Myviewholder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.keywordname);
        }
    }

    public List getSelectedOptionList() {
        return selectedOption;
    }
}
