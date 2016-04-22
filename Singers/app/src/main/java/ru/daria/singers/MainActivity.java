package ru.daria.singers;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.support.v7.widget.SearchView;

import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;
import com.nostra13.universalimageloader.core.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    List<Singer> singers = new ArrayList<>();
    ListItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Конфигурация для ImageLoader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        //получаем список исполнителей из HomeActivity
        singers = (List<Singer>) intent.getSerializableExtra("SINGERS");

        adapter = new ListItemAdapter(MainActivity.this, singers, imageLoader, MainActivity.this);

        //анимация при пролистывании списка исполнителей
        final SwingRightInAnimationAdapter rightInAnimationAdapter = new SwingRightInAnimationAdapter(adapter);

        ListView mainList = (ListView) findViewById(R.id.listView);
        rightInAnimationAdapter.setAbsListView(mainList);
        mainList.setAdapter(rightInAnimationAdapter);
        mainList.setTextFilterEnabled(true);
    }

    //Действие по нажатию строки в ListView с исполнителями
    @Override
    public void onClick(View view) {
        int position = ((ListView) view.getParent()).getPositionForView(view);
        Singer singer = adapter.getSinger(position);
        Intent info = new Intent(this, DetailsActivity.class);
        info.putExtra("SINGER", singer);
        startActivity(info);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //пункт меню "Поиск"
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(new ComponentName(getApplicationContext(), MainActivity.class)));
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //при подтверждении текста поиска
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //при изменении текста поиска
                adapter.getFilter().filter(newText);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}