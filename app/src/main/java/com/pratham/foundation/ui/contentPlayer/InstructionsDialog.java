package com.pratham.foundation.ui.contentPlayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.fontsview.SansTextViewBold;
import com.pratham.foundation.interfaces.ShowInstruction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InstructionsDialog extends CustomLodingDialog {

    @BindView(R.id.dia_title)
    SansTextViewBold dia_title;
    private ShowInstruction showInstruction;
    private String resorcetype = "";
    private Context context;
    private MediaPlayer mediaPlayer;

    public InstructionsDialog(ShowInstruction showInstruction, @NonNull Context context, String resorcetype) {
        super(context, R.style.FullScreenCustomDialogStyle);
        this.resorcetype = resorcetype;
        this.context = context;
        this.showInstruction = showInstruction;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions_dialog);
        ButterKnife.bind(this);
        getWindow().setDimAmount(.7f);
        int resId = context.getResources().getIdentifier(resorcetype, "string", context.getPackageName());
        if (resId != 0) {
            String instruction = context.getResources().getString(resId);
            dia_title.setText(instruction);
        } else {
            dia_title.setText("");
        }
        resorcetype = "hi_" + resorcetype;
        int rawID = context.getResources().getIdentifier(resorcetype.toLowerCase(), "raw", context.getPackageName());
        if (rawID != 0) {
            mediaPlayer = MediaPlayer.create(context, rawID);
            mediaPlayer.start();
        }else{
            resorcetype = "hi_" + resorcetype;
            rawID = context.getResources().getIdentifier(resorcetype.toLowerCase(), "raw", context.getPackageName());
            if (rawID != 0) {
                mediaPlayer = MediaPlayer.create(context, rawID);
                mediaPlayer.start();
            }
        }
    }

    @OnClick(R.id.dia_btn_green)
    public void playGame() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        showInstruction.play(context);
    }


    @Override
    public void onBackPressed() {
        /*super.onBackPressed();*/
    }
}
