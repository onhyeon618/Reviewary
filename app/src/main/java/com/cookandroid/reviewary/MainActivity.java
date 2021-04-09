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

        // 데이터베이스 열기
        openDatabase();

        // 권한 확인
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

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 다시 보지 않기 버튼을 만드려면 이 부분에 바로 요청을 하도록 하면 됨 (아래 else{..} 부분 제거)
            // ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);

            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent;
                                intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
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
                    // grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
                    if (grantResults[i] < 0) {
                        Toast.makeText(MainActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
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
        //Log.d(TAG, "돌려받은 값"+resultCode);
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
        builder.setMessage("백업 기능은 아직 지원되지 않고 있습니다. 업데이트를 기다려주세요!");

        builder.setPositiveButton("확인", null);

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void AskResetMsg()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle(null);
        builder.setMessage("저장된 리뷰가 모두 삭제되며, 삭제 된 리뷰는 되돌릴 수 없습니다. 정말 초기화 하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                bDatabase.delete();
                mDatabase.delete();
                pDatabase.delete();
                openDatabase();
                Toast.makeText(MainActivity.this, "저장된 리뷰가 초기화되었습니다.", Toast.LENGTH_LONG).show();
                refreshFragment(R.id.main_frame);
            }
        });
        builder.setNegativeButton("취소", null);

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
