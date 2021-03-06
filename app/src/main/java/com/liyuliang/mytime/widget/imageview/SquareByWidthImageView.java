package com.liyuliang.mytime.widget.imageview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * 正方形ImageView（高度适应宽度）
 * Created by LiYuliang on 2017/10/30 0030.
 *
 * @author LiYuliang
 * @version 2017/10/30
 */

public class SquareByWidthImageView extends AppCompatImageView {

    public SquareByWidthImageView(Context context) {
        super(context);
    }

    public SquareByWidthImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareByWidthImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }

}