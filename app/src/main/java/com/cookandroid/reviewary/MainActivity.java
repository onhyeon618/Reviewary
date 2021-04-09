package com.cookandroid.reviewary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    ReviewaryHome ReviewaryHome;
    BookMain BookMain;
    MovieMain MovieMain;
    PlayMain PlayMain;

    BookFragment bookFragment;
    MovieFragment movieFragment;
    PlayFragment playFragment;

    BottomNavigationView navigation;
    Toolbar toolbar;
    BackPressCloseHandler backpress;

    public static BookDataBase bDatabase = null;
    public static MovieDataBase mDatabase = null;
    public static PlayDataBase pDatabase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ReviewaryHome = new ReviewaryHome();
        BookMain = new BookMain();
        MovieMain = new MovieMain();
        PlayMain = new PlayMain();

        bookFragment = new BookFragment();
        movieFragment = new MovieFragment();
        playFragment = new PlayFragment();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, ReviewaryHome).commit();
                        return true;
                    case R.id.nav_book:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, BookMain).commit();
                        return true;
                    case R.id.nav_movie:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, MovieMain).commit();
                        return true;
                    case R.id.nav_play:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, PlayMain).commit();
                        return true;
                }

                return false;
            }
        });

        backpress = new BackPressCloseHandler(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, ReviewaryHome).commit();

        setPicturePath();

        // 데이터베이스 열기
        openDatabase();
    }

    protected void onDestroy() {
        super.onDestroy();

        if (bDatabase != null) {
            bDatabase.close();
            bDatabase = null;
        }

        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }

        if (pDatabase != null) {
            pDatabase.close();
            pDatabase = null;
        }
    }

    /**
     * 데이터베이스 열기 (데이터베이스가 없을 때는 만들기)
     */
    public void openDatabase() {
        // open database
        if (bDatabase != null) {
            bDatabase.close();
            bDatabase = null;
        }

        bDatabase = BookDataBase.getInstance(this);
        boolean isBOpen = bDatabase.open();
        if (isBOpen) {
            Log.d(TAG, "Book database is open.");
        } else {
            Log.d(TAG, "Book database is not open.");
        }

        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }

        mDatabase = MovieDataBase.getInstance(this);
        boolean isMOpen = mDatabase.open();
        if (isMOpen) {
            Log.d(TAG, "Movie database is open.");
        } else {
            Log.d(TAG, "Movie database is not open.");
        }

        if (pDatabase != null) {
            pDatabase.close();
            pDatabase = null;
        }

        pDatabase = PlayDataBase.getInstance(this);
        boolean isPOpen = pDatabase.open();
        if (isPOpen) {
            Log.d(TAG, "Play database is open.");
        } else {
            Log.d(TAG, "Play database is not open.");
        }
    }

    public void setPicturePath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        AppConstants.FOLDER_PHOTO = sdcardPath + File.separator + "photo";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.newItemBtn:
                Intent intent = new Intent(getApplicationContext(), NewItemActivity.class);
                startActivity(intent);
                break;
            case R.id.backupBtn:
                //
                break;
            case R.id.resetBtn:
                //
                break;
            case R.id.qnaBtn:
                Intent intent2 = new Intent(getApplicationContext(), InquiryActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onBackPressed() {
        backpress.onBackPressed();
    }

    private void println(String data) {
        Log.d(TAG, data);
    }
}