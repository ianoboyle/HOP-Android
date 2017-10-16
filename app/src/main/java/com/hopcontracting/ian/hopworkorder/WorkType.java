package com.hopcontracting.ian.hopworkorder;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Ian on 9/24/2017.
 */

public class WorkType {
    private String title;
    private Integer id;

    void setTitle(String title){
        this.title = title;
    }

    String getTitle(){
        return this.title;
    }

    void setId(Integer id){
        this.id = id;
    }

    Integer getId(){
        return this.id;
    }

    public static ArrayList<WorkType> getWorkTypesFromFile(String filename, Context context){
        final ArrayList<WorkType> workTypeList = new ArrayList<>();

        try {
            // Load data
            String jsonString = loadJsonFromAsset("workTypes", context);
            JSONObject json = new JSONObject(jsonString);
            JSONArray recipes = json.getJSONArray("work_types");
            // Get Recipe objects from data
            for(int i = 0; i < recipes.length(); i++){
                WorkType workType = new WorkType();
                workType.setTitle(recipes.getJSONObject(i).getString("title"));
                workType.setId(recipes.getJSONObject(i).getInt("id"));
                workTypeList.add(workType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return workTypeList;
    }


    public static ArrayList<WorkType> getWorksTypesFromJsonArray(JSONArray jsonArray){
        final ArrayList<WorkType> workTypeList = new ArrayList<>();

        try {
            // Load data
            for(int i = 0; i < jsonArray.length(); i++){
                WorkType workType = new WorkType();
                workType.setTitle(jsonArray.getJSONObject(i).getString("name"));
                workType.setId(jsonArray.getJSONObject(i).getInt("id"));
                workTypeList.add(workType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return workTypeList;
    }

    private static String loadJsonFromAsset(String filename, Context context) {
        String json = null;

        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }
        catch (java.io.IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }
}
