package com.pratham.foundation.ui.contentPlayer.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.utility.MediaPlayerUtil;

public class InstructionDialog extends CustomLodingDialog {

    private final String info;
    private final Context context;
    private String infoPath;
    private MediaPlayerUtil mediaPlayerUtil;

    public InstructionDialog(@NonNull Context context, String info, String infoPath) {
        super(context);
        this.info = info;
        this.context = context;
        this.infoPath = infoPath;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fc_custom_info_dialog);
        TextView infoText = findViewById(R.id.info);
        if (info != null)
            infoText.setText(info);

        mediaPlayerUtil = new MediaPlayerUtil(context);
        if (infoPath != null)
            mediaPlayerUtil.playMedia(infoPath);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayerUtil.stopMedia();
    }

}
