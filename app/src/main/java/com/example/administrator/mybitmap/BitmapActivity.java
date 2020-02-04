package com.example.administrator.mybitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapActivity extends AppCompatActivity {
private ImageView iv1,iv2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);
        iv1=findViewById(R.id.iv1);
        iv2=findViewById(R.id.iv2);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.tp);
        iv1.setImageBitmap(bitmap);

        /**
         * 尺寸压缩
         *
         * 压缩后44kb
         */
        //filter 图片滤波处理 色彩更丰富
//        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
//        compress(scaledBitmap, Bitmap.CompressFormat.JPEG,100,Environment.getExternalStorageDirectory()+"/test_scaled.jpeg2");
//
//        File file=new File(Environment.getExternalStorageDirectory()+"/test_scaled.jpeg2");
//
//        Bitmap bitmap1 = BitmapFactory.decodeFile(file.getAbsolutePath());
//
//        iv2.setImageBitmap(bitmap1);



        /**
         * 质量压缩
         * 集成libjpeg库  开启哈夫曼算法 性能优化更大
         * https://libjpeg-turbo.org/
         *
         * 没开启 优化后占用存储大小23kb
         * 开启后大概十几kb
         */
//        compress(bitmap, Bitmap.CompressFormat.JPEG,50,Environment.getExternalStorageDirectory()+"/test_q.jpeg");




        /**
         * 格式转换压缩
         * 压缩webp后 106kb
         * 压缩png后 400kb
         * 压缩JPG后 239kb
         *
         */
        //        //jpg格式
        compress(bitmap, Bitmap.CompressFormat.JPEG,100,Environment.getExternalStorageDirectory()+"/test.jpg");
//        //png格式
//        compress(bitmap, Bitmap.CompressFormat.PNG,100,Environment.getExternalStorageDirectory()+"/test.png");
//        //webp格式
//        compress(bitmap, Bitmap.CompressFormat.WEBP,100,Environment.getExternalStorageDirectory()+"/test.webp");





//        //综合使用 根据需要使用合理尺寸 压缩质量 格式选择webp
//        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
//        compress(scaleBitmap,Bitmap.CompressFormat.WEBP,50,Environment.getExternalStorageDirectory()+"/ts.webp");
    }


    /**
     * 压缩图片到制定文件
     * @param bitmap 待压缩图片
     * @param format 压缩的格式
     * @param q      质量
     * @param path  文件地址
     */
    private void compress(Bitmap bitmap, Bitmap.CompressFormat format, int q, String path) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            bitmap.compress(format,q,fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (null != fos){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
