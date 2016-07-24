package ru.daria.singers;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainFragment.OnSingerSelectedListener {
    List<Singer> singers = new ArrayList<>();
    HeadsetPlugReceiver headsetPlugReceiver;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            HomeFragment fragment = new HomeFragment();
            fragmentTransaction.add(R.id.fragmentContainer, fragment);
            fragmentTransaction.commit();
        }
    }

    private ListItemAdapter getListAdapter() {
        MainFragment currFragment = (MainFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer);
        return currFragment.adapter;
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
                getListAdapter().getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //при изменении текста поиска
                getListAdapter().getFilter().filter(newText);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);

        return super.onCreateOptionsMenu(menu);
    }

    public void showMenuVisible(Menu menu, boolean showMenu) {
        if (menu == null) return;
        menu.setGroupVisible(R.id.main_menu_group, showMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //"О программе"
            case R.id.about:
                FragmentManager fragmentManager = getFragmentManager();
                AboutProgramDialog aboutProgramDialog = new AboutProgramDialog();
                aboutProgramDialog.show(fragmentManager, "О программе");
                break;
            //"Обратная связь"
            case R.id.feedback:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                Resources recources = getResources();
                String uriText = String.format("mailto:%s ?subject= %s &body=%s",
                        recources.getString(R.string.contactEmail),
                        recources.getString(R.string.feedbackSubj),
                        recources.getString(R.string.feedbackText));
                Uri uri = Uri.parse(uriText);
                intent.setData(uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Intent mailer = Intent.createChooser(intent, "Напишите нам...");
                startActivity(mailer);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
            toolbar.setNavigationIcon(null);
            toolbar.setTitle(R.string.title_activity_main);
            showMenuVisible(toolbar.getMenu(), true);
        }
    }

    @Override
    public void onSingerSelected(Singer singer) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle arg = new Bundle();
        arg.putSerializable("SINGER", singer);
        fragment.setArguments(arg);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.headsetPlugReceiver = new HeadsetPlugReceiver();
        registerReceiver(this.headsetPlugReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
    }

    @Override
    protected void onPause() {
        unregisterReceiver(headsetPlugReceiver);
        super.onPause();
    }

    private void showMusicDialog() {
        MusicDialog musicDialog = new MusicDialog();
        musicDialog.show(getFragmentManager(), null);
    }

    public class HeadsetPlugReceiver extends BroadcastReceiver {
        private boolean headsetEnabled = true;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 1:
                        //headset_plugged
                        if (headsetEnabled == false) {
                            headsetEnabled = true;
                            showMusicDialog();
                        }
                        break;
                    case 0:
                        headsetEnabled = false;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

}