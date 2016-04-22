package ru.daria.singers;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class DetailsActivity extends AppCompatActivity {
    //настройка кэширования изображений
    private static DisplayImageOptions options = new DisplayImageOptions.Builder().
            cacheInMemory(true).cacheOnDisk(true).build();
    private String albumsDescr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //действие аналогично стандартной кнопке "Назад"
                onBackPressed();
            }
        });

        ImageView image = (ImageView) findViewById(R.id.image);
        TextView description = (TextView) findViewById(R.id.descriprion);
        TextView genres = (TextView) findViewById(R.id.genres);
        TextView albums = (TextView) findViewById(R.id.albums);
        //получаем экземпляр Singer из MainActivity
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        Singer singer = (Singer) b.get("SINGER");

        toolbar.setTitle(singer.getName());
        //пока подгружается изображение показываем стандартное
        String uri = "@drawable/defaultimage";
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);
        image.setImageDrawable(res);

        //Конфигурация для ImageLoader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        if (singer.getCovers() != null) {
            //закэшируем картинку
            imageLoader.displayImage(singer.getCovers().get(Singer.coverTypes.big), image, options);
        }
        description.setText(singer.getDescription());
        genres.setText(singer.genresToString());
        int albumsNum = singer.getAlbums();
        String[] albumCases = {"альбом", "альбома", "альбомов"};
        int tracksNum = singer.getTracks();
        String[] trackCases = {"песня", "песни", "песен"};
        //склоняем альбомы и песни
        String albumsDescr = "%d %s • %d %s";
        albumsDescr = String.format(albumsDescr, albumsNum, singer.getEnding(albumsNum, albumCases),
                tracksNum, singer.getEnding(tracksNum, trackCases));
        albums.setText(albumsDescr);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

}
