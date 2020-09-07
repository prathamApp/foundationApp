package com.pratham.foundation.ui.contentPlayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.fontsview.SansTextViewBold;
import com.pratham.foundation.interfaces.ShowInstruction;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

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
        String a = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI);
        Log.d("INSTRUCTIONFRAG", "Select Subj: "+a);
        FC_Utility.setAppLocal(context, a);

        int resId = context.getResources().getIdentifier(resorcetype, "string", context.getPackageName());
        if (resId != 0) {
            String instruction = context.getResources().getString(resId);
            Log.d("INSTRUCTIONFRAG", "instruction: "+instruction);
            dia_title.setText(instruction);
        } else {
            dia_title.setText("");
        }
        if(FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, "")
                .equalsIgnoreCase(FC_Constants.HINDI))
            resorcetype = "hi_" + resorcetype;
        else if(FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, "")
                .equalsIgnoreCase(FC_Constants.MARATHI))
            resorcetype = "mr_" + resorcetype;
        Log.d("INSTRUCTIONFRAG", "resorcetype: "+resorcetype);
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
