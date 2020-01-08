package com.pratham.foundation.ui.app_home.level;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.pratham.foundation.R;

import java.util.List;

import github.hellocsl.cursorwheel.CursorWheelLayout;


public class Level_ImageAdapter extends CursorWheelLayout.CycleWheelAdapter {
    private List<Level_ImageData> mMenuItemDatas;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public Level_ImageAdapter(Context context, List<Level_ImageData> menuItemDatas) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mMenuItemDatas = menuItemDatas;
    }

    @Override
    public int getCount() {
        return mMenuItemDatas == null ? 0 : mMenuItemDatas.size();
    }

    @Override
    public View getView(View parent, int position) {
        Level_ImageData item = getItem(position);
        View root = mLayoutInflater.inflate(R.layout.fc_level_card, null, false);
        ImageView imgView = root.findViewById(R.id.iv_level_img);

        switch (position){
            case 0:
                imgView.setImageResource(R.drawable.level_1);
                break;
            case 1:
                imgView.setImageResource(R.drawable.level_2);
                break;
            case 2:
                imgView.setImageResource(R.drawable.level_3);
                break;
            case 3:
                imgView.setImageResource(R.drawable.level_4);
                break;
            case 4:
                imgView.setImageResource(R.drawable.level_5);
                break;
        }
        return root;
//        9673152266
    }

    @Override
    public Level_ImageData getItem(int position) {
        return mMenuItemDatas.get(position);
    }

}
