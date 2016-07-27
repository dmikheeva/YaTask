package ru.daria.singers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListItemAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private final ImageLoader imageLoader;
    private LayoutInflater lInflater;
    private List<Singer> singers;
    private List<Singer> filteredSingers;
    private View.OnClickListener callback;

    //настройка кэширования изображений
    private static DisplayImageOptions options = new DisplayImageOptions.Builder().
            cacheInMemory(true).cacheOnDisk(true).build();

    public ListItemAdapter(Activity context,
                           List<Singer> singers,
                           ImageLoader imageLoader,
                           View.OnClickListener callback
    ) {
        this.context = context;
        this.singers = singers;
        this.filteredSingers = singers;
        this.imageLoader = imageLoader;
        this.lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.callback = callback;
    }

    static class ViewHolder {
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.singer)
        TextView name;
        @BindView(R.id.styles)
        TextView styles;
        @BindView(R.id.albums)
        TextView description;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getCount() {
        return filteredSingers.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredSingers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * исполнитель по позиции
     */
    public Singer getSinger(int position) {
        return filteredSingers.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        //используем созданные ранее View, но уже не используемые
        if (view == null) {
            view = lInflater.inflate(R.layout.list_single, parent, false);
            holder = new ViewHolder(view);
            view.setClickable(true);
            view.setOnClickListener(callback);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Singer singer = getSinger(position);
        //заполняем View данными
        //пока не подгрузилась картинка показываем дефолтную
        holder.image.setImageResource(R.drawable.defaultimage);
        if (singer.getCovers() != null) {
            imageLoader.displayImage(singer.getCovers().get(Singer.coverTypes.small), holder.image, options);
        }
        holder.name.setText(singer.getName());
        holder.styles.setText(singer.genresToString());
        int albumsNum = singer.getAlbums();
        String[] albumCases = {"альбом", "альбома", "альбомов"};
        int tracksNum = singer.getTracks();
        String[] trackCases = {"песня", "песни", "песен"};

        //склоняем альбомы и песни
        String albumsDescr = "%s • %s";
        albumsDescr = String.format(albumsDescr,
                context.getResources().getQuantityString(R.plurals.songs, albumsNum, albumsNum),
                context.getResources().getQuantityString(R.plurals.tracks, tracksNum, tracksNum));
        holder.description.setText(albumsDescr);
        return view;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults result = new FilterResults();
                List<Singer> listResult = new ArrayList<Singer>();
                if (constraint.length() == 0) {
                    listResult.addAll(singers);
                } else {
                    for (Singer singer : singers) {
                        if (singer.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            listResult.add(singer);
                        }
                    }
                }
                result.count = listResult.size();
                result.values = listResult;

                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults.count == 0) {
                    notifyDataSetInvalidated();
                } else {
                    filteredSingers = (ArrayList<Singer>) filterResults.values;
                    notifyDataSetChanged();
                }
            }

        };
    }

    public List<Singer> getSingers(){
        return singers;
    }

    public void setSingers(List<Singer> singers){
        this.singers.clear();
        this.singers.addAll(singers);
    }
}
