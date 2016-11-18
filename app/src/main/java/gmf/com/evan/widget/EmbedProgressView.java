package gmf.com.evan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import gmf.com.evan.R;

import static gmf.com.evan.extension.ViewExtension.dp2px;

/**
 * Created by Evan on 16/7/18 下午3:54.
 */
public class EmbedProgressView extends RelativeLayout {

    private TextView mMessageLabel;
    private GMFProgressBar mProgressBar;

    public EmbedProgressView(Context context) {
        this(context, null);
    }

    public EmbedProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmbedProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        FrameLayout progressContainer = new FrameLayout(context);
        progressContainer.setId(R.id.progress);
        {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
            params.addRule(CENTER_HORIZONTAL);
            addView(progressContainer, params);
        }

        {
            mProgressBar = new GMFProgressBar(context);
            mProgressBar.setTheme(GMFProgressBar.THEME_DARK);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dp2px(this, 48), dp2px(this, 48));
            params.gravity = Gravity.CENTER;
            progressContainer.addView(mProgressBar, params);
        }

        {
            ImageView loadingArrowImage = new ImageView(context);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2);
            params.gravity = Gravity.CENTER;
            progressContainer.addView(loadingArrowImage, params);
        }

        {
            mMessageLabel = new TextView(context);
            mMessageLabel.setId(R.id.label_title);
            mMessageLabel.setText("页面加载中...");

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
            params.addRule(BELOW, progressContainer.getId());
            params.addRule(CENTER_HORIZONTAL, TRUE);
            if (isInEditMode()) {
                params.topMargin = dp2px(this, 16);
            } else {
                params.topMargin = dp2px(this, 8);
            }
            addView(mMessageLabel, params);
        }
    }

    public void setMessage(CharSequence text) {
        mMessageLabel.setText(text);
    }
}
