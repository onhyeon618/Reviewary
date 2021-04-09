package com.cookandroid.reviewary;

import android.view.View;

public interface OnBookClickListener {
    public void onItemClick(BookAdapter.ViewHolder holder, View view, int position);
    public void onItemLongClick(BookAdapter.ViewHolder holder, View view, int position);
}
