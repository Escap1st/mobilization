package com.yandexmobilization.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.yandexmobilization.R;
import com.yandexmobilization.model.Artist;

import java.util.List;

public class ArtistsDetail extends BaseActivity {

    ImageView artistIV;
    TextView genresTV;
    TextView statisticsTV;
    TextView titleTV;
    TextView infoTV;


    Artist artist;
    DisplayMetrics metrics; //для определения размеров экрана и обрезки фотографии

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists_detail);
        initUI();
        artist = (Artist) this.getIntent().getExtras().getSerializable("artist");
        setUI();
    }

    //инициализация элементов интерфейса
    void initUI() {
        artistIV = (ImageView) findViewById(R.id.artistIV);
        genresTV = (TextView) findViewById(R.id.genresTV);
        statisticsTV = (TextView) findViewById(R.id.statisticsTV);
        titleTV = (TextView) findViewById(R.id.titleTV);
        infoTV = (TextView) findViewById(R.id.infoTV);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    //заполняем интерфейс информацией об артисте
    void setUI() {
        getSupportActionBar().setTitle(artist.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //фото артиста в пропорциях 3х2
        artistIV.setLayoutParams(new RelativeLayout.LayoutParams(metrics.widthPixels, metrics.widthPixels * 2 / 3));
        Picasso.with(ArtistsDetail.this)
                .load(artist.getCover().getBig())
                .error(getResources().getDrawable(R.drawable.singer_big))
                .transform(new CropTransformation())
                .into(artistIV);

        titleTV.setText("Биография");

        infoTV.setText(artist.getDescription());

        List<String> tempGenresList = artist.getGenres();
        String tempGenresString = "";
        for (int i = 0; i < tempGenresList.size(); i++) {
            tempGenresString += tempGenresList.get(i);
            if (i != tempGenresList.size() - 1)
                tempGenresString += " · ";
        }
        genresTV.setText(tempGenresString);

        statisticsTV.setText(rightAlbumsForm(artist.getAlbums()) + " · " + rightTracksForm(artist.getTracks()));
    }

    public class CropTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            try {
                int width = metrics.widthPixels;
                int height = width * source.getHeight() / source.getWidth();
                int scaledHeight = width * 2 / 3; // пропорции 3х2
                Bitmap result = Bitmap.createScaledBitmap(source, width, height, false); //подгоняем изображение под нужный размер экрана
                scaledHeight = height >= scaledHeight ? scaledHeight : height; //если изображение слишком узкое - ставим максимально возможную для него ширину
                result = Bitmap.createBitmap(result, 0, 0, width, scaledHeight); //обрезаем, оставляем только верхнюю часть, хотя рискуем получить не очень красивую картинку если лицо было в центре
                if (result != source) {
                    source.recycle();
                }
                return result;
            } catch (Exception e){
                e.printStackTrace();
                return source;
            }
        }

        @Override
        public String key() {
            return "crop()";
        }
    }
}
