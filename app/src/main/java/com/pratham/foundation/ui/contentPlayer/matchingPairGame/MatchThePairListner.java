package com.pratham.foundation.ui.contentPlayer.matchingPairGame;

import java.util.List;

interface MatchThePairListner {
    public void onItemDragListener(List draggedList);
    public void onSubmitClicked(boolean isCorrect);
}
