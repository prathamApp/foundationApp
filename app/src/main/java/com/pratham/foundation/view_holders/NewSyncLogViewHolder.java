package com.pratham.foundation.view_holders;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.SyncLog;
import com.pratham.foundation.ui.app_home.profile_new.temp_sync.SyncResultContract;
import com.pratham.foundation.utility.FC_Constants;

public class NewSyncLogViewHolder extends RecyclerView.ViewHolder {

    /**
     * Created common holder which is used for cards.
     * Folder and file type of cards have different views
     */

    TextView stat_date, tv_syncStatus, tv_uuid, tv_pushId, tv_pushError,tv_syncType;
    MaterialCardView log_card_main;
    Button btn_details, btn_sync;
    LinearLayout ll_data;
    SyncResultContract.SyncResultItemClick itemClick;


    public NewSyncLogViewHolder(View view, SyncResultContract.SyncResultItemClick itemClick) {
        super(view);
        stat_date = view.findViewById(R.id.stat_date);
        tv_syncStatus = view.findViewById(R.id.tv_syncStatus);
        tv_uuid = view.findViewById(R.id.tv_uuid);
        tv_pushId = view.findViewById(R.id.tv_pushId);
        tv_pushError = view.findViewById(R.id.tv_pushError);
        tv_syncType = view.findViewById(R.id.tv_syncType);
        log_card_main = view.findViewById(R.id.log_card_main);
        ll_data = view.findViewById(R.id.ll_data);
        btn_sync = view.findViewById(R.id.btn_sync);
        btn_details = view.findViewById(R.id.btn_details);
        this.itemClick = itemClick;
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void setSyncItem(SyncLog syncItem, int pos) {
        try {
//        SyncLog syncItem;
            stat_date.setText(ApplicationClass.getInstance().getString(R.string.date)+"\n" + syncItem.getPushDate());
            tv_syncStatus.setText(ApplicationClass.getInstance().getString(R.string.status)+" : " + syncItem.getStatus());
            if(!syncItem.getStatus().equalsIgnoreCase("COMPLETED")){
                stat_date.setBackgroundResource(R.drawable.sync_failed_bg);
                stat_date.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorRedSignBoard));
                tv_syncStatus.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorRedDark));
                tv_syncType.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorRedDark));
                btn_sync.setEnabled(true);
                btn_sync.setBackgroundResource(R.drawable.button_blue);
            }else{
                stat_date.setBackgroundResource(R.drawable.text_usage_game_grad);
                stat_date.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorSignBoard2));
                tv_syncStatus.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorSignBoard2));
                tv_syncType.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorSignBoard2));
                btn_sync.setEnabled(false);
                btn_sync.setBackgroundResource(R.drawable.convo_send_disable);
            }
            tv_uuid.setText("File Id : " + syncItem.getUuid());
            tv_pushId.setText("Push Id : " + syncItem.getPushId());
            if(syncItem.getPushType().equalsIgnoreCase(FC_Constants.APP_AUTO_SYNC))
            tv_syncType.setText("" + ApplicationClass.getInstance().getString(R.string.auto));
            else
            tv_syncType.setText("" + ApplicationClass.getInstance().getString(R.string.manual));
            if (syncItem.getError().equalsIgnoreCase("") || syncItem.getError().equalsIgnoreCase(" "))
                tv_pushError.setVisibility(View.GONE);
            else {
                tv_pushError.setVisibility(View.VISIBLE);
                tv_pushError.setText(syncItem.getError());
            }

            btn_details.setOnClickListener(view -> itemClick.checkDetails(pos, syncItem));
            btn_sync.setOnClickListener(view -> itemClick.syncData(pos, syncItem));
//            btn_sync.setVisibility(View.GONE);

            setAnimations(log_card_main);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setAnimations(final View content_card_view) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(ApplicationClass.getInstance(), R.anim.item_fall_down);
        animation.setDuration(500);
        content_card_view.setVisibility(View.VISIBLE);
        content_card_view.setAnimation(animation);
    }


}