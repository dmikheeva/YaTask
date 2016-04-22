package ru.daria.singers;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Извлечение Json по URL
 */
public class Extractor extends AsyncTask<Void, Void, String> {
    private final String URLSingers = "http://cache-default06g.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
    private HttpURLConnection urlConnection = null;
    private String resultJson = "";

    public AsyncResponse delegate = null;

    public interface AsyncResponse {
        /**
         * Вызывается по окончании извлечения данных из json
         */
        void processFinish(String output);
    }

    public Extractor(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(Void... params) {
        // получаем данные с внешнего ресурса
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
    protected void onPostExecute(String strJson) {
        super.onPostExecute(strJson);
        delegate.processFinish(strJson);
    }

}