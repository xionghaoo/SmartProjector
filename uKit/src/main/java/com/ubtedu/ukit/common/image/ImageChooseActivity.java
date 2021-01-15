/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.image;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.ubtedu.alpha1x.ui.recyclerview.CommonAdapter;
import com.ubtedu.alpha1x.ui.recyclerview.base.ViewHolder;
import com.ubtedu.alpha1x.ui.recyclerview.item.SpaceItemDecoration;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.permission.PermissionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ubtedu.ukit.common.image.ImageCropActivity.CROP_CONFIG_KEY;
import static com.ubtedu.ukit.common.image.ImageCropActivity.REQUEST_CODE_CROP_IMAGE;

/**
 * @Author qinicy
 * @Date 2018/11/15
 **/
public class ImageChooseActivity extends UKitBaseActivity {
    public static final int REQUEST_CODE_CHOOSE_IMAGE = 10;

    private static final int REQUEST_CODE_CAPTURE_CAMERA = 999;
    public static final String IMAGE_CAPTURE_NAME = "project_image";
    public static final String PHOTO_PATH = FileHelper.getFileRootPath() + "/photo/";
    private static final String EXT_IMAGE_BASE_URI = "content://media/external/images/media";
    public final String FILE_AUTHORITY = "com.ubtedu.ukit.fileProvider";
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private List<Uri> mImagePaths;
    private String mImagePath;
    private View mBackBtn;
    private ImageCropConfig mCropConfig;
    private Uri mImageUri;
    public static final int REQUEST_PERMISSION_CODE = 38;
    private static final String[] mRequiredPermissions = new String[]{Manifest.permission.CAMERA};

    public static void open(Fragment fragment, ImageCropConfig config) {
        Intent intent = new Intent(fragment.getContext(), ImageChooseActivity.class);
        intent.putExtra(CROP_CONFIG_KEY, config);
        fragment.startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCropConfig = (ImageCropConfig) getIntent().getSerializableExtra(CROP_CONFIG_KEY);
    }

    @Override
    protected void onInitViews() {
        super.onInitViews();
        setContentView(R.layout.activity_project_image_choose);
        mBackBtn = findViewById(R.id.project_image_choose_back_btn);
        bindSafeClickListener(mBackBtn);
        mRecyclerView = findViewById(R.id.project_image_choose_rcv);
        mImagePaths = getImagesPath(this);

        mAdapter = new ImageAdapter(this, R.layout.item_project_image_choose, mImagePaths);
        mRecyclerView.setAdapter(mAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_14px)));
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(View v, boolean isSafeClick) {
        super.onClick(v, isSafeClick);
        if (v == mBackBtn) {
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAPTURE_CAMERA && resultCode == RESULT_OK) {
            ImageCropActivity.open(this, mImageUri, mCropConfig);
        }
        if (requestCode == REQUEST_CODE_CROP_IMAGE && resultCode == RESULT_OK && data != null) {
            String image = (String) data.getExtras().get(ImageCropActivity.IMAGE_PATH_KEY);
            if (image != null) {
                File file = new File(image);
                if (file.exists() && file.length() > 0) {
                    mImagePath = image;
                    onImageChoose();
                }
            }
        }
    }


    private void openCamera() {
        if (!PermissionUtil.isPermissionsGranted(this, mRequiredPermissions)) {
            PermissionUtil.requestPermissions(this, mRequiredPermissions, REQUEST_PERMISSION_CODE);
            return;
        }
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mImagePath = PHOTO_PATH + System.currentTimeMillis() + ".png";

        File photoFile = new File(mImagePath);
        photoFile.getParentFile().mkdirs();
        mImageUri = FileProvider.getUriForFile(this, FILE_AUTHORITY, photoFile);
        it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        it.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(it, REQUEST_CODE_CAPTURE_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            List<String> deniedPermissions = PermissionUtil.getDeniedPermissions(permissions, grantResults);
            if (permissions.length != 0 && deniedPermissions.size() == 0) {
                openCamera();
            } else {
                PermissionUtil.showDeniedDialog(this, deniedPermissions, REQUEST_PERMISSION_CODE, null);
            }
        }
    }


    public static ArrayList<Uri> getImagesPath(Context activity) {
        ArrayList<Uri> listOfAllImages = new ArrayList<>();
        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        Cursor cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        if (cursor != null) {
            Uri base = Uri.parse(EXT_IMAGE_BASE_URI);
            int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
            while (cursor.moveToNext()) {
                int ringtoneID = cursor.getInt(columnIndexData);
                Uri imageUri = Uri.withAppendedPath(base, "" + ringtoneID);
                listOfAllImages.add(imageUri);
            }
            cursor.close();
        }
        Collections.reverse(listOfAllImages);
        listOfAllImages.add(0, Uri.EMPTY);
        return listOfAllImages;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            if (position == 0) {
                openCamera();
            } else {
                if (position >= 0 && position < mImagePaths.size()) {
                    Uri path = mImagePaths.get(position);
                    if (path != null) {
                        ImageCropActivity.open(ImageChooseActivity.this, path, mCropConfig);
                    }
                }
            }
        }
    };

    private void onImageChoose() {
        Intent data = new Intent();
        data.putExtra(ImageCropActivity.IMAGE_PATH_KEY, mImagePath);
        setResult(RESULT_OK, data);
        finish();
    }

    class ImageAdapter extends CommonAdapter<Uri> {
        private final DrawableCrossFadeFactory mDrawableCrossFadeFactory;
        private RequestOptions requestOptions;

        public ImageAdapter(Context context, int layoutId, List<Uri> datas) {
            super(context, layoutId, datas);
            mDrawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build();
            requestOptions = new RequestOptions()
                    .transforms(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.ubt_dimen_60px)));
        }

        @Override
        protected void convert(ViewHolder holder, Uri s, int position) {
            if (s == null) {
                return;
            }
            LogUtil.d(s.toString());
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.itemView.setTag(position);
            if (position == 0) {
                holder.setImageDrawable(R.id.item_project_image_choose_iv, ContextCompat.getDrawable(getContext(), R.drawable.keep_photo_icon));
            } else {
                Glide.with(getContext())
                        .load(s)
                        .transition(DrawableTransitionOptions.with(mDrawableCrossFadeFactory))
                        .apply(requestOptions)
                        .into((ImageView) holder.getView(R.id.item_project_image_choose_iv));
            }
        }

    }
}
