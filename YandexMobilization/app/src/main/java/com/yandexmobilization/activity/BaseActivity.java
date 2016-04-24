package com.yandexmobilization.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    //склоняем альбомы в зависимостиот количества
    String rightAlbumsForm(long count) {
        if (count % 10 == 1)
            return count + " альбом";

        if (count % 10 >= 2 && count % 10 <=4)
            return count + " альбома";

        return count + " альбомов";
    }

    //склоняем треки в зависимостиот количества
    String rightTracksForm(long count) {
        if (count % 10 == 1)
            return count + " песня";

        if (count % 10 >= 2 && count % 10 <=4)
            return count + " песни";

        return count + " песен";
    }
}
