package com.cookandroid.reviewary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cookandroid.reviewary.R;

public class ViewItemActivity extends AppCompatActivity {

    Toolbar toolbar;

    TextView viewTitleText;

    ViewBook viewBook;
    ViewMovie viewMovie;
    ViewPlay viewPlay;

    int ContentId;
    int Category;

    boolean isContextChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        Intent intent = getIntent();

        Category = intent.getIntExtra("Category", 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        viewTitleText = (TextView) findViewById(R.id.viewTitleText);

        if(Category == 0) {
            ContentId = intent.getIntExtra("BookId", 0);
            viewBook = new ViewBook();
            getSupportFragmentManager().beginTransaction().replace(R.id.containerForView, viewBook).commit();
        }
        else if(Category == 1) {
            ContentId = intent.getIntExtra("MovieId", 0);
            viewMovie = new ViewMovie();
            getSupportFragmentManager().beginTransaction().replace(R.id.containerForView, viewMovie).commit();
        }
        else {
            ContentId = intent.getIntExtra("PlayId", 0);
            viewPlay = new ViewPlay();
            getSupportFragmentManager().beginTransaction().replace(R.id.containerForView, viewPlay).commit();
        }

        viewTitleText.setText("리뷰어리");
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

    public int getMyData() {
        return ContentId;
    }
}