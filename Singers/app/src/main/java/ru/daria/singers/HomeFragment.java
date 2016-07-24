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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by dsukmanova on 17.07.16.
 */
public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<DataLoaderResult<List<Singer>>> {
    List<Singer> singers = new ArrayList<Singer>();
    final int LOADER_ID = 0;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Loader loader = getLoaderManager().initLoader(LOADER_ID, null, this);
        loader.forceLoad();
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.splash, container, false);
        return view;
    }

    @Override
    public Loader<DataLoaderResult<List<Singer>>> onCreateLoader(int id, Bundle args) {
        DataLoader loader = null;
        if (id == LOADER_ID) {
            loader = new DataLoader(getActivity());
        }
        return loader;
    }

    @Override
    public void onLoaderReset(Loader<DataLoaderResult<List<Singer>>> loader) {

    }

    @Override
    public void onLoadFinished(Loader<DataLoaderResult<List<Singer>>> loader, DataLoaderResult<List<Singer>> data) {
        Exception ex = data.getException();
        if (ex != null) {
            ex.printStackTrace();
            if (ex instanceof IOException) {
                Button b = ButterKnife.findById(view, R.id.tryAgainButton);
                b.setVisibility(View.VISIBLE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button b = (Button) v;
                        b.setVisibility(View.INVISIBLE);
                        getLoaderManager().initLoader(LOADER_ID, null, HomeFragment.this).forceLoad();
                    }
                });
                TextView v = ButterKnife.findById(view, R.id.splashText);
                v.setText("Невозможно загрузить список исполнителей.");
            }
        } else {

            singers = data.getResult();
            MainFragment fragment = new MainFragment();
            Bundle arg = new Bundle();
            arg.putSerializable("SINGERS", (Serializable) singers);
            fragment.setArguments(arg);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, fragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }
}

