package com.infodart.instaproject.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.infodart.instaproject.R;
import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by navraj.singh on 5/5/2017.
 */

public class LoaderViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.progressbar)
    ProgressBar mProgressBar;

    public LoaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}