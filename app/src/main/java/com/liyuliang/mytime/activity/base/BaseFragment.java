package com.liyuliang.mytime.activity.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.liyuliang.mytime.R;
import com.liyuliang.mytime.utils.DeviceUtil;
import com.liyuliang.mytime.utils.LogUtils;
import com.liyuliang.mytime.utils.MyLifecycleHandler;
import com.liyuliang.mytime.utils.NotificationsUtils;
import com.liyuliang.mytime.utils.ToastUtils;
import com.liyuliang.mytime.widget.dialog.LoadingDialog;

/**
 * fragment基类
 * Created by Li Yuliang on 2017/2/13 0013.
 *
 * @author LiYuliang
 * @version 2017/10/30
 */

public abstract class BaseFragment extends Fragment {

    public String TAG = getClass().getName();
    private Context mContext;
    private Toast toast;
    private LoadingDialog loadingDialog;
    protected float mDensity;
    protected int mDensityDpi;
    protected int mWidth;
    protected int mHeight;
    protected float mRatio;
    protected int mAvatarSize;
    protected ViewGroup viewGroup;
    protected LayoutInflater mInflater;

    protected boolean isViewInitiated;
    protected boolean isVisibleToUser;
    protected boolean isDataInitiated;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        loadingDialog = new LoadingDialog(mContext, R.style.loading_dialog);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mDensityDpi = dm.densityDpi;
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        mRatio = Math.min((float) mWidth / 720, (float) mHeight / 1280);
        mAvatarSize = (int) (50 * mDensity);
        LogUtils.d(LogUtils.TAG, getClass().getSimpleName() + "onCreate() ");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewInitiated = true;
        prepareFetchData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        prepareFetchData();
    }

    /**
     * 在懒加载中请求数据
     */
    public abstract void lazyLoad();

    public boolean prepareFetchData() {
        return prepareFetchData(false);
    }

    public boolean prepareFetchData(boolean forceUpdate) {
        if (isVisibleToUser && isViewInitiated && (!isDataInitiated || forceUpdate)) {
            lazyLoad();
            LogUtils.d(LogUtils.TAG, getClass().getSimpleName() + "lazyLoad() ");
            isDataInitiated = true;
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.d(LogUtils.TAG, getClass().getSimpleName() + "onCreateView() ");
        this.mInflater = inflater;
        this.viewGroup = container;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        LogUtils.d(LogUtils.TAG, getClass().getSimpleName() + "onViewCreated() ");
    }

    /**
     * 沉浸模式View
     *
     * @param statusBar 状态栏
     */
    protected void setStatusBar(View statusBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            statusBar.setVisibility(View.VISIBLE);
            statusBar.getLayoutParams().height = DeviceUtil.getStatusHeight(getActivity());
            statusBar.setLayoutParams(statusBar.getLayoutParams());
        } else {
            statusBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //如果toast在显示则取消显示
        if (toast != null) {
            toast.cancel();
        }
        //取消显示dialog
        cancelDialog();
        LogUtils.d(LogUtils.TAG, getClass().getSimpleName() + "onPause() ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d(LogUtils.TAG, getClass().getSimpleName() + "onDestroy() ");
    }

    /**
     * 显示加载的dialogs
     *
     * @param context    Context对象
     * @param msg        显示的信息
     * @param cancelable 是否可取消
     */
    public void showLoadingDialog(Context context, String msg, boolean cancelable) {
        if (!loadingDialog.isShowing()) {
            loadingDialog = new LoadingDialog(context, R.style.loading_dialog);
            loadingDialog.setCancelable(cancelable);
            if (!((AppCompatActivity) context).isFinishing()) {
                //显示dialog
                loadingDialog.show();
                loadingDialog.setMessage(msg);
            }
        }
    }

    /**
     * 显示加载的dialogs
     *
     * @param context    Context对象
     * @param cancelable 是否可取消
     */
    public void showLoadingDialog(Context context, boolean cancelable) {
        if (!loadingDialog.isShowing()) {
            loadingDialog = new LoadingDialog(context, R.style.loading_dialog);
            loadingDialog.setCancelable(cancelable);

            if (!((AppCompatActivity) context).isFinishing()) {
                //显示dialog
                loadingDialog.show();
            }
        }
    }

    /**
     * 取消dialog显示
     */
    public void cancelDialog() {
        if (null != loadingDialog && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public void showToast(int resId) {
        showToast(getString(resId));
    }

    /**
     * 自定义的Toast，避免重复出现
     *
     * @param msg 弹出的信息
     */
    public void showToast(String msg) {
        //如果授予了App系统通知权限，则使用系统Toast
        if (NotificationsUtils.isNotificationEnabled(getContext()) && MyLifecycleHandler.isApplicationInForeground()) {
            if (toast == null) {
                toast = new Toast(mContext);
                //获取自定义视图
                View view = LayoutInflater.from(mContext).inflate(R.layout.toast_layout, viewGroup);
                TextView tvMessage = view.findViewById(R.id.mbMessage);
                //设置文本
                tvMessage.setText(msg);
                //设置视图
                toast.setView(view);
                //设置显示时长
                toast.setDuration(Toast.LENGTH_SHORT);
            } else {
                View view = LayoutInflater.from(mContext).inflate(R.layout.toast_layout, viewGroup);
                TextView tvMessage = view.findViewById(R.id.mbMessage);
                //设置文本
                tvMessage.setText(msg);
                //设置视图
                toast.setView(view);
                //设置显示时长
                toast.setDuration(Toast.LENGTH_SHORT);
            }
            toast.show();
        } else {
            //否则使用自定义的View（模仿Toast，存在瑕疵）
            ToastUtils.makeText(getContext(), msg, ToastUtils.LENGTH_SHORT).show();
        }
    }
}
