package com.harmonyhub;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FetchMealNamesTask implements Runnable {

    private final AutoCompleteTextView autoCompleteTextView;

    public FetchMealNamesTask(AutoCompleteTextView autoCompleteTextView) {
        this.autoCompleteTextView = autoCompleteTextView;
    }

    @Override
    public void run() {
        List<String> mealNames = fetchMealNames();
        if (mealNames != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(autoCompleteTextView.getContext(),
                    android.R.layout.simple_dropdown_item_1line, mealNames);
            autoCompleteTextView.post(() -> autoCompleteTextView.setAdapter(adapter));
        }
    }

    private List<String> fetchMealNames() {
        List<String> mealNames = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        String apiKey = "XxaS7dYKyF0ZpBvJ0nfLmnTPdwSBg5QLw0pCbFxj";
        String url = "https://api.nal.usda.gov/fdc/v1/foods/search?query=*&api_key=" + apiKey;

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String responseBody = response.body().string();
                mealNames = parseMealNames(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mealNames;
    }

    private List<String> parseMealNames(String responseBody) {
        List<String> mealNames = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(responseBody);
            JSONArray foods = json.getJSONArray("foods");

            for (int i = 0; i < foods.length(); i++) {
                JSONObject food = foods.getJSONObject(i);
                String description = food.getString("description");
                mealNames.add(description);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mealNames;
    }
}
