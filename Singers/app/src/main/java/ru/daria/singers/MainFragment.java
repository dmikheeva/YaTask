package ru.daria.singers;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by dsukmanova on 15.07.16.
 */
public class MainFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<DataLoaderResult<List<Singer>>> {
    List<Singer> singers = new ArrayList<>();
    ListItemAdapter adapter;
    OnSingerSelectedListener mListener;
    final int LOADER_ID = 0;

    public interface OnSingerSelectedListener {
        void onSingerSelected(Singer singer);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Loader loader = getLoaderManager().initLoader(LOADER_ID, null, this);
        loader.forceLoad();
    }

    @Override
    public Loader<DataLoaderResult<List<Singer>>> onCreateLoader(int id, Bundle args) {
        DataLoader loader = new DataLoader(getActivity());
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<DataLoaderResult<List<Singer>>> loader, DataLoaderResult<List<Singer>> data) {
        Exception ex = data.getException();
        if (ex != null) {
            ex.printStackTrace();
            if (ex instanceof IOException) {
                /*Button b = ButterKnife.findById(view, R.id.tryAgainButton);
                b.setVisibility(View.VISIBLE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button b = (Button) v;
                        b.setVisibility(View.INVISIBLE);
                        getLoaderManager().restartLoader(LOADER_ID, null, HomeFragment.this);
                    }
                });
                TextView v = ButterKnife.findById(view, R.id.splashText);
                v.setText("Невозможно загрузить список исполнителей.");*/
                ex.printStackTrace();
            }
        } else {
            singers = data.getResult();
            adapter.setSingers(singers);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<DataLoaderResult<List<Singer>>> loader) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mListener = (OnSingerSelectedListener) getActivity();
        } catch (ClassCastException ex) {
            throw new ClassCastException(getActivity().toString() + "must implement OnSingerSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);
        Activity activity = getActivity();

        ((AppCompatActivity) activity).setSupportActionBar(((MainActivity)activity).getToolbar());

        // Конфигурация для ImageLoader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).build();
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        adapter = new ListItemAdapter(activity, singers, imageLoader, this);

        final SwingRightInAnimationAdapter rightInAnimationAdapter = new SwingRightInAnimationAdapter(adapter);
        ListView mainList = ButterKnife.findById(view,R.id.listView);
        rightInAnimationAdapter.setAbsListView(mainList);
        mainList.setAdapter(rightInAnimationAdapter);
        mainList.setTextFilterEnabled(true);

        return view;
    }

    @Override
    public void onClick(View view) {
        int position = ((ListView) view.getParent()).getPositionForView(view);
        Singer singer = adapter.getSinger(position);
        mListener.onSingerSelected(singer);
    }
}
