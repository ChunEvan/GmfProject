package gmf.com.evan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import gmf.com.evan.R;

import static gmf.com.evan.extension.ViewExtension.dp2px;

/**
 * Created by Evan on 16/7/18 下午4:05.
 */
public class GMFProgressBar extends View {
    public static final int THEME_DARK = 0;
    public static final int THEME_LIGHT = 1;

    private int mOffSetStartAngle;
    private int mSweepAngle;
    private int mBorderWidth;
    private int mFillColor;
    private int mBorderColor;
    private RectF mRect;
    private Paint mPaint;
    private Handler mHandler;
    private Runnable mRotatingTask = new Runnable() {
        @Override
        public void run() {
            mOffSetStartAngle += 5;
            mOffSetStartAngle = mOffSetStartAngle % 361;
            invalidate();
            mHandler.postDelayed(this, 25);
        }
    };

    public GMFProgressBar(Context context) {
        this(context, null);
    }

    public GMFProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GMFProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mOffSetStartAngle = 0;
        mSweepAngle = 120;
        mBorderWidth = dp2px(this, 2);
        int theme = THEME_DARK;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GMFProgressBar, defStyleAttr, 0);
        if (ta != null) {
            theme = ta.getInt(R.styleable.GMFProgressBar_gmf_pb_theme, theme);
            ta.recycle();
        }

        mRect = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mHandler = new Handler();

        setTheme(theme);
    }

    public void setTheme(int theme) {
        if (theme > 0 && theme <= 1) {
            if (theme == THEME_DARK) {
                mBorderColor = 0x0FFFFFFF;
                mFillColor = 0xFFFFFFFF;
            } else if (theme == THEME_LIGHT) {
                mFillColor = 0xFFFFFFFF;
                mBorderColor = 0x0FFFFFFF;
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            mHandler.removeCallbacks(mRotatingTask);
            mHandler.post(mRotatingTask);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!isInEditMode()) {
            mHandler.removeCallbacks(mRotatingTask);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(dp2px(48), MeasureSpec.EXACTLY);
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(dp2px(48), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int radius = Math.min(getWidth(), getHeight()) >> 1;
        final int cx = getWidth() >> 1;
        final int cy = getHeight() >> 1;

        mRect.set(0, 0, getWidth(), getHeight());
        mRect.inset(mBorderColor, mBorderColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setColor(mBorderColor);
        mPaint.setAlpha(15);
        canvas.drawCircle(cx, cy, radius - mBorderWidth, mPaint);

        mPaint.setColor(mFillColor);
        mPaint.setAlpha(255);
        canvas.drawArc(mRect, -90 + mOffSetStartAngle, mSweepAngle, false, mPaint);
    }
}
