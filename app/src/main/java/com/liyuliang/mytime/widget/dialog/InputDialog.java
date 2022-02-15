package com.liyuliang.mytime.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.liyuliang.mytime.R;

/**
 * 输入Dialog
 * Created at 2019/8/15 9:47
 *
 * @author LiYuliang
 * @version 1.0
 */

public class InputDialog extends Dialog {
    private Context context;
    private String text;
    private EditText etInput;
    private Button okBtn, cancelBtn;
    private OnDialogClickListener dialogClickListener;

    public InputDialog(Context context, String text) {
        super(context);
        this.context = context;
        this.text = text;
        initView();
    }

    //初始化View
    private void initView() {
        setContentView(R.layout.dialog_input);
        initWindow();
        TextView tvInputPrompt = findViewById(R.id.tvInputPrompt);
        etInput = findViewById(R.id.etInput);
        okBtn = findViewById(R.id.btn_ok);
        cancelBtn = findViewById(R.id.btn_cancel);
        tvInputPrompt.setText(text);
        okBtn.setOnClickListener((v) -> {
            dismiss();
            if (dialogClickListener != null) {
                dialogClickListener.onOKClick();
            }
        });
        cancelBtn.setOnClickListener((v) -> {
            dismiss();
            if (dialogClickListener != null) {
                dialogClickListener.onCancelClick();
            }
        });
    }

    /**
     * 设置按钮上的文字
     *
     * @param leftText  左边按钮文字
     * @param rightText 右边按钮文字
     */
    public void setButtonText(String leftText, String rightText) {
        cancelBtn.setText(leftText);
        okBtn.setText(rightText);
    }

    /**
     * 添加黑色半透明背景
     */
    private void initWindow() {
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setBackgroundDrawable(new ColorDrawable(0));//设置window背景
            dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//设置输入法显示模式
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics d = context.getResources().getDisplayMetrics();//获取屏幕尺寸
            lp.width = (int) (d.widthPixels * 0.9); //宽度为屏幕80%
            lp.gravity = Gravity.CENTER;  //中央居中
            dialogWindow.setAttributes(lp);
        }
    }

    public void setOnDialogClickListener(OnDialogClickListener clickListener) {
        dialogClickListener = clickListener;
    }

    /**
     * 添加按钮点击事件
     */
    public interface OnDialogClickListener {
        void onOKClick();

        void onCancelClick();
    }

    public String getInputContent() {
        return etInput.getText().toString().replace(" ", "");
    }
}

