package com.sunshanglei.camera.system;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * use 调用系统相机
 * author 孙尚磊
 * create time 2017-4-22
 */
public class MainActivity extends AppCompatActivity {
    private Button btn_original,btn_small,btn_square;
    private static final int SMALL_CAMERA=1;//压缩图请求码
    private static final int ORIGINAL_CAMERA=2;//原图请求码
    private static final int SQUARE_CAMERA_PRE=3;//跳到正方形请求码
    private static final int SQUARE_CAMERA=4;//正方形请求码
    private ImageView iv;
    private String filePath;
    private File file,fileTemp;
    private String temp="temp.png",picTemp="picTemp.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_original= (Button) findViewById(R.id.btn_original);
        btn_small= (Button) findViewById(R.id.btn_small);
        btn_square= (Button) findViewById(R.id.btn_square);
        iv= (ImageView) findViewById(R.id.iv);
        btn_original.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先验证手机是否有sdcard
                String status= Environment.getExternalStorageState();
                if(!status.equals(Environment.MEDIA_MOUNTED))
                {
                    Toast.makeText(getApplicationContext(),"你的sd卡不可用。",Toast.LENGTH_SHORT).show();
                    return;
                }
                filePath=Environment.getExternalStorageDirectory().getPath()+"/temp/";
                File fileDir=new File(filePath);
                if(!fileDir.exists()) {
                    fileDir.mkdirs();
                }

                file=new File(filePath,temp);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Uri uri = Uri.fromFile(file);
                //调用系统相机
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                startActivityForResult(intent,ORIGINAL_CAMERA);
            }
        });
        btn_small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用系统相机
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,SMALL_CAMERA);
            }
        });
        btn_square.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先验证手机是否有sdcard
                String status= Environment.getExternalStorageState();
                if(!status.equals(Environment.MEDIA_MOUNTED))
                {
                    Toast.makeText(getApplicationContext(),"你的sd卡不可用。",Toast.LENGTH_SHORT).show();
                    return;
                }
                filePath=Environment.getExternalStorageDirectory().getPath()+"/temp/";
                File fileDir=new File(filePath);
                if(!fileDir.exists()) {
                    fileDir.mkdirs();
                }

                file=new File(filePath,temp);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Uri uri = Uri.fromFile(file);
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                startActivityForResult(intent,SQUARE_CAMERA_PRE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                //返回压缩的图片
                case SMALL_CAMERA:
                    if(data==null){
                        return;
                    }
                     //如果图太大会造成内存溢出(OOM),取出的图片系统会默认压缩
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    iv.setImageBitmap(bitmap);
                    break;
                //返回原图
                case ORIGINAL_CAMERA:
                    //这种方法是通过内存卡的路径进行读取图片,获取的是原图
                    FileInputStream fis = null;
                    try {
                        //把图片转化为字节流
                        fis = new FileInputStream(file);
                        //把流转化图片
                        Bitmap bitmapOriginal = BitmapFactory.decodeStream(fis);
                        iv.setImageBitmap(bitmapOriginal);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }finally{
                        try {
                            if(fis!=null)
                            fis.close();//关闭流
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    //跳到截取图片
                case SQUARE_CAMERA_PRE:
                    fileTemp=new File(filePath,picTemp);
                    if (!fileTemp.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(Uri.fromFile(file), "image/*");//要裁切的图片源
                    intent.putExtra("crop", "true");//是否要裁切
                    // aspectX aspectY 是裁剪框宽高的比例
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("scale", true);// 去黑边
                    // outputX outputY 是裁剪后生成图片的宽高
                    // intent.putExtra("outputX", 800);
                    // intent.putExtra("outputY", 800);
                    intent.putExtra("scaleUpIfNeeded", true);// 去黑边
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//返回图片的格式
                    intent.putExtra("noFaceDetection", true);
                    // return-data为true时,会直接返回bitmap数据,推荐下面为false时的方式,同上面的
                    // return-data为false时,不会返回bitmap,但需要指定一个MediaStore.EXTRA_OUTPUT保存图片uri
                    intent.putExtra("return-data", false);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileTemp));
                    startActivityForResult(intent, SQUARE_CAMERA);
                    break;
                //获取截取的图片
                case SQUARE_CAMERA:
                    //这种方法是通过内存卡的路径进行读取图片,获取的是原图
                    FileInputStream fisSquare = null;
                    try {
                        //把图片转化为字节流
                        fisSquare = new FileInputStream(fileTemp);
                        //把流转化图片
                        Bitmap bitmapOriginal = BitmapFactory.decodeStream(fisSquare);
                        iv.setImageBitmap(bitmapOriginal);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }finally{
                        try {
                            if(fisSquare!=null)
                            fisSquare.close();//关闭流
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }

    }


}
