package com.cookandroid.reviewary;

import android.view.View;

public interface OnPlayClickListener {
    public void onItemClick(PlayAdapter.ViewHolder holder, View view, int position);
    public void onItemLongClick(PlayAdapter.ViewHolder holder, View view, int position);
}
