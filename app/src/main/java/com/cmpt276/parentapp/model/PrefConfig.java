package com.cmpt276.parentapp.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * PrefConfig class - This class is a helper class to save the list of
 * children between runs.
 */
public class PrefConfig<T> {

    private static final String PREFS_STRING_FOR_LIST_OF_CHILDREN = "SP - ChildList - Shared Preferences String";
    private static final String PREFS_STRING_FOR_FLIPS_HISTORY = "SP - FlipHistory - Shared Preferences String";
    private static final String PREFS_DEFAULT_STRING_FOR_LIST_OF_CHILDREN = "";
    private static final String PREFS_DEFAULT_STRING_FOR_FLIPS_HISTORY = "";

    public static void writeChildListInPref(Context context, List<Child> children) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(children);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_STRING_FOR_LIST_OF_CHILDREN, jsonString);
        editor.apply();
    }

    public static List<Child> readChildListFromPref(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = prefs.getString(
                PREFS_STRING_FOR_LIST_OF_CHILDREN,
                PREFS_DEFAULT_STRING_FOR_LIST_OF_CHILDREN
        );

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Child>>() {
        }.getType();
        return gson.fromJson(jsonString, type);
    }

    public static void writeFlipHistoryInPref(Context context, List<CoinFlip> flipsHistory) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(flipsHistory);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_STRING_FOR_FLIPS_HISTORY, jsonString);
        editor.apply();
    }

    public static List<CoinFlip> readFlipHistoryFromPref(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = prefs.getString(
                PREFS_STRING_FOR_FLIPS_HISTORY,
                PREFS_DEFAULT_STRING_FOR_FLIPS_HISTORY
        );

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<CoinFlip>>() {
        }.getType();
        return gson.fromJson(jsonString, type);
    }
}
