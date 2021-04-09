package com.cookandroid.reviewary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cookandroid.reviewary.R;
import com.google.android.material.tabs.TabLayout;

public class NewItemActivity extends AppCompatActivity {

    Toolbar toolbar;

    TextView titleText;

    BookFragment bookFragment;
    MovieFragment movieFragment;
    PlayFragment playFragment;

    String SelectedDate;

    boolean isContextChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        Intent intent = getIntent();
        int selectedNavi =intent.getIntExtra("currentNavi", 0);
        SelectedDate = intent.getStringExtra("SelectedDate");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleText = (TextView) findViewById(R.id.titleText);

        bookFragment = new BookFragment();
        movieFragment = new MovieFragment();
        playFragment = new PlayFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, bookFragment).commit();
        titleText.setText("도서");

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("도서"));
        tabs.addTab(tabs.newTab().setText("영화"));
        tabs.addTab(tabs.newTab().setText("연극뮤지컬"));

        switch(selectedNavi) {
            case 0:
            case 1:
                tabs.getTabAt(0).select();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, bookFragment).commit();
                titleText.setText("도서");
                break;
            case 2:
                tabs.getTabAt(1).select();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, movieFragment).commit();
                titleText.setText("영화");
                break;
            case 3:
                tabs.getTabAt(2).select();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, playFragment).commit();
                titleText.setText("연극뮤지컬");
                break;
        }

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d("MainActivity", "선택된 탭 : " + position);

                Fragment selected = null;
                if (position == 0) {
                    selected = bookFragment;
                    titleText.setText("도서");
                } else if (position == 1) {
                    selected = movieFragment;
                    titleText.setText("영화");
                } else if (position == 2) {
                    selected = playFragment;
                    titleText.setText("연극뮤지컬");
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    private void showEndMsg()
    {
        if(isContextChanged) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //builder.setTitle(null);
            builder.setMessage("작성하던 리뷰가 삭제됩니다. 작성을 취소하시겠습니까?");

            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setNegativeButton("취소", null);

            builder.setCancelable(true);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                showEndMsg();
                //finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showEndMsg();
    }

    public String getMyDate() {
        return SelectedDate;
    }
}