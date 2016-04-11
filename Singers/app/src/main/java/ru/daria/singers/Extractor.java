package ru.daria.singers;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//Извлечение Json по URL
public class Extractor extends AsyncTask<Void, Void, String> {

    public AsyncResponse delegate = null;
    public interface AsyncResponse {
        void processFinish(String output);
    }

    public Extractor(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    String URLSingers = "http://cache-default06g.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String resultJson = "";

    @Override
    protected String doInBackground(Void... params) {
        // получаем данные с внешнего ресурса
        try {
            URL url = new URL(URLSingers);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            resultJson = buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
        }
        return resultJson;
    }

    @Override
    protected void onPostExecute(String strJson) {
        super.onPostExecute(strJson);
        try {
            delegate.processFinish(strJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}