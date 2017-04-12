package com.infodart.instaproject.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.infodart.instaproject.R;
import com.infodart.instaproject.adapters.GalleryAdapter;
import com.infodart.instaproject.adapters.RecordAdapter;
import com.infodart.instaproject.adapters.RecyclerItemClickListener;
import com.infodart.instaproject.model.Photo;
import com.infodart.instaproject.utils.Logger;
import com.infodart.instaproject.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PhotoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "AndroidCameraApi";
    private ImageButton takePictureButton;
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private LinearLayout layoutGallery;
    private RelativeLayout layoutPhoto;
    private RelativeLayout layoutVideo;
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private RecyclerView recyclerView;
    TextView mTextMessage;
    String type ;
    GridView gridView;
    SimpleCursorAdapter mAdapter;

    private final int THUMBNAIL_LOADER_ID  = 0;
    private final int IMAGE_LOADER_ID  = 1;

    MatrixCursor mMatrixCursor;
    Cursor mThumbCursor;
    Cursor mImageCursor;

    String mThumbImageId="";
    String mThumbImageData="";
    String mImageSize="";
    String mImageTitle="";
    String mImageWidth="";
    String mImageHeight="";

    private ArrayList<Photo> photoList;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_gallery:
                    layoutGallery.setVisibility(View.VISIBLE);
                    layoutPhoto.setVisibility(View.GONE);
                    layoutVideo.setVisibility(View.GONE);
                    type = "image";
                    return true;
                case R.id.navigation_photo:
                    layoutGallery.setVisibility(View.GONE);
                    layoutPhoto.setVisibility(View.VISIBLE);
                    layoutVideo.setVisibility(View.GONE);
                    type = "image";
                    return true;
                case R.id.navigation_video:
                    layoutGallery.setVisibility(View.GONE);
                    layoutPhoto.setVisibility(View.GONE);
                    layoutVideo.setVisibility(View.VISIBLE);
                    type = "video";
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        textureView = (TextureView) findViewById(R.id.texture);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);
        takePictureButton = (ImageButton) findViewById(R.id.btn_takepicture);
        assert takePictureButton != null;
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        photoList = new ArrayList<Photo>();
        type = "image";
        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        layoutVideo = (RelativeLayout) findViewById(R.id.layoutVideo);
        layoutPhoto = (RelativeLayout) findViewById(R.id.layoutCamera);
        layoutGallery = (LinearLayout) findViewById(R.id.layoutGallery);
        imgPreview = (ImageView) findViewById(R.id.imgImagePreview);
        recyclerView = (RecyclerView) findViewById(R.id.gallery);
        gridView = (GridView) findViewById(R.id.gridview);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        listThumbnails();
        getSupportLoaderManager().initLoader(THUMBNAIL_LOADER_ID, null, this);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                thumbCursor.moveToFirst();
                thumbCursor.move(i);
                String message = "Selected Image-"+thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
                loadSelectedImage(thumbCursor.getInt(thumbCursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID)));
                Logger.d(message);
            }
        });
        mAdapter = new SimpleCursorAdapter(
                getBaseContext(),
                R.layout.photorow,
                null,
                new String[] { "_data"} ,
                new int[] { R.id.imgPhoto},
                0
        );
        gridView.setAdapter(mAdapter);
        //listImages();

    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_next:
                Intent intent = new Intent(this, ShareToActivity.class);
                intent.putExtra("photo",selectedPhoto.getPath());
                intent.putExtra("type",type);
                startActivityForResult(intent, 101);

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
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }
        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };
    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Toast.makeText(PhotoActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
            createCameraPreview();
        }
    };
    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    String capturedImageName = "";
    protected void takePicture() {

        if(null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            capturedImageName = "Instaclick"+ Utils.TimestampTotDate((int) (System.currentTimeMillis()),"yyyymmddHHmmss") +".jpg";
            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/"+ capturedImageName +".jpg");
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }
                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    //File file
                    Utils.RefreshGallery(PhotoActivity.this,file);
                    Toast.makeText(PhotoActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PhotoActivity.this, ShareToActivity.class);
                    intent.putExtra("photo",file.getAbsolutePath());
                    intent.putExtra("type",type);
                    startActivityForResult(intent, 101);
                    createCameraPreview();
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback(){
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(PhotoActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PhotoActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }
    protected void updatePreview() {
        if(null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(PhotoActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }
    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        //closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void listImages() {
        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA
        };

// content:// style URI for the "primary" external storage volume
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

// Make the query.
        Cursor cur = managedQuery(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                MediaStore.Images.Media.DATE_TAKEN        // Ordering
        );

        Log.i("ListingImages"," query count=" + cur.getCount());

        if (cur.moveToFirst()) {
            String bucket;
            String date;
            String displayName;
            String path;
            String id;
            String thumbnail ="";

            int bucketColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dateColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);
            int displayNameColumn = cur.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
            int pathColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);
            int idColumn = cur.getColumnIndex(MediaStore.Images.Media._ID);
            do {
                Photo photo = new Photo();
                // Get the field values
                bucket = cur.getString(bucketColumn);
                date = cur.getString(dateColumn);
                displayName = cur.getString(displayNameColumn);
                path = cur.getString(pathColumn);

                id = cur.getString(idColumn);
/*
                Cursor mCursor = getContentResolver()
                        .query(
                                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                                null,
                                MediaStore.Images.Thumbnails.IMAGE_ID + "=?" ,
                                new String[]{id},
                                null);
                mCursor.moveToFirst();
                while (!mCursor.isAfterLast()){
                    Log.d(TAG, "  - Thumbnail File Path : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA)));

                    //Log.d(TAG, "  - Thumbnail Type : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Thumbnails.KIND));
                    thumbnail = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                    mCursor.moveToNext();

                }
                mCursor.close();
*/

                // Do something with the values.
                Log.i("ListingImages", " bucket=" + bucket
                        + "  date_taken=" + date + " disply_name= "+ displayName +" path="+ path);
                photo.setBucketName(bucket);
                photo.setDisplayName(displayName);
                photo.setDateTaken(date);
                photo.setPath(path);
                photo.setThumbnail(thumbnail);
                photoList.add(photo);

            } while (cur.moveToNext());

        }

        recyclerView = (RecyclerView) findViewById(R.id.gallery);
        int numberOfColumns = 4;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        GalleryAdapter adapter = new GalleryAdapter(this, photoList);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        showSelectedPic(position);

                        // TODO Handle item click
                    }
                })
        );
        recyclerView.setAdapter(adapter);
        if(photoList.size()>0) {
            showSelectedPic(0);
        }
    }
    RecordAdapter adapter;
    private void listThumbnails() {

        mMatrixCursor = new MatrixCursor(new String[]{"_id","_data","_details"});
         adapter = new RecordAdapter(this,mMatrixCursor);

        mAdapter = new SimpleCursorAdapter(
                getBaseContext(),
                R.layout.photorow,
                null,
                new String[] { "_data"} ,
                new int[] { R.id.imgPhoto},
                0
        );
        //getSupportLoaderManager().initLoader(THUMBNAIL_LOADER_ID, null, this);


        int numberOfColumns = 4;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setAdapter(adapter);
    }


    Photo selectedPhoto;
    ImageView imgPreview;
    public void showSelectedPic(int position) {
        selectedPhoto = photoList.get(position);
        File imgFile = new File(selectedPhoto.getPath());
        Picasso.with(PhotoActivity.this).load(imgFile).into(imgPreview);
    }

    /** A callback method invoked by the loader when initLoader() is called */
    @Override
    public Loader<Cursor> onCreateLoader(int loader_id, Bundle arg1) {
        Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;

        /*return new CursorLoader(
                this,
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                mProjection,
                MediaStore.Images.Thumbnails.KIND + " = " + MediaStore.Images.Thumbnails.MINI_KIND +
                        " AND " + MediaStore.Images.Thumbnails.IMAGE_ID + " IN ("+ TextUtils.join(",", ids)+")",
                selectionArgs,
                MediaStore.Images.Thumbnails.IMAGE_ID + " DESC"
        );*/
        Cursor imagesCursor;

        imagesCursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );

        String[] imageIds = new String[imagesCursor.getCount()];
        int i = 0;
        while (imagesCursor.moveToNext()) {
            imageIds[i++] = imagesCursor.getString(imagesCursor.getColumnIndex(MediaStore.Images.Media._ID));
        }
        return new CursorLoader(
                this,
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Images.Thumbnails.KIND + " = " + MediaStore.Images.Thumbnails.MINI_KIND +
                        " AND " + MediaStore.Images.Thumbnails.IMAGE_ID + " IN ("+TextUtils.join(",", imageIds)+")",
                null,
                MediaStore.Images.Thumbnails.IMAGE_ID + " DESC"
        );
        //return new CursorLoader(this, uri, null, null, null, MediaStore.Images.Thumbnails._ID + " desc");

    }
    Cursor thumbCursor;
    /** A callback method, invoked after the requested content provider returned all the data */
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {

        mAdapter.swapCursor(arg1);
        thumbCursor = arg1;
        arg1.moveToFirst();
        do{
            Logger.d("Thumb path--"+arg1.getString(1)+"-");
        } while (arg1.moveToNext());
/*
        if(arg0.getId()==THUMBNAIL_LOADER_ID){  */
/** Thumbnail cursor is loaded completely *//*

            mThumbCursor = arg1;
            int length = mThumbCursor.getCount();
            Logger.d("Cursor Length - "+ length);
            if(mThumbCursor.moveToFirst()){ */
/** Taking the first thumbnail *//*


                mThumbImageId = mThumbCursor.getString(mThumbCursor.getColumnIndex(MediaStore.Images.Thumbnails._ID));
                mThumbImageData = mThumbCursor.getString(mThumbCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));

                */
/** Getting the image id from the mThumbCursor and putting in to the bundle*//*

                String image_id = mThumbCursor.getString(mThumbCursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
                Bundle data = new Bundle();
                data.putString("image_id", image_id);

                */
/** Intiates the Image Loader onCreateLoader() *//*

                getSupportLoaderManager().initLoader(IMAGE_LOADER_ID, data, this);

            }

        }else if(arg0.getId() == IMAGE_LOADER_ID){
            mImageCursor = arg1;

            if(mImageCursor.moveToFirst()){
                mImageTitle = mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                mImageSize = mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                mImageWidth = mThumbCursor.getString(mThumbCursor.getColumnIndex(MediaStore.Images.Thumbnails.WIDTH));
                mImageHeight = mThumbCursor.getString(mThumbCursor.getColumnIndex(MediaStore.Images.Thumbnails.HEIGHT));

                String details =    "Title:"+mImageTitle + "\n" +
                        "Size:" + mImageSize + " Bytes " + "\n" +
                        "Resolution:" + mImageWidth + " x " + mImageHeight ;

                */
/** Adding new row to the matrixcursor object *//*

                mMatrixCursor.addRow(new Object[]{ mThumbImageId,mThumbImageData, details });

                */
/** Taking the next thumbnail *//*

                if(mThumbCursor.moveToNext()){

                    mThumbImageId = mThumbCursor.getString(mThumbCursor.getColumnIndex(MediaStore.Images.Thumbnails._ID));
                    mThumbImageData = mThumbCursor.getString(mThumbCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));

                    String image_id = mThumbCursor.getString(mThumbCursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
                    Bundle data = new Bundle();
                    data.putString("image_id", image_id);

                    */
/** Restarting the image loader to get the next image details *//*

                    getSupportLoaderManager().restartLoader(IMAGE_LOADER_ID, data, this);

                }else{ */
/** No more thumbnails exists *//*

                    if(mThumbCursor.isAfterLast())
                        adapter.swapCursor(mMatrixCursor); */
/** Set the thumbnails and its details in the listview *//*

                }
            }
        }
*/
        //listThumbnails();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }

    private void loadSelectedImage(int position) {
        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA
        };

// content:// style URI for the "primary" external storage volume
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] args = { String.valueOf(position) };
// Make the query.
        Cursor cur = managedQuery(images,
                projection, // Which columns to return
               // null,
                MediaStore.Images.Media._ID + "=?",
                // MediaStore.Images.Media._ID + "=" + position + "",       // Which rows to return (all rows)
                args,       // Selection arguments (none)
                MediaStore.Images.Media.DATE_TAKEN        // Ordering
        );

        Logger.i("ListingImages"," query count=" + cur.getCount());

        cur.moveToFirst();
        do{
            String bucket;
            String date;
            String displayName;
            String path;
            String id;
            String thumbnail = "";

            int bucketColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dateColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);
            int displayNameColumn = cur.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
            int pathColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);
            int idColumn = cur.getColumnIndex(MediaStore.Images.Media._ID);

            Photo photo = new Photo();
            // Get the field values
            bucket = cur.getString(bucketColumn);
            date = cur.getString(dateColumn);
            displayName = cur.getString(displayNameColumn);
            path = cur.getString(pathColumn);

            id = cur.getString(idColumn);

            Log.i("ListingImages", " bucket=" + bucket
                    + "  date_taken=" + date + " disply_name= "+ displayName +" path="+ id);
            photo.setBucketName(bucket);
            photo.setDisplayName(displayName);
            photo.setDateTaken(date);
            photo.setPath(path);
            photo.setThumbnail(thumbnail);

        }while (cur.moveToNext());

        if (cur.moveToFirst()) {
            String bucket;
            String date;
            String displayName;
            String path;
            String id;
            String thumbnail = "";

            int bucketColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dateColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);
            int displayNameColumn = cur.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
            int pathColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);
            int idColumn = cur.getColumnIndex(MediaStore.Images.Media._ID);

            Photo photo = new Photo();
            // Get the field values
            bucket = cur.getString(bucketColumn);
            date = cur.getString(dateColumn);
            displayName = cur.getString(displayNameColumn);
            path = cur.getString(pathColumn);

            id = cur.getString(idColumn);

            Log.i("ListingImages", " bucket=" + bucket
                    + "  date_taken=" + date + " disply_name= "+ displayName +" path="+ path);
            photo.setBucketName(bucket);
            photo.setDisplayName(displayName);
            photo.setDateTaken(date);
            photo.setPath(path);
            photo.setThumbnail(thumbnail);

            selectedPhoto = photo;

            File imgFile = new File(selectedPhoto.getPath());
            Picasso.with(PhotoActivity.this).load(imgFile).into(imgPreview);

        }

    }
}
