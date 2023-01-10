package com.pratham.foundation.ui.contentPlayer.pdf_display;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


@EBean
public class PDF_PresenterImpl implements PDFContract.pdfPresenter {
    private final Context context;
    private PDFContract.pdf_View pdf_view;
    private ArrayList<Bitmap> bitmaps;

    public PDF_PresenterImpl(Context context) {
        this.context = context;
    }

    @Override
    public void setView(Fragment_PdfViewer activity_pdfViewer) {
        pdf_view = activity_pdfViewer;
    }

    @Background
    @Override
    public void generateImageFromPdf(String resPath) {
        if (bitmaps == null)
            bitmaps = new ArrayList<>();
        bitmaps.clear();
        try {
            PdfRenderer renderer = new PdfRenderer(getSeekableFileDescriptor(resPath));
            // let us just render all pages
            final int pageCount = renderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = renderer.openPage(i);
                // say we render for showing on the screen
                Bitmap mBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                // do stuff with the bitmap
                bitmaps.add(mBitmap);
                // close the page
                page.close();
            }
            renderer.close();
            pdf_view.recievedBitmaps(bitmaps);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ParcelFileDescriptor getSeekableFileDescriptor(String resPath) {
        ParcelFileDescriptor fd = null;
        try {
            fd = context.getContentResolver().openFileDescriptor(Uri.fromFile(new File(resPath)), "r");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fd;
    }

    @Background
    @Override
    public void addScoreToDB(String resId, String startTime, int pageSelected) {
        try {
            String endTime = FC_Utility.getCurrentDateTime();
            int total = (bitmaps.size() > 0) ? bitmaps.size() : 0;
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setStudentID("" + ((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ?"NA"
                    :FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "")));
            score.setDeviceID(FC_Utility.getDeviceID());
            score.setResourceID(resId);
            score.setQuestionId(0);
            score.setScoredMarks(pageSelected);
            score.setTotalMarks((bitmaps.size() > 0) ? bitmaps.size() : 0);
            score.setStartDateTime(startTime);
            score.setEndDateTime(endTime);
            score.setLevel(0);
            score.setLabel("PDF");
            score.setGroupId(""+((FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, "NA").equals(null)
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, "NA").equals(""))
                    ? "NA" : FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, "NA")));
            score.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getScoreDao().insert(score);
            float perc = 0f;
            perc = (pageSelected/(float) total)*100;
            addContentProgress(perc,"resourceProgress",resId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addContentProgress(float perc, String label,String resId) {
        try {
            ContentProgress contentProgress = new ContentProgress();
            contentProgress.setProgressPercentage("" + perc);
            contentProgress.setResourceId("" + resId);
            contentProgress.setSessionId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            contentProgress.setStudentId("" + ((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ?"NA"
                    :FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "")));
            contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
            contentProgress.setLabel("" + label);
            contentProgress.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getContentProgressDao().insert(contentProgress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
