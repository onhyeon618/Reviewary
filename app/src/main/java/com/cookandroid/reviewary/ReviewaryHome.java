package com.cookandroid.reviewary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.List;

public class ReviewaryHome extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.reviewary_home, container, false);

        final MainActivity activity = (MainActivity) getActivity();

        MaterialCalendarView calendarWidget = (MaterialCalendarView) rootView.findViewById(R.id.calendarWidget);
        calendarWidget.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
//                Intent intent = new Intent(activity.getApplicationContext(), DailyActivity.class);
//                intent.putExtra("SelectedDate", date.getYear()+"년 "+date.getMonth()+"월 "+date.getDay()+"일");
//                startActivity(intent);
//                Log.d("calendar", date.getYear()+"년 "+date.getMonth()+"월 "+date.getDay()+"일");
                Intent intent = new Intent(activity.getApplicationContext(), NewItemActivity.class);
                intent.putExtra("SelectedDate", date.getYear()+"년 "+date.getMonth()+"월 "+date.getDay()+"일");
                startActivity(intent);
            }
        });

        return rootView;
    }
}