package com.pratham.foundation.ui.contentPlayer.pdf_display;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.pdf.BookFlipPageTransformer;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

@EFragment(R.layout.fragment_pdf_display)
public class Fragment_PdfViewer extends Fragment implements PDFContract.pdf_View, OnGameClose {

    private static MediaPlayer page_flip_mp;
    private static ArrayList<Bitmap> bitmaps;
    @ViewById(R.id.pdf_curl_view)
    ViewPager pdf_curl_view;

    @Bean(PDF_PresenterImpl.class)
    PDFContract.pdfPresenter pdf_presenter;
    private String startTime, contentName, pdf_Path;
    private String resId;
    private int pageSelected = 1;
    private boolean isScoreAdded = false, onSdCard = false;

    @AfterViews
    public void initialize() {
        pdf_presenter.setView(Fragment_PdfViewer.this);
        page_flip_mp = MediaPlayer.create(getActivity(), R.raw.page_flip);
        startTime = FC_Utility.getCurrentDateTime();
        Bundle bundle = getArguments();
        if (bundle != null) {
            pdf_Path = bundle.getString("contentPath");
            resId = bundle.getString("resId");
            contentName = bundle.getString("contentName");
            onSdCard = bundle.getBoolean("onSdCard", false);

            if (onSdCard)
                pdf_Path = ApplicationClass.contentSDPath + gameFolderPath + "/" + pdf_Path;
            else
                pdf_Path = ApplicationClass.foundationPath + gameFolderPath + "/" + pdf_Path;
//            pdf_Path = Environment.getExternalStorageDirectory() + "/PrathamBackups/story.pdf";
            if (new File(pdf_Path).exists())
                pdf_presenter.generateImageFromPdf(pdf_Path);
        }
    }

    @UiThread
    @Override
    public void recievedBitmaps(ArrayList<Bitmap> bits) {
        try {
            bitmaps = bits;
            PDFPagerAdapter pagerAdapter = new PDFPagerAdapter(Objects.requireNonNull(getActivity()), bitmaps);
            pdf_curl_view.setAdapter(pagerAdapter);
            pdf_curl_view.setClipToPadding(false);
            BookFlipPageTransformer transformer = new BookFlipPageTransformer();
            transformer.setEnableScale(true);
            pdf_curl_view.setPageTransformer(true, transformer);
            pdf_curl_view.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {
                    page_flip_mp.start();
                }

                @Override
                public void onPageSelected(int i) {
                    pageSelected = i + 1;
                    if (pageSelected == bitmaps.size()) {
                        pdf_presenter.addScoreToDB(resId, startTime, pageSelected);
                        isScoreAdded = true;
                        if (Objects.requireNonNull(getArguments()).getBoolean("isCourse")) {
                            EventMessage message = new EventMessage();
                            message.setMessage(FC_Constants.SHOW_NEXT_BUTTON);
                            message.setDownloadId(resId);
                            EventBus.getDefault().post(message);
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void gameClose() {
        Log.d("gameClose", "gameClose: gameClose: ");
        if(!isScoreAdded) {
            pdf_presenter.addScoreToDB(resId, startTime, pageSelected);
        }
    }

    @Subscribe
    public void messageReceived(EventMessage message) {
        if (message != null) {
        } else if (message.getMessage().equalsIgnoreCase(FC_Constants.BACK_PRESSED)) {
        }
    }
}