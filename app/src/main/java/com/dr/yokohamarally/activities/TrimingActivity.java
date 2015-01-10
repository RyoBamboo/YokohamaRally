package com.dr.yokohamarally.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dr.yokohamarally.R;
import com.dr.yokohamarally.fragments.BitmapHolder;
import com.dr.yokohamarally.fragments.TrimView;

import java.util.ArrayList;

public class TrimingActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // タイトルバーの削除
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.triming);
        View view = (View)findViewById(R.id.triming_image);
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // ステータスバー削除
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        System.gc();
        _bmOriginal = BitmapHolder._holdedBitmap.copy(Bitmap.Config.ARGB_8888, true);
        BitmapHolder._holdedBitmap = null;
    }

    Bitmap _bmOriginal;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        final TrimView _tview = new TrimView(getApplicationContext());
        ((LinearLayout)findViewById(R.id.imgcontainer)).addView(_tview);
        int _width = ((FrameLayout)findViewById(R.id.fl1)).getWidth();
        int _height = ((FrameLayout)findViewById(R.id.fl1)).getHeight();

        //_bmOriginal = BitmapFactory.decodeResource(getResources(),R.drawable.temple);


        float _scaleW = (float) _width / (float) _bmOriginal.getWidth();
        float _scaleH = (float) _height / (float) _bmOriginal.getHeight();
        final float _scale = Math.min(_scaleW, _scaleH);
        Matrix matrix = new Matrix();
        matrix.postScale(_scale, _scale);

        Bitmap _bm = Bitmap.createBitmap(_bmOriginal, 0, 0, _bmOriginal.getWidth(),_bmOriginal.getHeight(), matrix, true);


        ((ImageView)findViewById(R.id.triming_image)).setImageBitmap(_bm);

        ((Button)findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ArrayList<Integer> _al = _tview.getTrimData();

                int _ix = (int)(_al.get(0)/_scale);
                int _iy = (int)(_al.get(1)/_scale);
                int _iwidth = (int)(_al.get(2)/_scale);
                int _iheight = (int)(_al.get(3)/_scale);

                _ix = (_ix>0) ? _ix : 0;
                _iy = (_iy>0) ? _iy : 0;
                _iwidth = (_iwidth + _ix < _bmOriginal.getWidth()) ? _iwidth : _bmOriginal.getWidth() - _ix;
                _iheight = (_iheight + _iy < _bmOriginal.getHeight()) ? _iheight : _bmOriginal.getHeight() - _iy;
                BitmapHolder._holdedBitmap = Bitmap.createBitmap(_bmOriginal, _ix, _iy, _iwidth, _iheight, null, true);
                setResult(RESULT_OK);
                finish();
                //((ImageView)findViewById(R.id.imageView1)).setImageBitmap(_bm);
            }
        });

        super.onWindowFocusChanged(hasFocus);
        _tview.sizeSet((int)(_bmOriginal.getWidth()*_scale),(int)(_bmOriginal.getHeight()*_scale));
    }
}