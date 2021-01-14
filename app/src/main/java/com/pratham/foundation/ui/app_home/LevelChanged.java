package com.pratham.foundation.ui.app_home;

import com.pratham.foundation.database.domain.ContentTable;

import java.util.List;

public interface LevelChanged {
    void setActualLevel(List<ContentTable> level_List, String titleContent, int level);
}
