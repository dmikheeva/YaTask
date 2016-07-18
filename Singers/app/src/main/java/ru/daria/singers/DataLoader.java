package ru.daria.singers;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by dsukmanova on 17.07.16.
 */
public class DataLoader extends AsyncTaskLoader<String> {
    private final String URLSingers = "http://cache-default06g.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
    private HttpURLConnection urlConnection = null;
    private String resultJson;

    public DataLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (resultJson != null) {
            deliverResult(resultJson);
        } else {
            forceLoad();
        }
    }
    @Override
    public void deliverResult(String data) {
        super.deliverResult(data);
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
    }

    @Override
    public String loadInBackground() {

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
            resultJson = buffer.toString();
        } catch (MalformedURLException e) {
            Log.e("ERROR", "URL is not available");
        } catch (IOException ex) {
            Log.e("ERROR", "Cannot open url connection.");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return resultJson;
    }

    @Override
    protected void onAbandon() {
        super.onAbandon();
    }

    @Override
    protected void onReset() {
        super.onReset();
    }
}
