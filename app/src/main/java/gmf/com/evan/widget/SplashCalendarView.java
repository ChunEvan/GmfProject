package gmf.com.evan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import gmf.com.evan.R;
import gmf.com.evan.utils.SecondUtil;

import static gmf.com.evan.extension.ViewExtension.v_findView;

/**
 * Created by Evan on 16/7/4 上午11:15.
 */
public class SplashCalendarView extends RelativeLayout {

    private TextView mDayLabel;
    private TextView mWeekNameLabel;
    private TextView mCalendarLabel;
    private TextView mInfosLabel;
    private String strInfos;

    public SplashCalendarView(Context context) {
        this(context, null);
    }

    public SplashCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.widget_calendar_view, this);

        mDayLabel = v_findView(this, R.id.label_text_day);
        mWeekNameLabel = v_findView(this, R.id.label_text_week);
        mCalendarLabel = v_findView(this, R.id.label_text_calendar);
        mInfosLabel = v_findView(this, R.id.label_text_infos);

        if (strInfos != null) {
            mInfosLabel.setText(strInfos);
        }

        mDayLabel.setText(String.valueOf(SecondUtil.currentCalendar().get(Calendar.DAY_OF_MONTH)));
        mWeekNameLabel.setText(SecondUtil.currentWeek());
        String strCalendar = SecondUtil.currentCalendar().get(Calendar.YEAR) + "年";
        if (SecondUtil.currentCalendar().get(Calendar.MONTH) < 10)
            strCalendar += " ";
        strCalendar += (SecondUtil.currentCalendar().get(Calendar.MONTH) + 1) + "月";
        mCalendarLabel.setText(strCalendar);

    }

    public void setInfos(String infos) {
        strInfos = infos;
        if (strInfos != null) {
            mInfosLabel.setText(infos);
        }
    }
}
