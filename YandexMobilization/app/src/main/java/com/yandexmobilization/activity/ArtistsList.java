package com.yandexmobilization.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.yandexmobilization.R;
import com.yandexmobilization.help.NetworkCheck;
import com.yandexmobilization.model.Artist;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ArtistsList extends BaseActivity {

    ListView artistsLV;
    SwipeRefreshLayout swipeRefresh;
    View customActionBar;
    SearchView searchView;

    List<Artist> artists;
    String url = "http://download.cdn.yandex.net/mobilization-2016/artists.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists_list);
        initUI();
        setUI();
        download();
    }

    //инициализация элементов интерфейса
    void initUI() {
        artistsLV = (ListView) findViewById(R.id.artistsLV);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        LayoutInflater mInflater = LayoutInflater.from(this);
        customActionBar = mInflater.inflate(R.layout.action_bar_custom, null);
    }

    //кастомизируем swipeRefresh и поле поиска
    void setUI() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                download();
            }
        });
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        TextView titleTV = (TextView) customActionBar.findViewById(R.id.titleTV);
        titleTV.setText("Исполнители");
        getSupportActionBar().setCustomView(customActionBar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        searchView = (SearchView) findViewById(R.id.searchView);
        setSearchView(searchView, titleTV);
    }

    //загрука данных
    void download() {
        if ((new NetworkCheck(ArtistsList.this)).check()) { //проверка наличия соединения с Интернетом
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.get(url, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    swipeRefresh.setRefreshing(true);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    String tempString = new String(response);
                    setAdapter(tempString);
                    writeCache(tempString);
                    swipeRefresh.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    setAdapter(readCache()); //при неудаче загружаем последние сохраненные данные из файла
                    swipeRefresh.setRefreshing(false);
                }
            });
        } else {
            Toast.makeText(ArtistsList.this, "Проблемы с подключением к сети Интернет, проверьте соединение и повторите попытку", Toast.LENGTH_LONG).show();
            setAdapter(readCache()); //при отсутствии подключения загружаем последние сохраненные данные из файла
            swipeRefresh.setRefreshing(false);
        }
    }

    void setAdapter(String response) {
        try {
            artists = LoganSquare.parseList(response, Artist.class); //парсим список с помощью сторонней библиотеки
            setAdapter(artists);
        } catch (Exception e) {
            Toast.makeText(ArtistsList.this, "Ошибка при обработке данных. Повторите попытку", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    void setAdapter(final List<Artist> artistsList) {
        ArtistsListAdapter listAdapter = new ArtistsListAdapter(artistsList);
        artistsLV.setAdapter(listAdapter);
        artistsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ArtistsList.this, ArtistsDetail.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("artist", artistsList.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    //адаптер для listview
    public class ArtistsListAdapter extends ArrayAdapter<String> {
        List<Artist> artists;

        public ArtistsListAdapter(List<Artist> artists) {
            super(ArtistsList.this, R.layout.listview_item_artists_list,
                    new String[artists.size()]);
            this.artists = artists;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) ArtistsList.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.listview_item_artists_list,
                    parent, false);
            Artist tempArtist = artists.get(position);
            TextView nameTV = (TextView) rowView.findViewById(R.id.nameTV);
            nameTV.setText(tempArtist.getName());

            TextView genresTV = (TextView) rowView.findViewById(R.id.genresTV);
            List<String> tempGenresList = tempArtist.getGenres();
            String tempGenresString = "";
            for (int i = 0; i < tempGenresList.size(); i++) {
                tempGenresString += tempGenresList.get(i);
                if (i != tempGenresList.size() - 1)
                    tempGenresString += " · ";
            }
            genresTV.setText(tempGenresString);

            TextView statisticsTV = (TextView) rowView.findViewById(R.id.statisticsTV);
            statisticsTV.setText(rightAlbumsForm(tempArtist.getAlbums()) + " · " + rightTracksForm(tempArtist.getTracks()));

            ImageView artistIV = (ImageView) rowView.findViewById(R.id.artistIV);
            Picasso.with(ArtistsList.this)
                    .load(tempArtist.getCover().getSmall())
                    .error(getResources().getDrawable(R.drawable.singer_small))
                    .into(artistIV);
            return rowView;
        }
    }

    //скрываем-показываем title в actionbar, поиске при изменении значения в поле поиска
    private void setSearchView(SearchView searchView, final TextView titleTV) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText.toLowerCase().trim());
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleTV.setVisibility(View.INVISIBLE);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                titleTV.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    //поиск
    void search(String searchString) {
        List<Artist> tempList = new ArrayList<>();
        if (searchString.equals("")) {
            tempList = artists;
        } else {
            for (int i = 0; i < artists.size(); i++) {
                boolean possibleResult = false;
                Artist tempArtist = artists.get(i);

                String[] tempArray = tempArtist.getName().split(" "); //по артистам
                for (String str : tempArray) {
                    if (str.toLowerCase().indexOf(searchString) == 0)
                        possibleResult = true;
                }

                List<String> genres = tempArtist.getGenres(); //по жанрам
                for (String str : genres) {
                    if (str.toLowerCase().indexOf(searchString) == 0)
                        possibleResult = true;
                }

                if (possibleResult)
                    tempList.add(tempArtist); //добавляем элемент если подходит хотя бы по 1 из 2 параметров
            }
        }
        setAdapter(tempList); //меняем элементы listview
    }

    //записываем результат последнего удачного запроса
    void writeCache(String response) {
        JSONObject json;
        Writer writer = null;
        try {
            OutputStream out = ArtistsList.this.openFileOutput("artistsCache.json", 1);
            writer = new OutputStreamWriter(out);
            writer.write(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    //считываем последний удачный результат
    String readCache() {
        StringBuilder result = new StringBuilder();
        BufferedReader reader = null;
        try {
            InputStream in = ArtistsList.this.openFileInput("artistsCache.json");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result.toString();
    }
}
