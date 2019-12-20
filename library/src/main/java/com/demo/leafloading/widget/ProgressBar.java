package com.demo.leafloading.widget;

/**
 * Author: ZhengHuaizhi
 * Date: 2019/12/19
 * Description: 进度条
 */
public class ProgressBar {
    float progress;
    float maxProgress;

    public ProgressBar(float progress, float maxProgress) {
        this.progress = progress;
        this.maxProgress = maxProgress;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }
}
