package com.infodart.instaproject.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.infodart.instaproject.R;
import com.infodart.instaproject.model.Photo;

import java.io.File;
import java.util.List;

/**
 * Created by navraj.singh on 4/10/2017.
 */

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    Cursor dataCursor;
    private List<Photo> photoList;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time;
        public TextView date;
        public TextView distance;
        public ImageView imageView;
        public ViewHolder(View v) {
            super(v);

            imageView = (ImageView) v.findViewById(R.id.imgPhoto);
        }
    }

    public RecordAdapter(Activity mContext, Cursor cursor) {
        dataCursor = cursor;
        context = mContext;
    }

    @Override
    public RecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardview = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photorow, parent, false);
        return new ViewHolder(cardview);
    }

    public Cursor swapCursor(Cursor cursor) {
        if (dataCursor == cursor) {
            return null;
        }
        Cursor oldCursor = dataCursor;
        this.dataCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        dataCursor.moveToPosition(position);

        String imageUri = dataCursor.getString(1);
        File file = new File(imageUri);
        Uri uri = Uri.fromFile(file);
        Bitmap bmp = BitmapFactory.decodeFile(imageUri);

        holder.imageView.setImageBitmap(bmp);


    }

    @Override
    public int getItemCount() {
        return (dataCursor == null) ? 0 : dataCursor.getCount();
    }
}
