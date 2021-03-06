package com.cookandroid.reviewary;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cookandroid.reviewary.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int MY_PERMISSION_STORAGE = 1001;

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

    int navigationIndex = 0;

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
                        navigationIndex = 0;
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, ReviewaryHome).commit();
                        return true;
                    case R.id.nav_book:
                        navigationIndex = 1;
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, BookMain).commit();
                        return true;
                    case R.id.nav_movie:
                        navigationIndex = 2;
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, MovieMain).commit();
                        return true;
                    case R.id.nav_play:
                        navigationIndex = 3;
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, PlayMain).commit();
                        return true;
                }

                return false;
            }
        });

        backpress = new BackPressCloseHandler(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, ReviewaryHome).commit();

        setPicturePath();

        // ?????????????????? ??????
        openDatabase();

        // ?????? ??????
        checkPermission();
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
     * ?????????????????? ?????? (????????????????????? ?????? ?????? ?????????)
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

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // ?????? ?????? ?????? ????????? ???????????? ??? ????????? ?????? ????????? ????????? ?????? ??? (?????? else{..} ?????? ??????)
            // ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);

            // ?????? ???????????? if()?????? ????????? false??? ?????? ??? -> else{..}??? ???????????? ?????????
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("??????")
                        .setMessage("????????? ?????????????????????. ????????? ???????????? ???????????? ?????? ????????? ?????? ??????????????? ?????????.")
                        .setNeutralButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent;
                                intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_STORAGE:
                for (int i = 0; i < grantResults.length; i++) {
                    // grantResults[] : ????????? ????????? 0, ????????? ????????? -1
                    if (grantResults[i] < 0) {
                        Toast.makeText(MainActivity.this, "?????? ????????? ????????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
        }
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
                intent.putExtra("currentNavi", navigationIndex);
                startActivityForResult(intent, 0);
                break;
            case R.id.backupBtn:
                AskBackUpMsg();
                break;
            case R.id.resetBtn:
                AskResetMsg();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Log.d(TAG, "???????????? ???"+resultCode);
        if(resultCode == 1) {

            refreshFragment(R.id.main_frame);

        }
    }

    protected void refreshFragment(int fragmentID) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(fragmentID);
        if (currentFragment instanceof BookMain || currentFragment instanceof MovieMain || currentFragment instanceof PlayMain ) {
            FragmentTransaction fragTransaction =  getSupportFragmentManager().beginTransaction();
            fragTransaction.detach(currentFragment);
            fragTransaction.attach(currentFragment);
            fragTransaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void onBackPressed() {
        backpress.onBackPressed();
    }

    private void println(String data) {
        Log.d(TAG, data);
    }

    public void AskBackUpMsg()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SORRY! :(");
        builder.setMessage("?????? ????????? ?????? ???????????? ?????? ????????????. ??????????????? ??????????????????!");

        builder.setPositiveButton("??????", null);

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void AskResetMsg()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle(null);
        builder.setMessage("????????? ????????? ?????? ????????????, ?????? ??? ????????? ????????? ??? ????????????. ?????? ????????? ???????????????????");

        builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                bDatabase.delete();
                mDatabase.delete();
                pDatabase.delete();
                openDatabase();
                Toast.makeText(MainActivity.this, "????????? ????????? ????????????????????????.", Toast.LENGTH_LONG).show();
                refreshFragment(R.id.main_frame);
            }
        });
        builder.setNegativeButton("??????", null);

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
