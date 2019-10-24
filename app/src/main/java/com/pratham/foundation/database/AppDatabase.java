package com.pratham.foundation.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.database.dao.AssessmentDao;
import com.pratham.foundation.database.dao.AttendanceDao;
import com.pratham.foundation.database.dao.ContentProgressDao;
import com.pratham.foundation.database.dao.ContentTableDao;
import com.pratham.foundation.database.dao.CrlDao;
import com.pratham.foundation.database.dao.EnglishWordDao;
import com.pratham.foundation.database.dao.GroupDao;
import com.pratham.foundation.database.dao.KeyWordDao;
import com.pratham.foundation.database.dao.LogDao;
import com.pratham.foundation.database.dao.MatchThePairDao;
import com.pratham.foundation.database.dao.ScoreDao;
import com.pratham.foundation.database.dao.SessionDao;
import com.pratham.foundation.database.dao.StatusDao;
import com.pratham.foundation.database.dao.StudentDao;
import com.pratham.foundation.database.dao.SupervisorDataDao;
import com.pratham.foundation.database.dao.VillageDao;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.Attendance;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.database.domain.Crl;
import com.pratham.foundation.database.domain.Groups;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Modal_Log;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.database.domain.Session;
import com.pratham.foundation.database.domain.Status;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.database.domain.SupervisorData;
import com.pratham.foundation.database.domain.Village;
import com.pratham.foundation.database.domain.WordEnglish;
import com.pratham.foundation.modalclasses.MatchThePair;


@Database(entities = {Crl.class, Student.class, Score.class, Session.class,
        Attendance.class, Status.class, Village.class, Groups.class,
        SupervisorData.class, Assessment.class, Modal_Log.class, ContentTable.class,
        ContentProgress.class, KeyWords.class, WordEnglish.class, MatchThePair.class }, version = 1, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    public static AppDatabase appDatabase;

    public static final String DB_NAME = "foundation_db";

    public abstract CrlDao getCrlDao();

    public abstract StudentDao getStudentDao();

    public abstract ScoreDao getScoreDao();

    public abstract AssessmentDao getAssessmentDao();

    public abstract SessionDao getSessionDao();

    public abstract AttendanceDao getAttendanceDao();

    public abstract VillageDao getVillageDao();

    public abstract GroupDao getGroupsDao();

    public abstract SupervisorDataDao getSupervisorDataDao();

    public abstract LogDao getLogsDao();

    public abstract ContentTableDao getContentTableDao();

    public abstract StatusDao getStatusDao();

    public abstract ContentProgressDao getContentProgressDao();

    public abstract KeyWordDao getKeyWordDao();

    public abstract EnglishWordDao getEnglishWordDao();

    public abstract MatchThePairDao getMatchThePairDao();


    public static AppDatabase getDatabaseInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(ApplicationClass.getInstance(), AppDatabase.class, DB_NAME)
                    .allowMainThreadQueries()
                    .build();
            return appDatabase;
        } else
            return appDatabase;
    }

}
