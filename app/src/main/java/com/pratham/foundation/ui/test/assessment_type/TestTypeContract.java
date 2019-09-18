package com.pratham.foundation.ui.test.assessment_type;

import android.graphics.Bitmap;


import com.pratham.foundation.database.domain.Student;

import java.util.List;

/**
 * Created by Ameya on 23-Nov-17.
 */

public interface TestTypeContract {

    interface TestTypeView{

        void setPhotoSaved(boolean b);

        void notifyTestAdapter(List<Student> studentList);

        void showAttendence();

        void StartTestActivity(String nodeId, String certiTitle);
    }

    interface TestTypePresenter {
        void AddSupervisor(String supervisorID, String supervisorName,
                           String supervisorPhoto);

        void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName);

        void getStudents();
    }

}
