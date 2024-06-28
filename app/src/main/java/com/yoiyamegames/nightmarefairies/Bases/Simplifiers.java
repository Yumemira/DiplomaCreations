package com.yoiyamegames.nightmarefairies.Bases;

import org.json.JSONArray;
import org.json.JSONException;

public class Simplifiers {

    public static String[] convertJSONArraytoStringArray(JSONArray jsonArray) throws JSONException {
        String[] stringArray = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++)
        {
            stringArray[i] = jsonArray.get(i).toString();
        }
        return stringArray;
    }
}
