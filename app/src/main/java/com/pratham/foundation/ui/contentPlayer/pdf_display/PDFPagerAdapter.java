package com.pratham.foundation.ui.contentPlayer.pdf_display;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.pratham.foundation.R;

import java.util.ArrayList;

public class PDFPagerAdapter extends PagerAdapter {
    private final ArrayList<Bitmap> bitmaps;
    private final LayoutInflater mLayoutInflater;

    public PDFPagerAdapter(Context context, ArrayList<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
        Context context1 = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return bitmaps.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pdf_pager_item, container, false);
        ImageView gifView = itemView.findViewById(R.id.pdf_page);
//        GifViewZoom gifView = itemView.findViewById(R.id.pdf_page);
//        InputStream gif = null;
//        try {
//            Log.d("TAG", "instantiateItem: "+bitmaps.get(position));
//            gif = new FileInputStream(bitmaps.get(position));
//            gifView.setGifResource(gif);
            gifView.setImageBitmap(bitmaps.get(position));
            container.addView(itemView);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
