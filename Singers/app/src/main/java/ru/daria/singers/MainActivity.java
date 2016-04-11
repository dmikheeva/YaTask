package ru.daria.singers;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;
import com.nostra13.universalimageloader.core.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    List<Singer> singers = new ArrayList<Singer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);



        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setTitle("Singers");

        Extractor ex = new Extractor(new Extractor.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                JSONArray dataJson = null;
                try {
                    dataJson = new JSONArray(output);
                    singers = getSingersFromJson(dataJson);

                    final ListItemAdapter adapter = new ListItemAdapter(MainActivity.this, singers, imageLoader, MainActivity.this);

                    final SwingRightInAnimationAdapter rightInAnimationAdapter = new SwingRightInAnimationAdapter(adapter);

                    ListView mainList = (ListView) findViewById(R.id.listView);
                    rightInAnimationAdapter.setAbsListView(mainList);
                    mainList.setAdapter(rightInAnimationAdapter);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        ex.execute();
    }

    //Действие по нажатию строчки
    @Override
    public void onClick(View view) {
        int position = ((ListView) view.getParent()).getPositionForView(view);
        Singer singer = singers.get(position);
        Intent info = new Intent(this, DetailsActivity.class);
        info.putExtra("SINGER", singer);
        startActivity(info);
    }

    //Получение списка исполнителей из JSON
    private List<Singer> getSingersFromJson(JSONArray array) {
        List<Singer> singers = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jSinger = array.getJSONObject(i);
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
                if (jSinger.has("description")) description = jSinger.getString("description");

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
            } catch (Exception ex) {

            }
        }
        return singers;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

}