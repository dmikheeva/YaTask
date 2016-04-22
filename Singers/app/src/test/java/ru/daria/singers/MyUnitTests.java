package ru.daria.singers;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class MyUnitTests {
    /**
     * Тест проверяет, все ли исполнители из json попали в список объектов-исполнителей
     */
    @Test
    public void correct_parser_test() {
        Extractor ex = new Extractor(new Extractor.AsyncResponse() {
            @Override
            public void processFinish(String output) {
            }
        });
        JSONArray dataJson = null;
        try {
            dataJson = new JSONArray(ex.doInBackground());
        } catch (JSONException e) {
            Log.e("ERROR", "Cannot take jsonArray in correct_parser test");
        }
        HomeActivity m = new HomeActivity();
        List<Singer> singers = m.getSingersFromJson(dataJson);
        assert dataJson.length() == singers.size();
    }

    /**
     * Тест проверяет, существуют ли элементы json с неописанными
     * в Singers атрибутами
     */
    @Test
    public void new_fields_test() {
        Extractor ex = new Extractor(new Extractor.AsyncResponse() {
            @Override
            public void processFinish(String output) {
            }
        });
        int j = 0;
        JSONArray dataJson;
        try {
            dataJson = new JSONArray(ex.doInBackground());
            for (int i = 0; i < dataJson.length(); i++){
                JSONObject obj = dataJson.getJSONObject(i);
                if (obj.length() > 8) {
                    j++;
                    break;
                }
            }
        } catch (JSONException e) {

        }
        assert j == 0;
    }
}