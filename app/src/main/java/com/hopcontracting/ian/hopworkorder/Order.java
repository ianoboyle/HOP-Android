package com.hopcontracting.ian.hopworkorder;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {

    String title;
    String description;
    String label;
    int id;

    void setTitle(String title){
        this.title = title;
    }

    void setDescription(String description){
        this.description = description;
    }

    void setLabel(String label){
        this.label = label;
    }


    public static ArrayList<Order> getOrdersFromFile(String filename, Context context){
        final ArrayList<Order> orderList = new ArrayList<>();

        try {
            // Load data
            String jsonString = loadJsonFromAsset("orders", context);
            JSONObject json = new JSONObject(jsonString);
            JSONArray recipes = json.getJSONArray("works");

            // Get Recipe objects from data
            for(int i = 0; i < recipes.length(); i++){
                Order order = new Order();

                order.title = recipes.getJSONObject(i).getString("title");
                order.description = recipes.getJSONObject(i).getString("description");

                orderList.add(order);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return orderList;
    }


    public static ArrayList<Order> getOrdersFromJsonArray(JSONArray jsonArray){
        final ArrayList<Order> orderList = new ArrayList<>();

        try {
            // Load data

            // Get Recipe objects from data
            for(int i = 0; i < jsonArray.length(); i++){
                Order order = new Order();

                order.title = jsonArray.getJSONObject(i).getString("address");
                order.description = jsonArray.getJSONObject(i).getString("date");
                order.id = jsonArray.getJSONObject(i).getInt("id");

                orderList.add(order);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return orderList;
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