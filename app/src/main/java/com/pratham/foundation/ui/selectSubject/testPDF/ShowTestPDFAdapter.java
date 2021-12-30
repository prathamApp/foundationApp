package com.pratham.foundation.ui.selectSubject.testPDF;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.Diagnostic_pdf_Modal;
import com.pratham.foundation.view_holders.TestPDFViewHolder;

import java.util.List;

public class ShowTestPDFAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final int lastPos = -1;
    private final List<Diagnostic_pdf_Modal> pdf_modalList;
    ShowTestPDFContract.ItemClicked itemClicked;

    public ShowTestPDFAdapter(Context mContext, List<Diagnostic_pdf_Modal> pdf_modalList, ShowTestPDFContract.ItemClicked itemClicked) {
        this.mContext = mContext;
        this.pdf_modalList = pdf_modalList;
        this.itemClicked = itemClicked;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {

        View view;
        LayoutInflater file = LayoutInflater.from(viewGroup.getContext());
        view = file.inflate(R.layout.item_test_pdf, viewGroup, false);
        return new TestPDFViewHolder(view, itemClicked);

/*        View view;
        switch (viewtype) {
            case 0:
                Log.d("ABC_ADAPTER", "Case : 0 ");
                LayoutInflater header = LayoutInflater.from(viewGroup.getContext());
                view = header.inflate(R.layout.item_sync_card_db, viewGroup, false);
                return new SyncLogViewHolder(view, 1);
            case 1:
                LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
                view = folder.inflate(R.layout.item_sync_card, viewGroup, false);
                return new SyncLogViewHolder(view);
            case 2:
                LayoutInflater file = LayoutInflater.from(viewGroup.getContext());
                view = file.inflate(R.layout.item_sync_card_auto, viewGroup, false);
                return new SyncLogViewHolder(view, "b");
            default:
                return null;
        }*/
    }

//    final String DB_ZIP_Push = "DB_ZIP_Push";
//    final String App_Auto_Sync = "App_Auto_Sync";
//    final String App_Manual_Sync = "App_Manual_Sync";

    @Override
    public int getItemViewType(int position) {
/*        if (showSyncLogList.get(position).getExceptionMessage() != null) {
            String a = showSyncLogList.get(position).getExceptionMessage();
            Log.d("ABC_ADAPTER", "2 getItemViewType SYNCLOG : " + a);
            switch (a) {
                case DB_ZIP_Push:
                    return 0;
                case App_Manual_Sync:
                    return 1;
                case App_Auto_Sync:
                    return 2;
                default:
                    return 0;
            }
        } else {
            Log.d("ABC_ADAPTER", "getItemViewType SYNCLOG ELSE : ");
            return 0;
        }*/
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

//        switch (viewHolder.getItemViewType()) {
//            case 0:
                TestPDFViewHolder testPDFViewHolder = (TestPDFViewHolder) viewHolder;
                testPDFViewHolder.setTestPDFItem(pdf_modalList.get(position), position);
//                break;
//            case 1:
//                SyncLogViewHolder syncLogViewHolder = (SyncLogViewHolder) viewHolder;
//                syncLogViewHolder.setSyncItem(showSyncLogList.get(position), position);
//                break;
//            case 2:
//                SyncLogViewHolder syncAutoLogViewHolder = (SyncLogViewHolder) viewHolder;
//                syncAutoLogViewHolder.setAutoSyncItem(showSyncLogList.get(position), position);
//                break;
//        }

        }

        @Override
        public int getItemCount () {
            return pdf_modalList.size();
        }
    }