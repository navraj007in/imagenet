package com.infodart.instaproject.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.infodart.instaproject.R;
import com.infodart.instaproject.config.Constants;
import com.infodart.instaproject.model.Comment;
import com.infodart.instaproject.model.Photo;
import com.infodart.instaproject.ui.BaseActivity;
import com.infodart.instaproject.utils.Logger;
import com.infodart.instaproject.utils.Utils;
import com.infodart.instaproject.utils.VolleySingleton;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by navraj.singh on 4/25/2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Cursor dataCursor;
    private List<Photo> photoList;
    Context context;
    ArrayList<Comment> comments;
    String mUserId;
    String postId;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtComment;
        public TextView txtName;
        public ImageView imgDelete;
        public TextView distance;
        public ImageView imageView;
        public ViewHolder(View v) {
            super(v);

            //imageView = (ImageView) v.findViewById(R.id.imgPhoto);
            txtComment = (TextView) v.findViewById(R.id.lblComment);
            txtName = (TextView) v.findViewById(R.id.lblName);
            imgDelete = (ImageView) v.findViewById(R.id.imgDeleteComment);

        }
    }

    public CommentAdapter(Activity mContext, ArrayList<Comment> comments) {
        this.comments = comments;
        context = mContext;
        mUserId = ((BaseActivity) context).mUserId;
    }
    public CommentAdapter(Activity mContext, ArrayList<Comment> comments,String postId) {
        this.comments = comments;
        context = mContext;
        mUserId = ((BaseActivity) context).mUserId;
        this.postId = postId ;
    }
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardview = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.commentrow, parent, false);
        return new CommentAdapter.ViewHolder(cardview);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder holder, final int position) {
        final Comment comment = comments.get(position);

        holder.txtName.setText(comment.getName());
        holder.txtComment.setText(comment.getText());
        Logger.e(comment.getUid()+"-"+ mUserId);
        if(mUserId.equalsIgnoreCase(comment.getUid()))
            holder.imgDelete.setVisibility(View.VISIBLE);
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete this comment?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String url = String.format(Constants.GetServerURL() + Constants.URL_COMMENTS,postId);
                        Logger.d(url);
                        RequestQueue queue = Volley.newRequestQueue(context);
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("uid",mUserId);
                        params.put("comment",comment.getText());
                        params.put("tstamp",comment.getPosted());
                        params.put("name",comment.getPosted());
                        Map<String, String> headers = new HashMap<>();

                        queue.add(VolleySingleton.getInstance(context).
                                getStringRequest(Request.Method.DELETE, url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                               comments.remove(position);
                                               notifyDataSetChanged();
                                                Utils.ShowToast(context,"Comment Deleted");
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Utils.ShowToast(context,"Comment could not be Deleted");
                                            }
                                        }, params, params));


                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Utils.ShowToast(context,"Comment not Deleted");
                    }
                });
// Set other dialog properties
//...

// Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        //holder.imageView.setImageBitmap(bmp);


    }

    @Override
    public int getItemCount() {
        return (comments == null) ? 0 : comments.size();
    }
}

