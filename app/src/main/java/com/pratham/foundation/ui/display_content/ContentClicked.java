package com.pratham.foundation.ui.display_content;


import com.pratham.foundation.database.domain.ContentTable;

/**
 * Created by Ameya on 23-Nov-17.
 */

public interface ContentClicked {

    void onContentClicked(int position, String nId);

    void onContentOpenClicked(int position, String nId);

    void onContentDownloadClicked(int position, String nId);

    void onContentDeleteClicked(int position, ContentTable contentList);
}
