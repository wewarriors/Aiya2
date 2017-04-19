package com.example.wewarriors.aiya;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wewarriors.aiya.bean.ImagePathItem;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PhotoAlbumActivity extends AppCompatActivity {



    @InjectView(R.id.tv_back)
    TextView mTvBack;
    @InjectView(R.id.tv_photo_edit)
    TextView mTvGiftEdit;
    @InjectView(R.id.rv_photo_album)
    RecyclerView mRvPhotoAlbum;

    private List<String> mLtTime;
    private List<List<String>> mLtAlbum;

    public static final int TAKE_PHOTO = 1;

    public static final int CHOOSE_PHOTO = 2;



    private Uri imageUri;


    ArrayList<String> photoList;

    Handler handler;

    private SQLiteDatabase db;
    private String outputImagePath;

    String dateNow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);
        ButterKnife.inject(this);
        mRvPhotoAlbum.setAdapter(new RvAlbumAdapter());
        mRvPhotoAlbum.setLayoutManager(new LinearLayoutManager(this));
        initData();
    }

    private void initData() {
        mLtTime = new ArrayList<>();
        mLtAlbum = new ArrayList<>();
        db = Connector.getDatabase();
        List<ImagePathItem> list;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        dateNow = df.format(new Date());
        System.out.println("DataSupport.isExist(ImagePathItem.class)"+DataSupport.isExist(ImagePathItem.class));
        if(DataSupport.isExist(ImagePathItem.class)){
            list = DataSupport.findAll(ImagePathItem.class);
            System.out.println("initdata"+list.size());
            for(ImagePathItem imagePathItem:list){
                String date = imagePathItem.getDate();
                List<String> path = imagePathItem.getImagePath();
                if(!date.equals(dateNow)){
                    path.remove(R.drawable.uploadpic_226x226+"");
                    if(path.size()==0){
                        continue;
                    }
                }
                mLtTime.add(imagePathItem.getDate());
                mLtAlbum.add(imagePathItem.getImagePath());
                System.out.println("imagePathItem.getImagePath()"+imagePathItem.getImagePath());
            }
            DataSupport.deleteAll(ImagePathItem.class);
        }


        //将数据库中保存的数据放到mLtTime和mLtAlbum


        if(mLtTime.size()==0){
            mLtTime.add(0,dateNow);
            photoList = new ArrayList<>();
            photoList.add(R.drawable.uploadpic_226x226+"");
            mLtAlbum.add(0,photoList);
        }else {
            if(!mLtTime.get(0).equals(dateNow)){
                mLtTime.add(0,dateNow);
                photoList = new ArrayList<>();
                photoList.add(R.drawable.uploadpic_226x226+"");
                mLtAlbum.add(0,photoList);
            }
        }




    }

    @Override
    protected void onDestroy() {
        List<ImagePathItem> list = new ArrayList<>();
        for(int i=0;i<mLtTime.size();i++){
            ImagePathItem imagePathItem = new ImagePathItem();
            imagePathItem.setDate(mLtTime.get(i));
            imagePathItem.setImagePath(mLtAlbum.get(i));
            System.out.println("imagePathItem.setImagePath(mLtAlbum.get(i))"+mLtAlbum.get(i));
            list.add(imagePathItem);
        }
        DataSupport.saveAll(list);

        System.out.println("OnDestroy"+list.size());
        System.out.println("DataSupport.isExist(ImagePathItem.class)"+DataSupport.isExist(ImagePathItem.class));
        super.onDestroy();
    }

    @OnClick({R.id.tv_back, R.id.tv_photo_edit})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_back:
                intent = new Intent(PhotoAlbumActivity.this,MainActivity.class);
                intent.putExtra("Flag","Me");
                startActivity(intent);
                finish();
                break;
            case R.id.tv_photo_edit:
                break;
        }
    }


    class RvAlbumAdapter extends RecyclerView.Adapter<RvAlbumAdapter.AlbumViewHolder>{

        @Override
        public RvAlbumAdapter.AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(PhotoAlbumActivity.this).inflate(R.layout.photo_album_item,parent,false);
            AlbumViewHolder albumViewHolder = new AlbumViewHolder(view);
            return albumViewHolder;
        }

        @Override
        public void onBindViewHolder(RvAlbumAdapter.AlbumViewHolder holder, final int position1) {

            holder.mTvDay.setText(mLtTime.get(position1));
            GridViewAdapter adapter = new GridViewAdapter(PhotoAlbumActivity.this,R.layout.grid_view_item,mLtAlbum.get(position1));
            adapter.date = mLtTime.get(position1);
            holder.mGvPhoto.setAdapter(adapter);
            holder.mGvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    String i = R.drawable.uploadpic_226x226+"";
                    String j = mLtAlbum.get(position1).get(position);

                    if(position1==0&&position==mLtAlbum.get(position1).size()-1&&i.equals(j)){

                        //此时增加图片
                        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoAlbumActivity.this);

                        //    指定下拉列表的显示数据
                        final String[] camera = {"相机", "相册"};
                        //    设置一个下拉的列表选择项
                        builder.setItems(camera, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                switch (which){
                                    case 0:
                                        openCameraStart();


                                        break;
                                    case 1:
                                        openAlbumStart();



                                        break;
                                    default:
                                        break;

                                }
                                dialog.dismiss();
                            }
                        });

                        builder.show();


                    }else{
                        //此时查看大图
                    }

                    handler = new Handler(){
                        public void handleMessage(android.os.Message msg) {
                            notifyDataSetChanged();
                        }
                    };

                }



            });
        }

        @Override
        public int getItemCount() {
            return mLtTime.size();
        }

        class AlbumViewHolder extends RecyclerView.ViewHolder{

            private TextView mTvDay;
            private GridView mGvPhoto;
            public AlbumViewHolder(View itemView) {
                super(itemView);
                mTvDay = (TextView) itemView.findViewById(R.id.tv_day);
                mGvPhoto = (GridView) itemView.findViewById(R.id.grid_photo_album);

            }
        }
        class GridViewAdapter extends ArrayAdapter<String>{

            private Context mContext;
            private int layoutResourceId;
            private List<String> mGridData = new ArrayList<>();
            String date;


            public GridViewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
                super(context, resource, objects);
                this.mContext = context;
                this.layoutResourceId = resource;
                this.mGridData = objects;

            }



            public void setGridData(ArrayList<String> mGridData){
                this.mGridData = mGridData;
                notifyDataSetChanged();
            }

            @Override
            public int getCount() {
                if(mGridData.size()>9){
                    return 9;
                }else{
                    return mGridData.size();
                }

            }


            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                ViewHolder holder;

                if (convertView == null) {
                    LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
                    convertView = inflater.inflate(layoutResourceId, parent, false);
                    holder = new ViewHolder();
                    holder.imageView = (ImageView) convertView.findViewById(R.id.imgview_item);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                String item = mGridData.get(position);

                if(position==mGridData.size()-1&&date.equals(dateNow)){
                    holder.imageView.setImageResource(Integer.parseInt(item));

                }else{
                    Bitmap bitmap = BitmapFactory.decodeFile(item);
                    holder.imageView.setImageBitmap(bitmap);
                }

                return convertView;
            }

            private class ViewHolder{
                ImageView imageView;
            }
        }
    }






    private void openAlbumStart(){
        if (ContextCompat.checkSelfPermission(PhotoAlbumActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PhotoAlbumActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
        } else {
            openAlbum();
        }
    }
    private void openCameraStart(){
        if (ContextCompat.checkSelfPermission(PhotoAlbumActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PhotoAlbumActivity.this, new String[]{ Manifest.permission. CAMERA }, 2);
        } else {
            openCamera();
        }

    }
    private void openCamera() {
        // 创建File对象，用于存储拍照后的图片
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        outputImagePath = outputImage.getPath();


        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < 24) {
            imageUri = Uri.fromFile(outputImage);
        } else {
            imageUri = FileProvider.getUriForFile(PhotoAlbumActivity.this, "com.example.Aiya.fileprovider", outputImage);
        }
        // 启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }


    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    mLtAlbum.get(0).add(0,outputImagePath);
                    handler.sendEmptyMessage(0);
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        //在这里可以将imagePath保存起来


        AddImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        AddImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void AddImage(String imagePath) {
        mLtAlbum.get(0).add(0,imagePath);
        handler.sendEmptyMessage(0);
    }
}
