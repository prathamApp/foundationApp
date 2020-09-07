package com.pratham.foundation.database.domain;

import androidx.room.TypeConverter;

public class JSONArrayToString {
	
    @TypeConverter
    public static String StringArrayToString(String[] stringArray) {
        String str="";
        if(stringArray!=null) {
            for (int i = 0; i < stringArray.length; i++)
                str += stringArray[i] + ",";
        }
        return stringArray == null ? null : str;
    }

    @TypeConverter
    public static String[] stringToStringArray(String strings) {
        String[] jsonArray = null;
        if (strings != null) {
           try {
               jsonArray=strings.split(",");
            }catch (Exception e){

            }
        }
        return jsonArray;
    }
}
