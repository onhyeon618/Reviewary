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

public class PlayMain extends Fragment {

    private static final String TAG = "PlayMain";
    public static final int REQUEST_CODE_MENU = 201;

    RecyclerView recyclerView;
    PlayAdapter adapter;

    Context context;

    RelativeLayout playRelative;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.play_main, container, false);

        playRelative = rootView.findViewById(R.id.playRelativeLayout);

        initUI(rootView);

        loadPlayListData();

        return rootView;
    }

    private void initUI(ViewGroup rootView) {
        recyclerView = rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PlayAdapter();

        recyclerView.setAdapter(adapter);

        adapter.setOnPlayClickListener(new OnPlayClickListener() {
            @Override
            public void onItemClick(PlayAdapter.ViewHolder holder, View view, int position) {
                Play item = adapter.getItem(position);

                Log.d(TAG, "아이템 선택됨 : " + item.get_id());

                final MainActivity activity = (MainActivity) getActivity();

                Intent intent = new Intent(activity.getApplicationContext(), ViewItemActivity.class);
                intent.putExtra("PlayId", item.get_id());
                intent.putExtra("Category", 2);

                startActivityForResult(intent, REQUEST_CODE_MENU);
            }

            @Override
            public void onItemLongClick(PlayAdapter.ViewHolder holder, View view, int position) {
                Play item = adapter.getItem(position);

                Log.d(TAG, "아이템 길게 선택됨 : " + item.get_id());

                showDeleteMsg(item.get_id());

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_MENU) {
            if (resultCode == RESULT_OK) {

                loadPlayListData();
            }
        }
    }

    public int loadPlayListData() {
        AppConstants.println("loadNoteListData called.");

        String sql = "select _id, IMAGE, NAME, ACTOR, GENRE, RATING, DATE, PLACE, IMPRESSIVE_SENTENCE, REVIEW from " + PlayDataBase.TABLE_NOTE + " order by DATE desc";

        int recordCount = -1;
        PlayDataBase database = PlayDataBase.getInstance(context);

        if (database != null) {
            Cursor outCursor = database.rawQuery(sql);

            recordCount = outCursor.getCount();
            AppConstants.println("record count : " + recordCount + "\n");

            ArrayList<Play> items = new ArrayList<Play>();

            if(recordCount > 0) {
                playRelative.setVisibility(View.INVISIBLE);

                for (int i = 0; i < recordCount; i++) {
                    outCursor.moveToNext();

                    int _id = outCursor.getInt(0);
                    String imagePath = outCursor.getString(1);
                    String name = outCursor.getString(2);
                    String actor = outCursor.getString(3);
                    String genre = outCursor.getString(4);
                    String rating = outCursor.getString(5);
                    String date = outCursor.getString(6);
                    String place = outCursor.getString(7);
                    String impressiveSentence = outCursor.getString(8);
                    String review = outCursor.getString(9);

                    AppConstants.println("#" + i + " -> " + _id + ", " + imagePath + ", " +
                            name + ", " + actor + ", " + genre + ", " +
                            rating + ", " + date + ", " + place + "," + impressiveSentence + "," + review);

                    items.add(new Play(_id, imagePath, name, actor, genre, rating, date, place, impressiveSentence, review));
                }
            }
            else {
                playRelative.setVisibility(View.VISIBLE);
            }

            outCursor.close();

            adapter.setItems(items);
            adapter.notifyDataSetChanged();

        }
        else {
            playRelative.setVisibility(View.VISIBLE);
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

    private void deleteData(int PlayId) {
        AppConstants.println("삭제되었습니다");

        String sql = "delete from " + PlayDataBase.TABLE_NOTE +
                " where " +
                "   _id = " + PlayId;

        //Log.d(TAG, "sql : " + sql);
        PlayDataBase database = PlayDataBase.getInstance(context);
        database.execSQL(sql);
    }
}