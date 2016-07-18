package ru.daria.singers;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by dsukmanova on 17.07.16.
 */
public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {
    List<Singer> singers = new ArrayList<Singer>();
    final int LOADER_ID = 0;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.splash, container, false);
        Loader loader = getLoaderManager().initLoader(LOADER_ID, null, this);
        loader.forceLoad();
        return view;
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        Loader<String> loader = null;
        if (id == LOADER_ID) {
            loader = new DataLoader(getActivity());
        }
        return loader;
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        try {
            JSONArray dataJson = new JSONArray(data);
            singers = getSingersFromJson(dataJson);
            MainFragment fragment = new MainFragment();
            Bundle arg = new Bundle();
            arg.putSerializable("SINGERS", (Serializable) singers);
            fragment.setArguments(arg);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, fragment);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (JSONException ex) {
            ex.printStackTrace();
            Button b = (Button) view.findViewById(R.id.tryAgainButton);
            b.setVisibility(View.VISIBLE);
            TextView v = (TextView) view.findViewById(R.id.splashText);
            v.setText("Невозможно загрузить список исполнителей.");
        }

    }

    public void buttonOnClick(View view) {
        Button b = (Button) view.findViewById(R.id.tryAgainButton);
        b.setVisibility(View.INVISIBLE);
        getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
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

