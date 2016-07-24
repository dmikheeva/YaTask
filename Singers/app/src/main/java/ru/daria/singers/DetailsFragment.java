package ru.daria.singers;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dsukmanova on 16.07.16.
 */
public class DetailsFragment extends Fragment {
    private static DisplayImageOptions options = new DisplayImageOptions.Builder().
            cacheInMemory(true).cacheOnDisk(true).build();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ButterKnife.bind(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details, container, false);
        final MainActivity activity = (MainActivity) getActivity();

        Toolbar toolbar = activity.getToolbar();
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        activity.showMenuVisible(toolbar.getMenu(), false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //действие аналогично стандартной кнопке "Назад"
                activity.onBackPressed();
            }
        });

        ImageView image = ButterKnife.findById(view, R.id.image);
        TextView description = ButterKnife.findById(view, R.id.descriprion);
        TextView genres = ButterKnife.findById(view, R.id.genres);
        TextView albums = ButterKnife.findById(view, R.id.albums);
        //получаем экземпляр Singer из MainActivity
        Bundle b = getArguments();
        Singer singer = (Singer) b.get("SINGER");

        toolbar.setTitle(singer.getName());
        //пока подгружается изображение показываем стандартное
        String uri = "@drawable/defaultimage";
        int imageResource = getResources().getIdentifier(uri, null, activity.getPackageName());
        Drawable res = getResources().getDrawable(imageResource);
        image.setImageDrawable(res);

        //Конфигурация для ImageLoader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(activity).build();
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        if (singer.getCovers() != null) {
            //закэшируем картинку
            imageLoader.displayImage(singer.getCovers().get(Singer.coverTypes.big), image, options);
        }
        description.setText(singer.getDescription());
        genres.setText(singer.genresToString());
        int albumsNum = singer.getAlbums();
        int tracksNum = singer.getTracks();
        //склоняем альбомы и песни
        String albumsDescr = "%s • %s";
        albumsDescr = String.format(albumsDescr,
                getResources().getQuantityString(R.plurals.songs, albumsNum, albumsNum),
                getResources().getQuantityString(R.plurals.tracks, tracksNum, tracksNum));
        albums.setText(albumsDescr);
        return view;
    }
}
