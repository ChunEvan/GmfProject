package gmf.com.evan.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import gmf.com.evan.R;

import static gmf.com.evan.extension.ViewExtension.dp2px;
import static gmf.com.evan.extension.ViewExtension.sp2px;

/**
 * Created by Evan on 16/7/13 上午10:57.
 */
public class MainTabView extends RelativeLayout {

    private View mRedDotView;

    public MainTabView(Context context) {
        this(context, null);
    }

    public MainTabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        String mTitleText = "noTitle";
        int iconResId = 0;
//        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MainTabView, defStyleAttr, 0);
//        if (ta != null) {
//            mTitleText = Optional.of(ta.getString(R.styleable.MainTabView_title)).or(mTitleText);
//            iconResId = ta.getInt(R.styleable.MainTabView_icon, iconResId);
//            ta.recycle();
//        }

        Resources res = context.getResources();
        {
            TextView titleLabel = new TextView(context);
            titleLabel.setId(android.R.id.text1);
            titleLabel.setTextColor(res.getColorStateList(R.color.sel_tab_main_text));
            titleLabel.setTextSize(sp2px(10));
            titleLabel.setText(mTitleText);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
            params.addRule(ALIGN_PARENT_BOTTOM);
            params.addRule(CENTER_HORIZONTAL);
            params.bottomMargin = dp2px(this, 8);
            addView(titleLabel, params);
        }

        FrameLayout imageContainer = new FrameLayout(context);
        {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, 0);
            params.addRule(ALIGN_PARENT_TOP);
            params.addRule(ALIGN_TOP, android.R.id.text1);
            addView(imageContainer, params);
        }

        ImageView iconImage = new ImageView(context);
        iconImage.setImageResource(iconResId);
        {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2);
            params.gravity = Gravity.CENTER;
            imageContainer.addView(iconImage, params);
        }

        View redDotView = new View(context);
        redDotView.setBackgroundDrawable(new ShapeDrawable(new RoundCornerShape(0xFFEC1919, dp2px(this, 3))));
        {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
            params.addRule(ALIGN_PARENT_RIGHT);
            params.topMargin = dp2px(this, 6);
            params.rightMargin = dp2px(this, 16);
            addView(redDotView, params);
        }

        mRedDotView = redDotView;
        setRedDotViewVisibility(GONE);
    }

    public void setRedDotViewVisibility(int visibility) {
        mRedDotView.setVisibility(visibility);
    }


}
