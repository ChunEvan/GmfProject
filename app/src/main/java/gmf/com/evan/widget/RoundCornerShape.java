package gmf.com.evan.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;

/**
 * Created by Evan on 16/7/13 下午2:48.
 */
public class RoundCornerShape extends Shape {

    private RectF mRectF = new RectF();
    private int mFillColor;
    private int mRadiusInPx;
    private int mBorderColor;
    private int mBorderWithInPx;

    public RoundCornerShape(int fillColor, int radiusInPx) {
        mFillColor = fillColor;
        mRadiusInPx = radiusInPx;
    }

    public RoundCornerShape border(int borderColor, int borderWithInPx) {
        mBorderColor = borderColor;
        mBorderWithInPx = borderWithInPx;
        return this;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        mRectF.set(0, 0, getWidth(), getHeight());
        if (mFillColor != 0) {
            paint.setColor(mFillColor);
            canvas.drawRoundRect(mRectF, mRadiusInPx, mRadiusInPx, paint);
        }

        if (mBorderColor != 0 && mBorderWithInPx > 0) {
            paint.setColor(mBorderColor);
            paint.setStrokeWidth(mBorderWithInPx);
            paint.setStyle(Paint.Style.STROKE);
            mRectF.inset(mBorderWithInPx, mBorderWithInPx);
            canvas.drawRoundRect(mRectF, mRadiusInPx, mRadiusInPx, paint);
        }

    }
}
