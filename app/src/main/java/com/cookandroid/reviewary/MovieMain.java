package com.cookandroid.reviewary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class MovieMain extends Fragment {

    private static final String TAG = "MovieMain";
    public static final int REQUEST_CODE_MENU =  201;

    RecyclerView recyclerView;
    MovieAdapter adapter;

    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.movie_main, container, false);

        initUI(rootView);

        loadMovieListData();

        return rootView;
    }

    private void initUI(ViewGroup rootView) {
        recyclerView = rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MovieAdapter();

        recyclerView.setAdapter(adapter);

        adapter.setOnMovieClickListener(new OnMovieClickListener() {
            @Override
            public void onItemClick(MovieAdapter.ViewHolder holder, View view, int position) {
                Movie item = adapter.getItem(position);

                Log.d(TAG, "아이템 선택됨 : " + item.get_id());

                final MainActivity activity = (MainActivity) getActivity();

                Intent intent = new Intent(activity.getApplicationContext(), ViewItemActivity.class);
                intent.putExtra("MovieId", item.get_id());
                intent.putExtra("Category", 1);

                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_MENU) {
            if(resultCode == RESULT_OK) {

                loadMovieListData();
            }
        }
    }

    public int loadMovieListData() {
        AppConstants.println("loadNoteListData called.");

        String sql = "select _id, IMAGE, NAME, DIRECTOR, ACTOR, GENRE, RATING, DATE, PLACE, IMPRESSIVE_SENTENCE, REVIEW from " + MovieDataBase.TABLE_NOTE + " order by DATE desc";

        int recordCount = -1;
        MovieDataBase database = MovieDataBase.getInstance(context);
        if (database != null) {
            Cursor outCursor = database.rawQuery(sql);

            recordCount = outCursor.getCount();
            AppConstants.println("record count : " + recordCount + "\n");

            ArrayList<Movie> items = new ArrayList<Movie>();

            for (int i = 0; i < recordCount; i++) {
                outCursor.moveToNext();

                int _id = outCursor.getInt(0);
                String imagePath = outCursor.getString(1);
                String name = outCursor.getString(2);
                String director = outCursor.getString(3);
                String actor = outCursor.getString(4);
                String genre = outCursor.getString(5);
                String rating = outCursor.getString(6);
                String date = outCursor.getString(7);
                String place = outCursor.getString(8);
                String impressiveSentence = outCursor.getString(9);
                String review = outCursor.getString(10);

                AppConstants.println("#" + i + " -> " + _id + ", " + imagePath + ", " +
                        name + ", " + director + ", " + actor + ", " + genre + ", " +
                        rating + ", " + date + ", " + place + "," + impressiveSentence + "," + review);

                items.add(new Movie(_id, imagePath, name, director, actor, genre, rating, date, place , impressiveSentence, review));
            }

            outCursor.close();

            adapter.setItems(items);
            adapter.notifyDataSetChanged();

        }

        return recordCount;
    }

}