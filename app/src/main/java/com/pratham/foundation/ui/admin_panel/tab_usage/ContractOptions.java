package com.pratham.foundation.ui.admin_panel.tab_usage;

import android.view.View;

import com.pratham.foundation.modalclasses.Modal_NavigationMenu;


public interface ContractOptions {
    void menuClicked(int position, Modal_NavigationMenu modal_navigationMenu, View view);

    void toggleMenuIcon();
}
