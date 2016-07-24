package ru.daria.singers;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dsukmanova on 17.07.16.
 */
public class DataLoader extends AsyncTaskLoader<DataLoaderResult<List<Singer>>> {
    private final String URLSingers = "http://cache-default06g.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
    private HttpURLConnection urlConnection = null;
    private DataLoaderResult<List<Singer>> result;

    public DataLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (result != null) {
            deliverResult(result);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(DataLoaderResult<List<Singer>> result) {
        this.result = result;
        super.deliverResult(result);
    }

    @Override
    public DataLoaderResult<List<Singer>> loadInBackground() {
        DataLoaderResult<List<Singer>> result = new DataLoaderResult<List<Singer>>();
        try {
            URL url = new URL(URLSingers);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            String resultJson = buffer.toString();
            JSONArray dataJson = new JSONArray(resultJson);
            List<Singer> singers = getSingersFromJson(dataJson);
            result.setResult(singers);
        } catch (MalformedURLException ex) {
            Log.e("ERROR", "URL is not available");
            result.setException(ex);
        } catch (IOException ex) {
            Log.e("ERROR", "Cannot open url connection.");
            result.setException(ex);
        } catch (JSONException ex) {
            Log.e("ERROR", "Problems with JSON");
            result.setException(ex);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
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
