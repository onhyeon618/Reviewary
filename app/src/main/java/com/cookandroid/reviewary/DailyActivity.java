//package com.cookandroid.reviewary;
//
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//
//import static android.app.Activity.RESULT_OK;
//
//public class DailyActivity extends AppCompatActivity {
//
//    RecyclerView recyclerView;
//    BookAdapter adapter;
//
//    Context context;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_new_item);
//
//        recyclerView = findViewById(R.id.recyclerView);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//
//        adapter = new BookAdapter();
//        recyclerView.setAdapter(adapter);
//        adapter.setOnBookClickListener(new OnBookClickListener() {
//            @Override
//            public void onItemClick(BookAdapter.ViewHolder holder, View view, int position) {
//                Book item = adapter.getItem(position);
//
//                Intent intent = getIntent();
//                intent.putExtra("BookId", item.get_id());
//                intent.putExtra("BookImage", item.getImage());
//                intent.putExtra("BookName", item.get_id());
//                intent.putExtra("BookAuthor", item.getAuthor());
//                intent.putExtra("BookPub", item.getPublisher());
//                intent.putExtra("BookGenre", item.getGenre());
//                intent.putExtra("BookRate", item.getRating());
//                intent.putExtra("BookDate", item.getReadDate());
//                intent.putExtra("BookImp", item.getImpressiveSentence());
//                intent.putExtra("BookReview", item.getReview());
//            }
//        });
//
//        loadBookListData();
//    }
//
//
//    public int loadBookListData() {
//        AppConstants.println("loadNoteListData called.");
//
//        String sql = "select _id, IMAGE, NAME, AUTHOR, PUBLISHER, GENRE, RATING, READ_DATE, IMPRESSIVE_SENTENCE, REVIEW from " + BookDataBase.TABLE_NOTE + " where " +
//                " READ_DATE = ";
//
//        int recordCount = -1;
//        BookDataBase database = BookDataBase.getInstance(context);
//        if (database != null) {
//            Cursor outCursor = database.rawQuery(sql);
//
//            recordCount = outCursor.getCount();
//            AppConstants.println("record count : " + recordCount + "\n");
//
//            ArrayList<Book> items = new ArrayList<Book>();
//
//            for (int i = 0; i < recordCount; i++) {
//                outCursor.moveToNext();
//
//                int _id = outCursor.getInt(0);
//                String imagePath = outCursor.getString(1);
//                String name = outCursor.getString(2);
//                String author = outCursor.getString(3);
//                String publisher = outCursor.getString(4);
//                String genre = outCursor.getString(5);
//                String rating = outCursor.getString(6);
//                String readDate = outCursor.getString(7);
//                String impressiveSentence = outCursor.getString(8);
//                String review = outCursor.getString(9);
//
//                AppConstants.println("#" + i + " -> " + _id + ", " + imagePath + ", " +
//                        name + ", " + author + ", " + publisher + ", " + genre + ", " +
//                        rating + ", " + readDate + ", " + impressiveSentence + "," + review);
//
//                items.add(new Book(_id, imagePath, name, author, publisher, genre, rating, readDate, impressiveSentence, review));
//            }
//
//            outCursor.close();
//
//            adapter.setItems(items);
//            adapter.notifyDataSetChanged();
//
//        }
//
//        return recordCount;
//    }
//
//}
