package com.infodart.instaproject.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.infodart.instaproject.R;
import com.infodart.instaproject.config.Constants;
import com.infodart.instaproject.model.Photo;
import com.infodart.instaproject.utils.Logger;
import com.infodart.instaproject.utils.VolleySingleton;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShareToActivity extends BaseActivity {
    Photo photo;
    ImageView imgPreview;
    FirebaseStorage storage ;
    EditText txtCaption;
    ProgressDialog progressDialog;
    String type ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_to);
        storage = FirebaseStorage.getInstance();
        photo = new Photo();
        photo.setPath(getIntent().getStringExtra("photo"));
        type = getIntent().getStringExtra("type");
        imgPreview = (ImageView) findViewById(R.id.imgImagePreview);
        txtCaption = (EditText) findViewById(R.id.txtCaption);

        File imgFile = new File(photo.getPath());
        Picasso.with(this).load(imgFile).into(imgPreview);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_next:
                uploadImage();
                //Intent intent = new Intent(this, ShareToActivity.class);
                //startActivityForResult(intent, 101);

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photo_menu, menu);
        return true;
    }

    private void uploadImage() {
        uploadImageFile();
        String url = Constants.GetServerURL() + Constants.URL_POSTS;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error

                        error.printStackTrace();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("text", txtCaption.getText().toString());
                params.put("type", type);
                params.put("url", photo.getPath());
                params.put("comments", "");
                params.put("likes", "");
                params.put("user_id", mUserId);

                return params;
            }
        };




        //VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        //queue.add(stringRequest);

    }

    private void uploadImageFile() {

        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();

        System.out.println("Random UUID String = " + randomUUIDString);

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://firetest-1f735.appspot.com");

        // Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child(uuid +".jpg");

        // Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child("images/"+ uuid+ ".jpg");

        // While the file names are the same, the references point to different files
        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false
        imgPreview.setDrawingCacheEnabled(true);
        imgPreview.buildDrawingCache();
        progressDialog.setTitle("Posting your image...");
        progressDialog.show();
        Bitmap bitmap = imgPreview.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ShareToActivity.this,"Image Upload Failed",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Logger.v(taskSnapshot.getDownloadUrl().toString());
                postRecord(taskSnapshot.getDownloadUrl().toString());
            }
        });
    }

    private void postRecord(String imgurl) {
        String url = Constants.GetServerURL() + Constants.URL_POSTS;
        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid",mUserId);
        params.put("url",imgurl);
        params.put("text",txtCaption.getText().toString());
        params.put("type", type);
        params.put("comments", "");
        params.put("likes", "");
        Map<String, String> headers = new HashMap<>();

        queue.add(VolleySingleton.getInstance(this).
                getStringRequest(Request.Method.POST, url,postResponseListener,postErrorResponseListener,headers,params));
    }
    Response.Listener<String> postResponseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Toast.makeText(ShareToActivity.this,"Image Upload Successful",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            Logger.i(response);
            Intent intent = new Intent(ShareToActivity.this,InstaHome.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
    };

    Response.ErrorListener postErrorResponseListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(ShareToActivity.this,"Image Upload failed!!",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            error.printStackTrace();
        }
    };
}
