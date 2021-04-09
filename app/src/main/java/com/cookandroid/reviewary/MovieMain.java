package com.cookandroid.reviewary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.reviewary.R;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class MovieMain extends Fragment {

    private static final String TAG = "MovieMain";
    public static final int REQUEST_CODE_MENU =  201;

    RecyclerView recyclerView;
    MovieAdapter adapter;

    Context context;

    RelativeLayout movieRelative;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.movie_main, container, false);

        movieRelative = rootView.findViewById(R.id.movieRelativeLayout);

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

            @Override
            public void onItemLongClick(MovieAdapter.ViewHolder holder, View view, int position) {
                Movie item = adapter.getItem(position);

                Log.d(TAG, "아이템 길게 선택됨 : " + item.get_id());

                showDeleteMsg(item.get_id());

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if(requestCode == REQUEST_CODE_MENU) {
        if(resultCode == RESULT_OK) {

            loadMovieListData();
        }
//        }
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

            if (recordCount > 0) {
                movieRelative.setVisibility(View.INVISIBLE);

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

                    items.add(new Movie(_id, imagePath, name, director, actor, genre, rating, date, place, impressiveSentence, review));
                }

                outCursor.close();

                adapter.setItems(items);
                adapter.notifyDataSetChanged();
            }
            else {
                movieRelative.setVisibility(View.VISIBLE);
            }
        }
        else {
            movieRelative.setVisibility(View.VISIBLE);
        }

        return recordCount;
    }


    //
    private void showDeleteMsg(final int selectedID)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle(null);
        builder.setMessage("선택한 리뷰를 삭제하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteData(selectedID);
                Toast.makeText(getActivity(), "삭제되었습니다", Toast.LENGTH_SHORT).show();

                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.main_frame);
                FragmentTransaction fragTransaction =  getActivity().getSupportFragmentManager().beginTransaction();
                fragTransaction.detach(currentFragment);
                fragTransaction.attach(currentFragment);
                fragTransaction.commitAllowingStateLoss();
            }
        });
        builder.setNegativeButton("취소", null);

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteData(int MovieId) {
        AppConstants.println("삭제되었습니다");

        String sql = "delete from " + MovieDataBase.TABLE_NOTE +
                " where " +
                "   _id = " + MovieId;

        //Log.d(TAG, "sql : " + sql);
        MovieDataBase database = MovieDataBase.getInstance(context);
        database.execSQL(sql);
    }

}
