package com.infodart.instaproject.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.infodart.instaproject.R;
import com.infodart.instaproject.customviews.ScaledImageView;
import com.infodart.instaproject.model.Photo;
import com.infodart.instaproject.ui.PhotoActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by navraj.singh on 4/8/2017.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder>{
    private List<Photo> photoList;
    Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
        public ImageView photo;

        public MyViewHolder(View view) {
            super(view);
            //title = (TextView) view.findViewById(R.id.title);
            photo = (ImageView) view.findViewById(R.id.imgPhoto);
        }
    }

    public GalleryAdapter(Context context,List<Photo> photoList) {
        this.photoList = photoList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photorow, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Photo movie = photoList.get(position);

        String imageUri = movie.getPath();

        File imgFile = new File(movie.getPath());
        Bitmap bmp = BitmapFactory.decodeFile(movie.getPath());
        Bitmap thump = ThumbnailUtils.extractThumbnail(bmp,200,200);
        //Picasso.with(context).load(thump).into(holder.photo);
        holder.photo.setImageBitmap(thump);

        //if(imgFile.exists()){

            //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            //holder.photo.setImageBitmap(myBitmap);

        //}
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

}
