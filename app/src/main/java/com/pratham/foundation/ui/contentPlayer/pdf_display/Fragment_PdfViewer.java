package com.pratham.foundation.ui.contentPlayer.pdf_display;

import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.pdf.BookFlipPageTransformer;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

@EActivity(R.layout.fragment_pdf_display)
public class Fragment_PdfViewer extends BaseActivity implements PDFContract.pdf_View {

    @Bean(PDF_PresenterImpl.class)
    PDFContract.pdfPresenter pdf_presenter;

    @ViewById(R.id.pdf_curl_view)
    ViewPager pdf_curl_view;
    @ViewById(R.id.close_video)
    ImageButton close_video;

    private static MediaPlayer page_flip_mp;
    private static ArrayList<Bitmap> bitmaps;
    private String startTime, contentName, pdf_Path;
    private String resId;
    private int pageSelected = 1;
    private boolean isScoreAdded = false, onSdCard = false;

    @AfterViews
    public void initialize() {
        pdf_presenter.setView(Fragment_PdfViewer.this);
        page_flip_mp = MediaPlayer.create(this, R.raw.page_flip);
        startTime = FC_Utility.getCurrentDateTime();
        Bundle bundle = new Bundle(); /*getArguments();*/
        Intent intent = getIntent();
//        if (bundle != null) {
//            pdf_Path = bundle.getString("contentPath");
//            resId = bundle.getString("resId");
//            contentName = bundle.getString("contentName");
//            onSdCard = bundle.getBoolean("onSdCard", false);
        pdf_Path = intent.getStringExtra("contentPath");
        resId = intent.getStringExtra("resId");
        contentName = intent.getStringExtra("contentName");
        onSdCard = intent.getBooleanExtra("onSdCard", false);

        if (onSdCard)
            pdf_Path = ApplicationClass.contentSDPath + gameFolderPath + "/" + pdf_Path;
        else
            pdf_Path = ApplicationClass.foundationPath + gameFolderPath + "/" + pdf_Path;
//            pdf_Path = FC_Utility.getStoragePath() + "/PrathamBackups/story.pdf";
        if (new File(pdf_Path).exists())
            pdf_presenter.generateImageFromPdf(pdf_Path);
        else
            Toast.makeText(this, "PDF not found", Toast.LENGTH_SHORT).show();
//        }
    }

    @UiThread
    @Override
    public void recievedBitmaps(ArrayList<Bitmap> bits) {
        try {
//            File directory = new File(FC_Utility.getStoragePath().toString() + "/.FCAInternal/TestJsons");
//            File[] fileListArray = directory.listFiles();
//
//            for (File file : fileListArray) {
//                if (file.exists() && file.isDirectory()) {
//                    try {
//                        FileUtils.deleteDirectory(file);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    if (!file.getName().contains(".nomedia"))
//                        file.delete();
//                }
//            }
//
            bitmaps = bits;
//            ArrayList<String> imagesList = new ArrayList<>();
//            for (int i = 0; i < bitmaps.size(); i++) {
//                File pictureFile = FC_Utility.getOutputMediaFile(this, i);
//                imagesList.add(pictureFile.toString());
////                if (pictureFile == null) {
////                    Log.d(TAG, "Error creating media file, check storage permissions: ");// e.getMessage());
////                    return;
////                }
//                FileOutputStream fos = new FileOutputStream(pictureFile);
//                bits.get(i).compress(Bitmap.CompressFormat.PNG, 100, fos);
//                fos.close();
//            }
//            for (int a = 0; a < imagesList.size(); a++)
//                Log.d("TAG", "recievedBitmaps: " + imagesList.get(a));
            PDFPagerAdapter pagerAdapter = new PDFPagerAdapter(Objects.requireNonNull(this), bitmaps);
            pdf_curl_view.setAdapter(pagerAdapter);
            pdf_curl_view.setClipToPadding(false);
            BookFlipPageTransformer transformer = new BookFlipPageTransformer();
            transformer.setEnableScale(false);
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
/*                        if (Objects.requireNonNull(this).getBoolean("isCourse")) {
                            EventMessage message = new EventMessage();
                            message.setMessage(FC_Constants.SHOW_NEXT_BUTTON);
                            message.setDownloadId(resId);
                            EventBus.getDefault().post(message);
                        }*/
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }*/

    @Click(R.id.close_video)
    public void closeVide() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        try {
            pdf_presenter.addScoreToDB(resId, startTime, pageSelected);
            BackupDatabase.backup(this);
            super.onBackPressed();
        } catch (Exception e) {
            super.onBackPressed();
            e.printStackTrace();
        }
    }

    /*    @Subscribe
    public void messageReceived(EventMessage message) {
        if (message != null) {
        } else if (message.getMessage().equalsIgnoreCase(FC_Constants.BACK_PRESSED)) {
        }
    }*/
}