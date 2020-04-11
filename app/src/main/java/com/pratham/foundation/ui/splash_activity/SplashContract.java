package com.pratham.foundation.ui.splash_activity;


import com.pratham.foundation.database.AppDatabase;

import java.io.File;

/**
 * Created by Ameya on 23-Nov-17.
 */
 
 

public interface SplashContract {

    interface SplashView{

        void startApp();

        void showUpdateDialog();

        void showButton();

        void showProgressDialog();

        void showBottomFragment();

        void dismissProgressDialog();

        void gotoNextActivity();
    }

    interface SplashPresenter {

        void checkVersion();

        void createDatabase();

        void pushData();

        void doInitialEntries(AppDatabase appDatabase);

        void copyZipAndPopulateMenu();

        void versionObtained(String latestVersion);

        void copyDataBase();

        boolean getSdCardPath();

        void populateSDCardMenu();

        void setView(SplashContract.SplashView splashView);

        void insertNewData();

        void updateVersionApp();

        void requestLocation();

        void copyZipAndPopulateMenu_New();

        void createNoMediaForFCInternal(File myFile);
    }

}
