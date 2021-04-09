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
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class BookMain extends Fragment {
    private static final String TAG = "BookMain";
    public static final int REQUEST_CODE_MENU =  201;

    RecyclerView recyclerView;
    BookAdapter adapter;

    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.book_main, container, false);

        initUI(rootView);

        loadBookListData();

        return rootView;
    }

    private void initUI(ViewGroup rootView) {
        recyclerView = rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BookAdapter();

        recyclerView.setAdapter(adapter);

        adapter.setOnBookClickListener(new OnBookClickListener() {
            @Override
            public void onItemClick(BookAdapter.ViewHolder holder, View view, int position) {
                Book item = adapter.getItem(position);

                Log.d(TAG, "아이템 선택됨 : " + item.get_id());

                final MainActivity activity = (MainActivity) getActivity();

                Intent intent = new Intent(activity.getApplicationContext(), ViewItemActivity.class);
                intent.putExtra("BookId", item.get_id());
                intent.putExtra("Category", 0);

                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_MENU) {
            if(resultCode == RESULT_OK) {

                loadBookListData();
            }
        }
    }

    public int loadBookListData() {
        AppConstants.println("loadNoteListData called.");

        String sql = "select _id, IMAGE, NAME, AUTHOR, PUBLISHER, GENRE, RATING, READ_DATE, IMPRESSIVE_SENTENCE, REVIEW from " + BookDataBase.TABLE_NOTE + " order by READ_DATE desc";

        int recordCount = -1;
        BookDataBase database = BookDataBase.getInstance(context);
        if (database != null) {
            Cursor outCursor = database.rawQuery(sql);

            recordCount = outCursor.getCount();
            AppConstants.println("record count : " + recordCount + "\n");

            ArrayList<Book> items = new ArrayList<Book>();

            for (int i = 0; i < recordCount; i++) {
                outCursor.moveToNext();

                int _id = outCursor.getInt(0);
                String imagePath = outCursor.getString(1);
                String name = outCursor.getString(2);
                String author = outCursor.getString(3);
                String publisher = outCursor.getString(4);
                String genre = outCursor.getString(5);
                String rating = outCursor.getString(6);
                String readDate = outCursor.getString(7);
                String impressiveSentence = outCursor.getString(8);
                String review = outCursor.getString(9);

                AppConstants.println("#" + i + " -> " + _id + ", " + imagePath + ", " +
                        name + ", " + author + ", " + publisher + ", " + genre + ", " +
                        rating + ", " + readDate + ", " + impressiveSentence + "," + review);

                items.add(new Book(_id, imagePath, name, author, publisher, genre, rating, readDate, impressiveSentence, review));
            }

            outCursor.close();

            adapter.setItems(items);
            adapter.notifyDataSetChanged();

        }

        return recordCount;
    }

}
