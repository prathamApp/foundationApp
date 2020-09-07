package com.pratham.foundation.ui.contentPlayer.chit_chat.level_2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.Message;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pratham.foundation.ui.contentPlayer.chit_chat.level_2.ConversationFragment_2.playChat;


public class MessageAdapter_2 extends RecyclerView.Adapter<MessageAdapter_2.ViewHolder> {

    private List messageList;
    Context context;
    int lastPos = -1;

    public static final int SENDER = 0;
    public static final int RECEIVER = 1;

    public MessageAdapter_2(List messages, Context context) {
        messageList = messages;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text)
        TextView mTextView;

        public ViewHolder(RelativeLayout v) {
            super(v);
            ButterKnife.bind(this,v);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_received, parent, false);
            ViewHolder vh = new ViewHolder((RelativeLayout) v);
            return vh;
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_send, parent, false);
            ViewHolder vh = new ViewHolder((RelativeLayout) v);
            return vh;
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mTextView.setText(((Message)messageList.get(holder.getAdapterPosition())).getMessage());
        holder.mTextView.setMaxWidth(650);
        if(position>lastPos) {
            ImageViewAnimatedChange(context, holder);
            lastPos=position;
        }
        holder.mTextView.setOnClickListener(v -> playChat(""+((Message)messageList.get(holder.getAdapterPosition())).getSenderAudio()));
    }

    public void ImageViewAnimatedChange(Context c, final ViewHolder holder) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
        final Animation anim_in = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                });
            }
        });
        //(holder.mTextView).setAnimation(anim_in);
        (holder.itemView).setAnimation(anim_in);
    }


    public void remove(int pos) {
        int position = pos;
        messageList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, messageList.size());

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = (Message) messageList.get(position);

        if (message.getSenderName().equals("user")) {
            return SENDER;
        } else {
            return RECEIVER;
        }

    }

}