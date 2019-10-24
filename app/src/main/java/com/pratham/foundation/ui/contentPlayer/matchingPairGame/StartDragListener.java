package com.pratham.foundation.ui.contentPlayer.matchingPairGame;

import android.support.v7.widget.RecyclerView;
import com.pratham.foundation.modalclasses.MatchThePair;

import java.util.List;

public interface StartDragListener {
    void requestDrag(RecyclerView.ViewHolder viewHolder);
    void onItemDragged(List<MatchThePair> draggedList);


}
