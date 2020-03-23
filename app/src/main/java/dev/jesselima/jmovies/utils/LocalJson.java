package dev.jesselima.jmovies.utils;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import dev.jesselima.jmovies.models.Genre;

public class LocalJson {

    private final ArrayList<Genre> genres = new ArrayList<>();
    private String jsonStr = null;

    public ArrayList<Genre> read(Context context) {

        try {
            InputStream is = context.getAssets().open("genres.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            int isRead = is.read(buffer);
            is.close();
            jsonStr = new String(buffer, StandardCharsets.UTF_8);
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray genresJsonArray = jsonObj.getJSONArray("genres");

            for (int i = 0; i < genresJsonArray.length(); i++) {

                JSONObject genreJSONObject = genresJsonArray.getJSONObject(i);
                int id      = genreJSONObject.optInt("id");
                String name = genreJSONObject.optString("name");

                Genre data = new Genre(id, name);
                genres.add(data);
            }
            return genres;

        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
