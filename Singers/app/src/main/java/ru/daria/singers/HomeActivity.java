package ru.daria.singers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity для отображения окна приветствия, загрузки списка исполнителей
 */
public class HomeActivity extends Activity {
    List<Singer> singers = new ArrayList<Singer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Extractor ex = new Extractor(new Extractor.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    JSONArray dataJson = new JSONArray(output);
                    singers = getSingersFromJson(dataJson);
                    Intent i = new Intent(HomeActivity.this, MainActivity.class);
                    i.putExtra("SINGERS", (Serializable) singers);
                    startActivity(i);
                    finish();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    Button b = (Button) findViewById(R.id.tryAgainButton);
                    b.setVisibility(View.VISIBLE);
                    TextView v = (TextView) findViewById(R.id.splashText);
                    v.setText("Невозможно загрузить список исполнителей.");
                }
            }
        });
        ex.execute();
    }

    public void buttonOnClick(View view) {
        Button b = (Button) findViewById(R.id.tryAgainButton);
        b.setVisibility(View.INVISIBLE);
        Intent i = new Intent(HomeActivity.this, HomeActivity.class);
        startActivity(i);
    }

    /**
     * Функция создает список исполнителей по входному json
     *
     * @param jSingers JSONArray Массив исполнителей в формате json
     */
    public List<Singer> getSingersFromJson(JSONArray jSingers) {
        List<Singer> singers = new ArrayList<>();
        for (int i = 0; i < jSingers.length(); i++) {
            try {
                JSONObject jSinger = jSingers.getJSONObject(i);
                Long id = null;
                if (jSinger.has("id")) id = jSinger.getLong("id");
                String name = "";
                if (jSinger.has("name")) name = jSinger.getString("name");
                List<String> genres = new ArrayList<>();
                if (jSinger.has("genres")) {
                    JSONArray jGenres = jSinger.getJSONArray("genres");
                    for (int j = 0; j < jGenres.length(); j++) {
                        genres.add(jGenres.getString(j));
                    }
                }
                int tracks = 0;
                if (jSinger.has("tracks")) tracks = jSinger.getInt("tracks");
                int albums = 0;
                if (jSinger.has("albums")) albums = jSinger.getInt("albums");
                String link = "";
                if (jSinger.has("link")) link = jSinger.getString("link");
                String description = "";
                if (jSinger.has("description")) {
                    description = jSinger.getString("description");
                    //описание будет всегда с заглавной буквы
                    if (description.length() > 0) {
                        description = description.substring(0, 1).toUpperCase() + description.substring(1);
                    }
                }

                Map<Singer.coverTypes, String> covers = new LinkedHashMap<>();
                if (jSinger.has("cover")) {
                    JSONObject jCovers = jSinger.getJSONObject("cover");
                    String small = jCovers.getString("small");
                    String big = jCovers.getString("big");
                    if (small != null) covers.put(Singer.coverTypes.small, small);
                    if (big != null) covers.put(Singer.coverTypes.big, big);
                }
                Singer singer = new Singer(id, name, genres, tracks, albums, link, description, covers);
                singers.add(singer);
            } catch (JSONException ex) {
                Log.e("ERROR", String.format("getJSONObject(%d) occurred an error", i));
            }
        }
        return singers;
    }

}
