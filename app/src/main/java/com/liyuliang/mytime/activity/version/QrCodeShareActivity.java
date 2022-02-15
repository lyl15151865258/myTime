package com.liyuliang.mytime.activity.version;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.base.SwipeBackActivity;
import com.liyuliang.mytime.bean.User;
import com.liyuliang.mytime.contentprovider.SPHelper;
import com.liyuliang.mytime.network.NetClient;
import com.liyuliang.mytime.utils.ActivityController;
import com.liyuliang.mytime.utils.BitmapUtils;
import com.liyuliang.mytime.utils.GsonUtils;
import com.liyuliang.mytime.utils.ZXingUtils;
import com.liyuliang.mytime.widget.MyToolbar;

import java.io.File;

/**
 * 二维码分享页面
 * Created at 2019/3/1 11:16
 *
 * @author Li Yuliang
 * @version 1.0
 */

public class QrCodeShareActivity extends SwipeBackActivity {

    private Context mContext;
    private ImageView ivQrCode;
    private String filePath;

    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_share);
        mContext = this;
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, R.string.ShareToOthers, R.drawable.back_white, -1, R.drawable.icon_share_right, -1, onClickListener);
        ivQrCode = findViewById(R.id.ivQrCode);
        filePath = getExternalFilesDir("QrCode") + File.separator + "share_qrcode" + ".jpg";
        createQrCode();
        ivQrCode.setOnLongClickListener(onLongClickListener);
    }

    private final View.OnClickListener onClickListener = (v) -> {
        if (v.getId() == R.id.toolbarLeft) {
            ActivityController.finishActivity(this);
        } else if (v.getId() == R.id.toolbarRight) {
            if (success) {
                shareSingleImage(filePath);
            }
        }
    };

    private final View.OnLongClickListener onLongClickListener = (v) -> {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if (BitmapUtils.saveImageToGallery(mContext, bitmap)) {
            vibrator.vibrate(50);
            showToast(R.string.SaveToGallerySuccessfully);
        }
        return true;
    };

    /**
     * 生成专属二维码
     */
    private void createQrCode() {
        //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
        showLoadingDialog(mContext, getString(R.string.GeneratingQRCode), true);
        new Thread(() -> {
            Bitmap logo;

            User user = GsonUtils.parseJSON(SPHelper.getString("User", GsonUtils.convertJSON(new User())), User.class);

            if (!user.getIcon().equals("")) {
                String photoPath = (NetClient.getBaseUrl() + user.getIcon()).replace("\\", "/");
                logo = BitmapUtils.getBitmapFromNetwork(photoPath);
            } else {
                logo = BitmapUtils.getBitmapFromResource(mContext, R.mipmap.ic_launcher);
            }
            String downloadUrl = NetClient.getBaseUrlProject() + "VersionController/downloadNewVersionByLoginId.do?loginId=" + user.getId();
            success = ZXingUtils.createQRImage(mContext, downloadUrl, false, 800, 800, logo, filePath);
            runOnUiThread(() -> {
                if (success) {
                    ivQrCode.setImageBitmap(BitmapFactory.decodeFile(filePath));
                } else {
                    showToast(R.string.CreateQrCodeFailed);
                }
                cancelDialog();
            });
        }).start();
    }

    /**
     * 分享单张图片
     *
     * @param imagePath 图片路径
     */
    public void shareSingleImage(String imagePath) {
        Uri imageUri = Uri.fromFile(new File(imagePath));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.ShareTo)));
    }

}
