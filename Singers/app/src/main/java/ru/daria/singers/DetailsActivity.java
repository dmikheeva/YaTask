package ru.daria.singers;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class DetailsActivity extends AppCompatActivity {
    DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                onBackPressed();
            }
        });

        ImageView image = (ImageView) findViewById(R.id.image);
        TextView description = (TextView) findViewById(R.id.descriprion);
        TextView genres = (TextView) findViewById(R.id.genres);
        TextView albums = (TextView) findViewById(R.id.albums);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        Singer singer = (Singer) b.get("SINGER");

        toolbar.setTitle(singer.getName());
        String uri = "@drawable/defaultimage";
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);
        image.setImageDrawable(res);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        imageLoader.displayImage(singer.getCovers().get(Singer.coverTypes.big), image, options);
        description.setText(singer.getDescription());
        genres.setText(singer.genresToString());
        int albumsNum = singer.getAlbums();
        String[] albumCases = {"альбом", "альбома", "альбомов"};
        int tracksNum = singer.getTracks();
        String[] trackCases = {"песня", "песни", "песен"};

        albums.setText(albumsNum + " " + singer.getEnding(albumsNum, albumCases) + " " + "\u2022" + " " +
                tracksNum + " " + singer.getEnding(tracksNum, trackCases));
    }
}
