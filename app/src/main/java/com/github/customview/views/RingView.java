package com.github.customview.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.github.customview.R;

/**
 * Created by admin on 16/1/18.
 */
public class RingView extends View {

    public static final String TAG = RingView.class.getSimpleName();

    private static final int MARK_IMG_LENGTH = 400;
    private static final int STATE_NONE = 0;
    private static final int STATE_DONE_PROGRESS = 1;

    private float currentMarkOffset = MARK_IMG_LENGTH;
    private float mMarkXPosition;
    private float mMarkYPosition;
    private Bitmap mMarkBitmap;
    private Bitmap mTempBitmap;

    private int mState = STATE_NONE;

    private Paint mPaintLevel;
    private Paint mPaintStart;
    private Paint mPaintLevelEmpty;
    private Paint mPaintWide;
    private Paint mPaintText;

    private RectF mRectF;
    private float mStartColorAngle = 120;
    private float mStartEmptyColorAngle = 120;

    private float mSweepAngle = 0;
    private int[] mColors;
    private float[] mPositions;

    private SweepGradient mSweepGradient;

    private int mLevel;
    private int mMaxLevel = 30;
    private int mDrawIndex = 0;

    private int mDefaultSize;
    private int mWidth;


    private float mWideRingWidth;
    private float mInnerRingWidth;
    private float mLevelTextSize;

    private ObjectAnimator mMarkAnimator;

    private OnFinishListener onFinishListener;

    public RingView(Context context) {
        this(context, null);
    }

    public RingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.level_ring);
        mWideRingWidth = a.getDimension(R.styleable.level_ring_wide_ring_width, 0);
        mInnerRingWidth = a.getDimension(R.styleable.level_ring_inner_ring_width, 0);
        mLevelTextSize = a.getDimension(R.styleable.level_ring_level_text_size, 10);
        a.recycle();

        mDefaultSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getContext().getResources().getDisplayMetrics());


        mPaintStart = new Paint();
        mPaintStart.setColor(Color.parseColor("#FFFFFF"));
        mPaintStart.setAlpha(50);
        mPaintStart.setStrokeWidth(mInnerRingWidth);
        mPaintStart.setStyle(Paint.Style.STROKE);
        mPaintStart.setAntiAlias(true);

        mPaintLevel = new Paint();
        mPaintLevel.setColor(Color.parseColor("#FFFFFF"));
        mPaintLevel.setStrokeWidth(mInnerRingWidth);
        mPaintLevel.setStyle(Paint.Style.STROKE);
        mPaintLevel.setAntiAlias(true);

        mPaintLevelEmpty = new Paint();
        mPaintLevelEmpty.setColor(Color.parseColor("#CCCCCC"));
        mPaintLevelEmpty.setAlpha(150);
        mPaintLevelEmpty.setStrokeWidth(mInnerRingWidth);
        mPaintLevelEmpty.setStyle(Paint.Style.STROKE);
        mPaintLevelEmpty.setAntiAlias(true);

        mPaintWide = new Paint();
        mPaintWide.setColor(Color.parseColor("#FFFFFF"));
        mPaintWide.setAlpha(50);
        mPaintWide.setStrokeWidth(mWideRingWidth);
        mPaintWide.setStyle(Paint.Style.STROKE);
        mPaintWide.setAntiAlias(true);

        mPaintText = new Paint();
        mPaintText.setColor(Color.parseColor("#FFFFFF"));
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setAntiAlias(true);
        mPaintText.setTextSize(mLevelTextSize);

        mColors = getResources().getIntArray(R.array.level_ring_colors);

        mPositions = new float[30];
        for (int i = 0; i < mPositions.length; i++) {
            float position;

            position = 0f + 0.029f * (float) i;

            if (position > 1) {
                position = 1;
            }
            mPositions[i] = position;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(measureDimension(mDefaultSize, widthMeasureSpec),
                measureDimension(mDefaultSize, widthMeasureSpec));
    }

    /**
     * 测量该空间的宽高
     *
     * @param defaultSize
     * @param measureSpec
     * @return
     */
    private int measureDimension(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                result = defaultSize;
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    /**
     * 在测量结束后做一些初始化操作
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        init();
    }

    private void init() {

        setUpMark();

        float padding = mWideRingWidth / 2 + 2;

        mRectF = new RectF(padding, padding, mWidth - padding, mWidth - padding);

        mSweepGradient = new SweepGradient(mWidth / 2, mWidth / 2, mColors, mPositions);
        Matrix matrix = new Matrix();
        matrix.setRotate(120, mWidth / 2, mWidth / 2);
        mSweepGradient.setLocalMatrix(matrix);
        mPaintLevel.setShader(mSweepGradient);
    }

    /**
     * 初始化在绘制完成圆环后执行的圆中心的动画
     */
    private void setUpMark() {
        mTempBitmap = Bitmap.createBitmap(mWidth, mWidth, Bitmap.Config.ARGB_8888);

        mMarkBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_done_white_48dp);
        mMarkXPosition = mWidth / 2 - mMarkBitmap.getWidth() / 2;
        mMarkYPosition = mWidth / 2 - mMarkBitmap.getHeight() / 2;
        mMarkAnimator = ObjectAnimator.ofFloat(this, "currentMarkOffset", mWidth - mMarkYPosition, 0)
                .setDuration(300);
        mMarkAnimator.setInterpolator(new OvershootInterpolator());
        mMarkAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (onFinishListener != null) {
                    onFinishListener.onFinish();
                }
            }
        });
    }

    public void setLevel(int level) {
        this.mLevel = level;
        mDrawIndex = 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawArc(mRectF, 120, 300, false, mPaintWide);

        //绘制底部空白的环形
        canvas.drawArc(mRectF, 60, 30, false, mPaintStart);
        canvas.drawArc(mRectF, 90, 30, false, mPaintLevelEmpty);

        //初始角度
        mStartColorAngle = 120;
        mStartEmptyColorAngle = 120;

        //绘制灰色的色块
        for (int i = 0; i < mMaxLevel; i++) {
            canvas.drawArc(mRectF, mStartEmptyColorAngle, 9, false, mPaintLevelEmpty);
            mStartEmptyColorAngle += 10;
            if (mStartEmptyColorAngle > 360) {
                mStartEmptyColorAngle -= 360;
            }
        }

        //如果每次带颜色的色块增长结束,直接绘制出来
        for (int i = 0; i < mDrawIndex; i++) {
            canvas.drawArc(mRectF, mStartColorAngle, 9, false, mPaintLevel);
            mStartColorAngle += 10;
        }

        if (mDrawIndex < mLevel) {
            canvas.drawArc(mRectF, mStartColorAngle, mSweepAngle, false, mPaintLevel);
        } else {
            if (mState != STATE_DONE_PROGRESS) {
                mState = STATE_DONE_PROGRESS;
                mMarkAnimator.start();
            }
            //mTempCanvas.drawBitmap(mMarkBitmap, mMarkXPosition, mMarkYPosition + currentMarkOffset, mMarkPaint);
            canvas.drawBitmap(mMarkBitmap, mMarkXPosition, mMarkYPosition + currentMarkOffset, null);
            return;

        }

        //如果带颜色的色块没有绘制完成，递增角度,形成增长的效果
        //如果绘制完成当前色块,mDrawIndex增加，绘制下一个带颜色的色块
        if (mSweepAngle < 9) {
            mSweepAngle += 3;
        } else {
            mSweepAngle = 0;
            mDrawIndex++;
        }

        invalidate();
    }


    public void setCurrentMarkOffset(float currentMarkOffset) {
        this.currentMarkOffset = currentMarkOffset;
        postInvalidate();
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    public interface OnFinishListener {
        void onFinish();
    }
}
