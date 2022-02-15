package com.liyuliang.mytime.fragment.homepage;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.liyuliang.mytime.BuildConfig;
import com.liyuliang.mytime.R;
import com.liyuliang.mytime.activity.main.MainActivity;
import com.liyuliang.mytime.activity.base.BaseFragment;
import com.liyuliang.mytime.activity.loginregister.LoginRegisterActivity;
import com.liyuliang.mytime.activity.settings.AccountInfoActivity;
import com.liyuliang.mytime.activity.settings.SetLanguageActivity;
import com.liyuliang.mytime.activity.version.QrCodeShareActivity;
import com.liyuliang.mytime.activity.version.VersionsActivity;
import com.liyuliang.mytime.bean.Result;
import com.liyuliang.mytime.bean.User;
import com.liyuliang.mytime.bean.VersionInfo;
import com.liyuliang.mytime.bean.VersionResult;
import com.liyuliang.mytime.constant.ApkInfo;
import com.liyuliang.mytime.constant.ErrorCode;
import com.liyuliang.mytime.contentprovider.SPHelper;
import com.liyuliang.mytime.interfaces.DownloadProgress;
import com.liyuliang.mytime.network.ExceptionHandle;
import com.liyuliang.mytime.network.NetClient;
import com.liyuliang.mytime.network.NetworkObserver;
import com.liyuliang.mytime.service.DownloadService;
import com.liyuliang.mytime.utils.ActivityController;
import com.liyuliang.mytime.utils.ApkUtils;
import com.liyuliang.mytime.utils.FileUtil;
import com.liyuliang.mytime.utils.GsonUtils;
import com.liyuliang.mytime.utils.LogUtils;
import com.liyuliang.mytime.widget.dialog.SelectDialog;
import com.liyuliang.mytime.widget.dialog.UpgradeVersionDialog;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 设置页面
 * Created at 2019/1/21 13:57
 *
 * @author Li Yuliang
 * @version 1.0
 */

public class SettingsFragment extends BaseFragment {

    private Context mContext;
    private MainActivity mainActivity;
    private ImageView ivUserIcon;
    private TextView tvUserName;
    // 版本更新相关信息
    private String latestVersionName, latestVersionMD5, latestVersionLog, apkDownloadPath, latestFileName;
    private int myVersionCode, latestVersionCode;

    private static final int GET_UNKNOWN_APP_SOURCES = 2;
    protected static final int INSTALL_PACKAGES_REQUEST_CODE = 103;

    public static DownloadProgress downloadProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mainActivity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ivUserIcon = view.findViewById(R.id.ivUserIcon);
        tvUserName = view.findViewById(R.id.tvUserName);

        LinearLayout llUserInfo = view.findViewById(R.id.llUserInfo);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        llUserInfo.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, screenWidth * 9 / 16));

        view.findViewById(R.id.llPersonalInformation).setOnClickListener(onClickListener);
        view.findViewById(R.id.llLanguageSettings).setOnClickListener(onClickListener);
        view.findViewById(R.id.llVersion).setOnClickListener(onClickListener);
        view.findViewById(R.id.llUpdate).setOnClickListener(onClickListener);
        view.findViewById(R.id.llShare).setOnClickListener(onClickListener);
        view.findViewById(R.id.ll_login_account).setOnClickListener(onClickListener);
        view.findViewById(R.id.ll_sign_out).setOnClickListener(onClickListener);

        downloadProgress = new DownloadProgress() {
            @Override
            public void downloadStart() {
                mainActivity.runOnUiThread(() -> showToast("正在后台下载"));
            }

            @Override
            public void downloadFinish() {
                mainActivity.runOnUiThread(() -> checkIsAndroidO());
            }
        };
        searchNewVersion(false);

        return view;
    }

    @Override
    public void lazyLoad() {

    }

    @Override
    public void onResume() {
        super.onResume();
        User user = GsonUtils.parseJSON(SPHelper.getString("User", GsonUtils.convertJSON(new User())), User.class);
        showUserIcon((NetClient.getBaseUrl() + user.getIcon()).replace("\\", "/"));
        // 当前版本
        myVersionCode = ApkUtils.getVersionCode(mContext);
        tvUserName.setText(user.getUsername());
    }

    /**
     * 加载头像
     *
     * @param photoPath 头像路径
     */
    private void showUserIcon(String photoPath) {
        LogUtils.d(TAG, "图片路径：" + photoPath);
        SPHelper.save("userIconPath", photoPath);
        if (photoPath != null) {
            RequestOptions options = new RequestOptions().error(R.drawable.photo_user).placeholder(R.drawable.photo_user).dontAnimate().circleCrop();
            Glide.with(this).load(photoPath).apply(options).into(ivUserIcon);
        }
    }

    private final View.OnClickListener onClickListener = (v) -> {
        if (v.getId() == R.id.llPersonalInformation) {
            // 个人信息
            mainActivity.openActivity(AccountInfoActivity.class);
        } else if (v.getId() == R.id.llLanguageSettings) {
            // 语言设置
            mainActivity.openActivity(SetLanguageActivity.class);
        } else if (v.getId() == R.id.llVersion) {
            // 版本信息
            mainActivity.openActivity(VersionsActivity.class);
        } else if (v.getId() == R.id.llUpdate) {
            // 版本更新
            searchNewVersion(true);
        } else if (v.getId() == R.id.llShare) {
            // 分享软件
            mainActivity.openActivity(QrCodeShareActivity.class);
        } else if (v.getId() == R.id.ll_login_account) {
            // 切换账号
            Intent intent1 = new Intent(mainActivity, LoginRegisterActivity.class);
            intent1.setAction("SwitchAccount");
            startActivity(intent1);
            ActivityController.finishActivity(mainActivity);
            mainActivity.overridePendingTransition(R.anim.left_in, R.anim.right_out);
        } else if (v.getId() == R.id.ll_sign_out) {
            // 退出程序
            showExitDialog();
        }
    };

    /**
     * 提示用户退出程序
     */
    private void showExitDialog() {
        SelectDialog selectDialog = new SelectDialog(mContext, getString(R.string.warning_exit));
        selectDialog.setButtonText(getString(R.string.Cancel), getString(R.string.Continue));
        selectDialog.setCancelable(false);
        selectDialog.setOnDialogClickListener(new SelectDialog.OnDialogClickListener() {
            @Override
            public void onOKClick() {
                // 开始权限检查
                ActivityController.exit(mainActivity);
            }

            @Override
            public void onCancelClick() {

            }
        });
        selectDialog.show();
    }

    /**
     * 查询最新版本
     */
    public void searchNewVersion(boolean showToast) {
        JsonObject params = new JsonObject();
        params.addProperty("apkTypeId", ApkInfo.APK_TYPE_ID_MyTime);

        Observable<Result> resultObservable = NetClient.getInstance(NetClient.getBaseUrlProject(), true).getZbsApi().searchNewVersion(params);
        resultObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkObserver<Result>(mainActivity) {

            @Override
            public void onSubscribe(Disposable d) {
                showLoadingDialog(mContext, "查询最新版本中", false);
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
            }

            @Override
            public void onNext(@NonNull Result result) {
                super.onNext(result);
                cancelDialog();
                VersionResult versionResult = GsonUtils.parseJSON(GsonUtils.convertJSON(result.getData()), VersionResult.class);
                if (result.getCode() == ErrorCode.SUCCESS) {
                    VersionInfo versionInfo = versionResult.getVersionInfo();
                    latestVersionName = versionInfo.getVersionName();
                    latestVersionMD5 = versionInfo.getMd5Value();
                    latestVersionLog = versionInfo.getVersionLog();
                    apkDownloadPath = versionInfo.getVersionUrl().replace("\\", "/");
                    latestFileName = versionInfo.getVersionFileName();
                    latestVersionCode = versionInfo.getVersionCode();
                    if (myVersionCode < latestVersionCode) {
                        showDialogUpdate();
                    } else {
                        if (showToast) {
                            showToast("您当前使用的是最新版本");
                        }
                    }
                } else if (result.getCode() == ErrorCode.FAIL) {
                    if (showToast) {
                        showToast("查询版本信息失败");
                    }
                }
            }
        });
    }

    /**
     * 提示版本更新的对话框
     */
    private void showDialogUpdate() {
        UpgradeVersionDialog upgradeVersionDialog = new UpgradeVersionDialog(mContext);
        upgradeVersionDialog.setCancelable(false);
        ((TextView) upgradeVersionDialog.findViewById(R.id.tv_versionLog)).setText(latestVersionLog);
        ((TextView) upgradeVersionDialog.findViewById(R.id.tv_currentVersion)).setText(ApkUtils.getVersionName(mContext));
        ((TextView) upgradeVersionDialog.findViewById(R.id.tv_latestVersion)).setText(latestVersionName);
        upgradeVersionDialog.setOnDialogClickListener(new UpgradeVersionDialog.OnDialogClickListener() {
            @Override
            public void onOKClick() {
                downloadApk(apkDownloadPath);
            }

            @Override
            public void onCancelClick() {
                upgradeVersionDialog.dismiss();
            }
        });
        upgradeVersionDialog.show();
    }

    /**
     * 下载新版本程序
     */
    public void downloadApk(String downloadUrl) {
        final int DOWNLOAD_APK_ID = 10;
        // 判断下载服务是不是已经在运行了
        if (isServiceRunning(DownloadService.class.getName())) {
            showToast("正在后台下载，请稍后");
        } else {
            Intent intent = new Intent(mainActivity, DownloadService.class);
            Bundle bundle = new Bundle();
            bundle.putString("download_url", downloadUrl);
            bundle.putInt("download_id", DOWNLOAD_APK_ID);
            bundle.putString("download_file", latestFileName);
            intent.putExtras(bundle);
            mainActivity.startService(intent);
        }
    }

    /**
     * 用来判断服务是否运行.
     *
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    private boolean isServiceRunning(String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mainActivity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 安装apk
     *
     * @param file 需要安装的apk
     */
    private void installApk(File file) {
        //先验证文件的正确性和完整性（通过MD5值）
        LogUtils.d(TAG, "文件路径：" + file.getAbsolutePath());
        if (file.isFile() && latestVersionMD5.equals(FileUtil.getFileMD5(file))) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", file);//在AndroidManifest中的android:authorities值
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            startActivity(intent);
        } else {
            showToast("File error");
        }
    }

    /**
     * Android8.0需要处理未知应用来源权限问题,否则直接安装
     */
    private void checkIsAndroidO() {
        LogUtils.d(TAG, "检查Android8.0安装软件的权限");
        File file = new File(ApkInfo.APP_ROOT_PATH + ApkInfo.DOWNLOAD_DIR, latestFileName);
        if (file.exists()) {
            LogUtils.d(TAG, "文件存在，准备安装");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LogUtils.d(TAG, "Android版本大于等于Android8.0，需要检查安装软件的权限");
                boolean b = mContext.getPackageManager().canRequestPackageInstalls();
                if (b) {
                    LogUtils.d(TAG, "有安装未知应用来源的权限，开始安装");
                    installApk(file);
                } else {
                    //请求安装未知应用来源的权限
                    LogUtils.d(TAG, "没有安装未知应用来源的权限，开始申请");
                    requestPermissions(new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, INSTALL_PACKAGES_REQUEST_CODE);
                }
            } else {
                LogUtils.d(TAG, "Android版本小于Android8.0，直接安装");
                installApk(file);
            }
        } else {
            LogUtils.d(TAG, "文件不存在，安装失败");
            showToast("文件不存在");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == INSTALL_PACKAGES_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkIsAndroidO();
            } else {
                //  Android8.0以上引导用户手动开启安装权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LogUtils.d(TAG, "需要引导用户手动开启安装权限");
                    Uri packageURI = Uri.parse("package:" + mContext.getPackageName());
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                    startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GET_UNKNOWN_APP_SOURCES) {// 从安装未知来源文件的设置页面返回
                checkIsAndroidO();
            }
        }
    }


}
