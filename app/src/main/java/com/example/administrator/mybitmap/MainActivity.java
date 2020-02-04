package com.example.administrator.mybitmap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private ImageView iv1, iv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);

        /**
         * 图片的压缩
         */

        //1尺寸的压缩---》优化存储大小 和内存大小
        Bitmap bitmap = scaleBitmap(getResources(), R.mipmap.d, 150, 150);
        iv1.setImageBitmap(bitmap);
        iv2.setImageBitmap(bitmap);

        //2质量的压缩 --》优化存储大小 不能优化内存大小
        Bitmap bitmap1 = qualityCompress(bitmap, Bitmap.CompressFormat.JPEG, 50);


        //3格式的压缩---》优化存储大小 和内存大小
        // 安卓常用的图片格式 jpeg  png  webp
        //jpeg 兼容最后是有损压缩 占用内存第二
        //png 无损压缩 彩色好 占用内存最多
        //webp 支持无损和有损压缩  安卓4.0才支持，不考虑兼容问题 建议使用webp 有损压缩占用内存最小 无损压缩保证色彩前提比png占用内存也少（京东项目使用）


        /**
         * 色彩模式
         *
         * 图片由像素组成，像素占用的内存由像素的色彩模式决定
         *
         * 常用是色彩模式有ARGB888 ARGB444 RGB56
         *
         * ARGB888 32位 4个字节
         *
         * ARG444 16位 2个字节 兼容性
         *
         * RGB565 16位 2个字节 兼容性好  建议使用（腾讯内部建议使用的色彩模式）
         *
         * 使用rgb565 在兼容性前提下 内存省50%
         *
         */

        Bitmap copy = bitmap.copy(Bitmap.Config.RGB_565, true);


        /**
         * 图片的缓存
         * 展示图片时 经常需要重新加载图片，对于重复加载的图片
         * 如果使用缓存可以高性能
         *
         * 三级缓存  我们加载一张图片 先从内存缓存找 内存缓存没有再到磁盘缓存  磁盘缓存没有再到网络
         *
         * 内存缓存可以使用对象池+LRU算法-->LruCache
         * 考虑8.0后图片回收是放到native层，
         * 我们可以使用弱引用+引用队列 对没有设置可复用的bitmap直接回收，
         * 设置复用的bitmap使用弱引用放到缓存容器（加锁的set容器）  当引用队列有bitmap 并且不为空 我们就加速bitmap的释放
         * 没有被放到引用队列的 就还可以被复用内存
         *
         *
         *
         * 磁盘缓存 disklrucache  才有对象池+lru ；如果内存缓存没有就到磁盘缓存
         *
         * 网络缓存
         *
         *
         *
         * 加载图片先到内存缓存-内存缓存没有到---》弱引用的缓存容器---》磁盘缓存--》磁盘缓存有就加载显示并且保存到内存缓存--》没有就到网络加载
         *
         */


        /**
         * 长图优化加载
         * 使用 BitmapRegionDecoder 核心类可以加载图片局部信息图像 并且开启内存复用， 再结合滑动监听 监听滑动时需要显示的图片显示，这样就只需要申请屏幕大小内存就可以显示长图内容
         *
         */




    }


    /**
     * 尺寸压缩
     *
     * @param resources 资源
     * @param id        资源id
     * @param newWidth  压缩后的宽度
     * @param newHeight 压缩后的高度
     * @return 压缩后的Btmap
     */
    private Bitmap scaleBitmap(Resources resources, int id, int newWidth, int newHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //开始读入图片 获取的bitmap才可以对属性操作，但是bitmap不能加载到ui控件
        options.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeResource(resources, id);

        //关闭读入图片 （耗性能 获取到bitmap就关闭）
        options.inJustDecodeBounds = false;

        //获取读入图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //缩放比例  旧的比新的  宽高缩放比值 取大的
        int sacle = (int) (width / newWidth > height / newHeight ? width / newWidth : height / newHeight);
        //只对图片缩小 不放大处理
        sacle = sacle <= 0 ? 1 : sacle;

        //设置缩放比例值
        options.inSampleSize = sacle;

        //重新读入图片 ，这时injustDecodeBound是关闭的获取的bitmap可以加载到UI上面
        bitmap = BitmapFactory.decodeResource(resources, id, options);

        return bitmap;
    }


    /**
     * 质量压缩
     *
     * @param bitmap         原图
     * @param compressFormat 图片类型：JPEG,PNG,WEBP;
     * @param quality        质量 0--100 0表示压缩最小  100表示压缩质量最大 一般50左右
     * @return 压缩后的Bitmap
     * <p>
     * 7.0之前 安卓哈夫曼算法关闭 压缩性能很低 可以自己集合libjpeg 开启哈夫曼算法
     */
    public static Bitmap qualityCompress(Bitmap bitmap, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(compressFormat, quality, baos);
        byte[] bytes = baos.toByteArray();
        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bm;
    }


    /**
     * 设置图片色彩模式
     *
     * @param bitmap 位图
     * @param config 色彩模式，常用的：RGB_565,ARGB_4444ARGB_8888。
     * @return 返回新图片
     */

    public Bitmap setConfig(Bitmap bitmap, Bitmap.Config config) {
        if (bitmap == null) {
            return null;
        }
        Bitmap newBitmap = bitmap.copy(config, true);
        long size = newBitmap.getByteCount();//图片大小
        return newBitmap;
    }

}
