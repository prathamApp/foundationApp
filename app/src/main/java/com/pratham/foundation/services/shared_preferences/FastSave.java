package com.pratham.foundation.services.shared_preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Map;

/**
 * This class is used to store the data in Shared Prefernces.
 * Shared Preferences allows to save and retrieve data in the form of key,value pair.*/
public class FastSave {

    private static FastSave instance;
    private static SharedPreferences mSharedPreferences;

    private FastSave() {
    }

    public static void init(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static FastSave getInstance() {
        if (instance == null) {
            validateInitialization();
            synchronized (FastSave.class) {
                if (instance == null) {
                    instance = new FastSave();
                }
            }
        }
        return instance;
    }

    //Integer value is stored
    public void saveInt(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    //Integer value is retrived
    public int getInt(String key, int defaultValue) {
        if (isKeyExists(key)) {
            return mSharedPreferences.getInt(key, defaultValue);
        }
        return defaultValue;
    }

    //Boolean value is stored
    public void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    //Boolean value is retrived
    public boolean getBoolean(String key, boolean defaultValue) {
        if (isKeyExists(key)) {
            return mSharedPreferences.getBoolean(key, defaultValue);
        }
        return defaultValue;
    }

    //Float value is stored
    public void saveFloat(String key, float value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    //Float value is retrived
    public float getFloat(String key, float defaultValue) {
        if (isKeyExists(key)) {
            return mSharedPreferences.getFloat(key, defaultValue);
        }
        return defaultValue;
    }

    //Long value is stored
    public void saveLong(String key, long value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    //Long value is retrived
    public long getLong(String key, long defaultValue) {
        if (isKeyExists(key)) {
            return mSharedPreferences.getLong(key, defaultValue);
        }
        return defaultValue;
    }

    //String value is stored
    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    //String value is retrived
    public String getString(String key, String defaultValue) {
        if (isKeyExists(key)) {
            return mSharedPreferences.getString(key, defaultValue);
        }
        return defaultValue;
    }

    public <T> void saveObject(String key, T object) {
        String objectString = new Gson().toJson(object);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, objectString);
        editor.apply();
    }

    public <T> T getObject(String key, Class<T> classType) {
        if (isKeyExists(key)) {
            String objectString = mSharedPreferences.getString(key, null);
            if (objectString != null) {
                return new Gson().fromJson(objectString, classType);
            }
        }
        return null;
    }


//    public <T> void saveObjectsList(String key, List<T> objectList) {
//        String objectString = new Gson().toJson(objectList);
//        SharedPreferences.Editor editor = mSharedPreferences.edit();
//        editor.putString(key, objectString);
//        editor.apply();
//    }

//    public <T> List<T> getObjectsList(String key, Class<T> classType) {
//        if (isKeyExists(key)) {
//            String objectString = mSharedPreferences.getString(key, null);
//            if (objectString != null) {
//                return new Gson().fromJson(objectString, new TypeToken<List<T>>() {
//                }
//                        .where(new TypeParameter<T>() {
//                        }, classType)
//                        .getType());
//            }
//        }
//
//        return null;
//    }

    public void clearSession() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public boolean deleteValue(String key) {
        if (isKeyExists(key)) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.remove(key);
            editor.apply();
            return true;
        }

        return false;
    }


    private static void validateInitialization() {
        if (mSharedPreferences == null)
            throw new FastException("FastSave Library must be initialized inside your application class by calling FastSave.init(getApplicationContext)");
    }

    public boolean isKeyExists(String key) {
        Map<String, ?> map = mSharedPreferences.getAll();
        if (map.containsKey(key)) {
            return true;
        } else {
            Log.e("FastSave", "No element founded in sharedPrefs with the key " + key);
            return false;
        }
    }

}