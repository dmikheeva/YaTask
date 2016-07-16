package ru.daria.singers;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dsukmanova on 15.07.16.
 */
public class MainFragment extends Fragment implements View.OnClickListener {
    List<Singer> singers = new ArrayList<>();
    ListItemAdapter adapter;
    OnSingerSelectedListener mListener;

    public interface OnSingerSelectedListener {
        void onSingerSelected(Singer singer);
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

        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);

        ((AppCompatActivity) activity).setSupportActionBar(toolbar);

        // Конфигурация для ImageLoader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).build();
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        //получаем список исполнителей из HomeActivity
        singers = (List<Singer>) activity.getIntent().getSerializableExtra("SINGERS");

        adapter = new ListItemAdapter(activity, singers, imageLoader, this);
        final SwingRightInAnimationAdapter rightInAnimationAdapter = new SwingRightInAnimationAdapter(adapter);

        ListView mainList = (ListView) view.findViewById(R.id.listView);
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
