package com.dr.yokohamarally.activities;


import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.dr.yokohamarally.R;
import com.dr.yokohamarally.fragments.BitmapHolder;

public class CameraActivity extends Activity implements OnClickListener {

    private static final int IMAGE_CAPTURE = 101;
    private static int GETTRIM = 10;
    private static final String KEY_IMAGE_URI = "KEY_IMAGE_URI";

    private Uri mImageUri;
    private Bitmap imageBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);

        // ボタンの設定
        Button cameraBtn = (Button) findViewById(R.id.camera_button);
        cameraBtn.setOnClickListener(this);
    }

    /**
     * カメラ起動ボンタン押下時の処理
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.camera_button) {
            mImageUri = getPhotoUri();
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(intent, IMAGE_CAPTURE);
        }
    }

    /**
     * カメラから戻ってきた時の処理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                setImageView();


                try {
                    ContentResolver contentResolver = getContentResolver();
                    InputStream inputStream;
                    BitmapFactory.Options imageOptions;

                    // メモリ上に画像を読み込まず、画像サイズ情報のみを取得する
                    inputStream = contentResolver.openInputStream(mImageUri);
                    imageOptions = new BitmapFactory.Options();
                    imageOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(inputStream, null, imageOptions);
                    inputStream.close();

                    // もし読み込む画像が大きかったら縮小して読み込む
                    inputStream = contentResolver.openInputStream(mImageUri);
                    if (imageOptions.outWidth > 2048 && imageOptions.outHeight > 2048) {
                        imageOptions = new BitmapFactory.Options();
                        imageOptions.inSampleSize = 2;
                        imageBitmap = BitmapFactory.decodeStream(inputStream, null, imageOptions);
                    } else {
                        imageBitmap = BitmapFactory.decodeStream(inputStream, null, null);
                    }
                    inputStream.close();
                } catch (Exception e) {}


                // 回転マトリックス作成（90度回転）
                Matrix mat = new Matrix();
                mat.postRotate(90);

                // 回転したビットマップを作成
                Bitmap bmp = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), mat, true);

                Intent _intent = new Intent(getApplicationContext(),TrimingActivity.class);
                BitmapHolder._holdedBitmap = bmp;
                startActivityForResult(_intent,GETTRIM);

            }

        }

        if(requestCode == GETTRIM){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "トリミングに成功しました", Toast.LENGTH_SHORT).show();
                ImageView imageView = (ImageView)findViewById(R.id.camera_image);
                imageView.setImageBitmap(BitmapHolder._holdedBitmap);
            }else{
                Toast.makeText(this, "画像の取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);


    }

    /**
     * 状態を保持する
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_IMAGE_URI, mImageUri);
    }

    /**
     * 保持した状態を元に戻す
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageUri = (Uri) savedInstanceState.get(KEY_IMAGE_URI);
        setImageView();
    }

    /**
     * ImageViewに画像をセットする
     */
    private void setImageView() {
        ImageView imageView = (ImageView)findViewById(R.id.camera_image);
        imageView.setImageURI(mImageUri);

    }

    /**
     * 画像のディレクトリパスを取得する
     *
     * @return
     */
    private String getDirPath() {
        String dirPath = "";
        File photoDir = null;
        File extStorageDir = Environment.getExternalStorageDirectory();
        if (extStorageDir.canWrite()) {
            photoDir = new File(extStorageDir.getPath() + "/" + getPackageName());
        }
        if (photoDir != null) {
            if (!photoDir.exists()) {
                photoDir.mkdirs();
            }
            if (photoDir.canWrite()) {
                dirPath = photoDir.getPath();
            }
        }
        return dirPath;
    }

    /**
     * 画像のUriを取得する
     *
     * @return
     */
    private Uri getPhotoUri() {
        long currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String title = dateFormat.format(today);
        String dirPath = getDirPath();
        String fileName = "samplecameraintent_" + title + ".jpg";
        String path = dirPath + "/" + fileName;
        File file = new File(path);
        ContentValues values = new ContentValues();
        values.put(Images.Media.TITLE, title);
        values.put(Images.Media.DISPLAY_NAME, fileName);
        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        values.put(Images.Media.DATA, path);
        values.put(Images.Media.DATE_TAKEN, currentTimeMillis);
        if (file.exists()) {
            values.put(Images.Media.SIZE, file.length());
        }
        Uri uri = getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
        return uri;
    }
}