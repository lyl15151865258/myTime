package com.liyuliang.mytime.widget.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.liyuliang.mytime.R;

/**
 * 选择照片、拍照的弹窗
 * Created at 2019/6/5 14:46
 *
 * @author LiYuliang
 * @version 1.0
 */

public class SelectPicturePopupWindow extends PopupWindow implements View.OnClickListener {

    private final View mMenuView;
    private PopupWindow popupWindow;
    private OnSelectedListener mOnSelectedListener;

    public SelectPicturePopupWindow(Context context, ViewGroup viewGroup) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_camera, viewGroup, false);
        Button takePhotoBtn = mMenuView.findViewById(R.id.btn_camera_pop_camera);
        Button pickPictureBtn = mMenuView.findViewById(R.id.btn_camera_pop_album);
        Button cancelBtn = mMenuView.findViewById(R.id.btn_camera_pop_cancel);
        // 设置按钮监听
        takePhotoBtn.setOnClickListener(this);
        pickPictureBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    /**
     * 把一个View控件添加到PopupWindow上并且显示
     */
    public void showPopupWindow(View parent) {
        popupWindow = new PopupWindow(mMenuView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        popupWindow.setBackgroundDrawable(dw);
        // 设置窗口显示的动画效果
        popupWindow.setAnimationStyle(R.style.AnimBottom);
        // 设置外部可点击
        popupWindow.setOutsideTouchable(true);
        // 点击其他地方隐藏键盘 popupWindow
        popupWindow.setFocusable(true);
        popupWindow.update();
    }

    /**
     * 移除PopupWindow
     */
    public void dismissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (null != mOnSelectedListener) {
            if (v.getId() == R.id.btn_camera_pop_camera) {
                mOnSelectedListener.onSelected(v, 0);
            } else if (v.getId() == R.id.btn_camera_pop_album) {
                mOnSelectedListener.onSelected(v, 1);
            } else if (v.getId() == R.id.btn_camera_pop_cancel) {
                mOnSelectedListener.onSelected(v, 2);
            }
        }
    }

    /**
     * 设置选择监听
     */
    public void setOnSelectedListener(OnSelectedListener l) {
        this.mOnSelectedListener = l;
    }

    /**
     * 选择监听接口
     */
    public interface OnSelectedListener {
        void onSelected(View v, int position);
    }

}