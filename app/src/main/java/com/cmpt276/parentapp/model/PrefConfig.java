/**
 * PrefConfig class - This class is a helper class to save the list of
 * children between runs.
 */
package com.cmpt276.parentapp.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PrefConfig {

    private static final String PREFS_STRING_FOR_LIST_OF_CHILDREN = "ChildList - Prefs String";
    private static final String PREFS_DEFAULT_STRING_FOR_LIST_OF_CHILDREN = "";

    public static void writeListInPref(Context context, List<Child> children){
        Gson gson = new Gson();
        String jsonString = gson.toJson(children);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_STRING_FOR_LIST_OF_CHILDREN, jsonString);
        editor.apply();
    }

    public static List<Child> readListFromPref(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = prefs.getString(
                PREFS_STRING_FOR_LIST_OF_CHILDREN,
                PREFS_DEFAULT_STRING_FOR_LIST_OF_CHILDREN
        );

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Child>>() {}.getType();
        List<Child> children = gson.fromJson(jsonString, type);
        return children;
    }
}