package com.cookandroid.reviewary;

import android.view.View;

public interface OnMovieClickListener {
    public void onItemClick(MovieAdapter.ViewHolder holder, View view, int position);
    public void onItemLongClick(MovieAdapter.ViewHolder holder, View view, int position);
}
