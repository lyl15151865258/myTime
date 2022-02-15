package com.liyuliang.mytime.pageindicator;

import androidx.annotation.Nullable;

import com.liyuliang.mytime.pageindicator.animation.AnimationManager;
import com.liyuliang.mytime.pageindicator.animation.controller.ValueController;
import com.liyuliang.mytime.pageindicator.animation.data.Value;
import com.liyuliang.mytime.pageindicator.draw.DrawManager;
import com.liyuliang.mytime.pageindicator.draw.data.Indicator;

public class IndicatorManager implements ValueController.UpdateListener {

    private DrawManager drawManager;
    private AnimationManager animationManager;
    private Listener listener;

    interface Listener {
        void onIndicatorUpdated();
    }

    IndicatorManager(@Nullable Listener listener) {
        this.listener = listener;
        this.drawManager = new DrawManager();
        this.animationManager = new AnimationManager(drawManager.indicator(), this);
    }

    public AnimationManager animate() {
        return animationManager;
    }

    public Indicator indicator() {
        return drawManager.indicator();
    }

    public DrawManager drawer() {
        return drawManager;
    }

    @Override
    public void onValueUpdated(@Nullable Value value) {
        drawManager.updateValue(value);
        if (listener != null) {
            listener.onIndicatorUpdated();
        }
    }
}
