package com.demo.leafloading.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.demo.leafloading.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Random;

import static android.animation.ValueAnimator.REVERSE;

/**
 * Author: ZhengHuaizhi
 * Date: 2019/12/13
 * Description:
 */
public class LeafLoadingView extends View {

    // 淡白色
    private static final int WHITE_COLOR = 0xfffde399;
    // 橙色
    private static final int ORANGE_COLOR = 0xffffa800;
    // 淡白色画笔、橙色画笔、图片画笔
    private Paint mWhitePaint, mOrangePaint, mBitmapPaint;
    // 加载框图片，风扇图片，树叶图片
    private Bitmap mLoadingBoxBitmap, mFanBitmap, mLeafBitmap;
    // 风扇
    private Fan mFan;
    // 风扇动画
    private ObjectAnimator mFanAnimator;
    // 进度条
    private ProgressBar mProgressBar;
    // 进度条更新动画
    private ObjectAnimator mProgressAnimator;
    // 控件宽度、高度
    private int mWidth, mHeight;
    // 加载框间隙
    private float mLoadingMargin;
    // 风扇与风扇框间隙
    private float mFanMargin;
    // 进度条宽度
    private float mProgressWidth;
    // 进度条半圆部分半径
    private float mCircleRadius;
    // 将要达到的加载进度
    private int mFutureProgress;
    // 最大进度
    private int mMaxProgress;
    // 每片树叶的进度
    private int mPerLeafProgress;
    // 树叶标准左右移动速度
    private long mLeafLRTDuration;
    // 树叶标准上下移动速度
    private long mLeafUDTDuration;
    // 树叶标准旋转速度
    private long mLeafRDuration;
    // 风扇标准旋转速度
    private long mFanRDuration;
    // 进度条标准左右移动速度
    private long mProgressLRTDuration;

    private boolean isInit;

    public LeafLoadingView(Context context) {
        this(context, null);
    }

    public LeafLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeafLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mProgressWidth = mWidth - 2 * mLoadingMargin;
        mCircleRadius = (mHeight - 2 * mLoadingMargin) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInit) {
            moveCanvas(canvas);
            drawWhiteProgress(canvas);
            drawLeaves(canvas);
            drawProgress(canvas);
            drawLoadingBox(canvas);
            drawFan(canvas);
        }
    }

    private void initAttrs() {
        mLoadingMargin = 10f;
        mFanMargin = 10f;
        mMaxProgress = 100;
        mPerLeafProgress = mMaxProgress / 50;
        mLeafLRTDuration = 3000;
        mLeafUDTDuration = 6000;
        mLeafRDuration = 1500;
        mFanRDuration = 3000;
        mProgressLRTDuration = 5000;
    }

    private void initPaints() {
        mWhitePaint = new Paint();
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setColor(WHITE_COLOR);

        mOrangePaint = new Paint();
        mOrangePaint.setAntiAlias(true);
        mOrangePaint.setColor(ORANGE_COLOR);

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);
    }

    private void initBitmaps() {
        mLoadingBoxBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.leaf_kuang);
        mFanBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.fengshan);
        mLeafBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.leaf);
    }

    /**
     * 画白色进度条：左半圆 + 右矩形
     */
    private void drawWhiteProgress(Canvas canvas) {
        RectF leftR = new RectF(-mCircleRadius, -mCircleRadius, mCircleRadius,
                mCircleRadius);
        RectF rightR = new RectF(0f, -mCircleRadius,
                mProgressWidth - 2 * mCircleRadius, mCircleRadius);
        canvas.drawArc(leftR, 90f, 270f, true, mWhitePaint);
        canvas.drawRect(rightR, mWhitePaint);
    }

    /**
     * 移动画布到半圆圆心
     */
    private void moveCanvas(Canvas canvas) {
        canvas.translate(mLoadingMargin + mCircleRadius,
                mLoadingMargin + mCircleRadius);
    }

    /**
     * 画树叶
     */
    private void drawLeaves(Canvas canvas) {
        ArrayList<Leaf> leaves = LeafFactory.getInstance().getLeaves();
        for (Leaf leaf : leaves) {
            Matrix matrix = new Matrix();
            matrix.postTranslate(leaf.left, leaf.top);
            matrix.postRotate(leaf.degree, leaf.left + mLeafBitmap.getWidth(),
                    leaf.top + mLeafBitmap.getHeight());
            canvas.drawBitmap(mLeafBitmap, matrix, mBitmapPaint);
        }
    }

    /**
     * 画风扇
     */
    private void drawFan(Canvas canvas) {
        // 指定要汲取的图片区域
        Rect src = new Rect(0, 0, mFanBitmap.getWidth(), mFanBitmap.getHeight());
        // 绘制图片处
        RectF dst = new RectF(mProgressWidth - 3 * mCircleRadius + mFanMargin,
                -mCircleRadius + mFanMargin,
                mProgressWidth - mCircleRadius - mFanMargin,
                mCircleRadius - mFanMargin);
        canvas.rotate(mFan.degree, dst.left + dst.width() / 2, 0);
        canvas.drawBitmap(mFanBitmap, src, dst, mBitmapPaint);
    }

    /**
     * 画当前进度条
     */
    private void drawProgress(Canvas canvas) {
        // 百分百时进度条宽度
        float totalWidth = mProgressWidth - mCircleRadius;
        // 进度百分比
        float percent = mProgressBar.progress / (float) mMaxProgress;
        // 左半圆占进度条百分比
        float progressPercent = mCircleRadius / totalWidth;

        if (percent <= progressPercent) {
            // 左半圆
            float a =
                    (float) Math.toDegrees(Math.acos((mCircleRadius - percent * totalWidth) / mCircleRadius));
            canvas.drawArc(-mCircleRadius, -mCircleRadius, mCircleRadius, mCircleRadius,
                    180f - a, 2f * a, false, mOrangePaint);
        } else {
            // 左半圆
            canvas.drawArc(-mCircleRadius, -mCircleRadius, mCircleRadius,
                    mCircleRadius,
                    90f, 180f, true, mOrangePaint);
            // 右矩形
            canvas.drawRect(0, -mCircleRadius, (percent * totalWidth) - mCircleRadius
                    , mCircleRadius, mOrangePaint);
        }
    }

    private void drawLoadingBox(Canvas canvas) {
        // 指定要汲取的图片区域
        Rect src = new Rect(0, 0, mLoadingBoxBitmap.getWidth(), mLoadingBoxBitmap.getHeight());
        // 绘制图片处
        RectF dst = new RectF(-mCircleRadius - mLoadingMargin,
                -mCircleRadius - mLoadingMargin,
                mProgressWidth - mCircleRadius + mLoadingMargin,
                mCircleRadius + mLoadingMargin);
        canvas.drawBitmap(mLoadingBoxBitmap, src, dst, mBitmapPaint);
    }

    /**
     * 添加新树叶
     */
    private void addLeaves(int upProgress) {
        // 每百分之2的变化绘制一片新树叶
        for (int i = 0; i < upProgress / mPerLeafProgress; i++) {
            Leaf leaf = new Leaf(mFutureProgress / 2, mProgressWidth - 2 * mCircleRadius,
                    0, 0f);
            startLeafAnimation(leaf);
        }
    }

    /**
     * 给新树叶开启动画
     */
    private void startLeafAnimation(Leaf leaf) {
        Random random = new Random();
        float amplitude = random.nextFloat() * 3 + 1;

        // 左移起点x坐标
        float pxStart = leaf.left - mLeafBitmap.getWidth();
        // 左移终点x坐标
        float pxEnd = mProgressWidth * mProgressBar.progress / mMaxProgress - mCircleRadius;
        if (pxEnd > pxStart) {
            pxEnd = pxStart;
        }
        // 左移
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(leaf, "left", pxStart, pxEnd);
        objectAnimator.setInterpolator(new LinearInterpolator());
        float k = -mLeafLRTDuration * amplitude / mProgressWidth;
        float x = mCircleRadius + pxEnd;
        float b = mLeafLRTDuration * amplitude;
        long duration = (long) (k * x + b);
        objectAnimator.setDuration(duration);
        objectAnimator.addUpdateListener(animation -> {
            LeafFactory.getInstance().putLeaf(leaf.id, leaf);
        });
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFutureProgress = mFutureProgress + mPerLeafProgress;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mProgressAnimator.isPaused()) {
                    mProgressAnimator.resume();
                }
                LeafFactory.getInstance().removeLeft(leaf.id);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        // 上下移
        float amplitude1 = random.nextFloat() * mCircleRadius;
        boolean flag1 = System.currentTimeMillis() % 2 == 0;
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(leaf, "top", flag1 ? amplitude1 :
                -amplitude1, flag1 ? -amplitude1 : amplitude1);
        objectAnimator1.setInterpolator(new LinearInterpolator());
        objectAnimator1.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator1.setRepeatMode(REVERSE);
        objectAnimator1.setDuration(mLeafUDTDuration);
        // 中心旋转
        float amplitude2 = random.nextFloat() * 180;
        boolean flag2 = System.currentTimeMillis() % 2 == 0;
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(leaf, "degree", flag2 ?
                amplitude2 : -amplitude2, flag2 ? amplitude2 + 360f : -amplitude2 - 360f);
        objectAnimator2.setInterpolator(new LinearInterpolator());
        objectAnimator2.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator2.setDuration((long) (mLeafRDuration * amplitude));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator, objectAnimator1, objectAnimator2);
        animatorSet.start();
    }

    /**
     * 开启风扇动画
     */
    private void startFanAnimation() {
        mFan = new Fan();
        mFanAnimator = ObjectAnimator.ofFloat(mFan, "degree", mFan.degree, 360f);
        mFanAnimator.setInterpolator(new LinearInterpolator());
        mFanAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mFanAnimator.setDuration(mFanRDuration);
        mFanAnimator.addUpdateListener(animation -> {
            invalidate();
        });
        mFanAnimator.start();
    }

    /**
     * 开启进度条更新动画
     */
    private void startProgressAnimation() {
        mProgressBar = new ProgressBar(0, mMaxProgress);
        mProgressAnimator = ObjectAnimator.ofFloat(mProgressBar, "progress",
                mProgressBar.progress, mProgressBar.maxProgress);
        mProgressAnimator.setInterpolator(new LinearInterpolator());
        mProgressAnimator.setDuration(mProgressLRTDuration);
        mProgressAnimator.addUpdateListener(animation -> {
            if (mProgressBar.progress >= mFutureProgress) {
                mProgressAnimator.pause();
            }
            invalidate();
        });
        mProgressAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                stopAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        mProgressAnimator.start();
    }

    private void stopAnimation() {
        mFanAnimator.cancel();
        mProgressAnimator.cancel();
    }

    public void build() {
        initPaints();
        initBitmaps();
        startFanAnimation();
        startProgressAnimation();
        isInit = true;
    }
    /**
     * 设置将要加载到的新进度
     */
    public void setProgress(int progress) {
        progress = progress > mMaxProgress ? mMaxProgress : progress;
        // 要增加的进度
        int upProgress = progress - mFutureProgress;
        addLeaves(upProgress);
    }

    /**
     * 设置最大进度
     */
    public void setMaxProgress(int maxProgress) {
        BigDecimal bigDecimal = new BigDecimal(maxProgress * 0.02);
        this.mMaxProgress = maxProgress;
        this.mPerLeafProgress = bigDecimal.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    /**
     * 设置树叶左右移动速率
     */
    public void setLeafLRTDuration(long leafLRTDuration) {
        this.mLeafLRTDuration = leafLRTDuration;
    }

    /**
     * 设置树叶上下移动速率
     */
    public void setLeafUDTDuration(long leafUDTDuration) {
        this.mLeafUDTDuration = leafUDTDuration;
    }

    /**
     * 设置树叶自转速率
     */
    public void setLeafRDuration(long leafRDuration) {
        this.mLeafRDuration = leafRDuration;
    }

    /**
     * 设置风扇自转速率
     */
    public void setFanRDuration(long fanRDuration) {
        this.mFanRDuration = fanRDuration;
    }

    /**
     * 设置进度条左右移动速率
     */
    public void setProgressLRTDuration(long progressLRTDuration) {
        this.mProgressLRTDuration = progressLRTDuration;
    }
}
