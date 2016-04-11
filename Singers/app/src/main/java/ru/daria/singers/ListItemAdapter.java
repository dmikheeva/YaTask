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

public class ListItemAdapter extends BaseAdapter implements Filterable {
    private Activity context;
    private final ImageLoader imageLoader;
    LayoutInflater lInflator;
    private final List<Singer> singers;
    private View.OnClickListener callback;

    //настройка кэширования изображений
    DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();

    static class ViewHolder {
        public ImageView imageView;
        public TextView name;
        public TextView styles;
        public TextView description;
    }

    public ListItemAdapter(Activity context,
                           List<Singer> singers,
                           ImageLoader imageLoader,
                           View.OnClickListener callback
    ) {
        this.context = context;
        this.singers = singers;
        this.imageLoader = imageLoader;
        lInflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return singers.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return singers.get(position);
    }

    //певец по позиции
    public Singer getSinger(int position) {
        return ((Singer) singers.get(position));
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //используем созданные ранее View, но не используемые
        View view = convertView;
        if (view == null) {
            view = lInflator.inflate(R.layout.list_single, parent, false);
            holder = new ViewHolder();
            //Typeface myFont = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/journal.ttf");
            holder.imageView = (ImageView) view.findViewById(R.id.image);
            holder.name = (TextView) view.findViewById(R.id.singer);
            //holder.name.setTypeface(myFont);
            holder.styles = (TextView) view.findViewById(R.id.styles);
            holder.description = (TextView) view.findViewById(R.id.albums);
            view.setClickable(true);
            view.setOnClickListener(callback);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Singer singer = getSinger(position);
        //и заполняем их
        imageLoader.displayImage(singer.getCovers().get(Singer.coverTypes.small), holder.imageView,options);
        holder.name.setText(singer.getName());
        holder.styles.setText(singer.genresToString());
        int albumsNum = singer.getAlbums();
        String[] albumCases = {"альбом", "альбома", "альбомов"};
        int tracksNum = singer.getTracks();
        String[] trackCases = {"песня", "песни", "песен"};

        holder.description.setText(albumsNum + " " + singer.getEnding(albumsNum, albumCases) + " " + "\u2022" + " " +
                tracksNum + " " + singer.getEnding(tracksNum, trackCases));
        return view;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Singer> filteredResult = getFilteredResults(charSequence);

                FilterResults results = new FilterResults();
                results.values = filteredResult;
                results.count = filteredResult.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ArrayList<Singer> list = (ArrayList<Singer>) filterResults.values;
                ListItemAdapter.this.notifyDataSetChanged();
            }


            private List<Singer> getFilteredResults(CharSequence constraint){
                if (constraint.length() == 0){
                    return  singers;
                }
                List<Singer> listResult = new ArrayList<Singer>();
                for (Singer singer : singers){
                    if (constraint.toString().contains(singer.getName())){
                        listResult.add(singer);
                    }
                }
                return listResult;
            }
        };
    }
}
