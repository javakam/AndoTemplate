package ando.repo.widget.indicator.titles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import ando.repo.R;
import ando.widget.indicator.navigator.titles.CommonPagerTitleView;

/**
 * Title: MainNavigationView
 * <p>
 * Description:底部导航
 * </p>
 *
 * @author javakam
 * @date 2019/11/11  15:31
 */
public class MainNavigationView extends CommonPagerTitleView {

    public MainNavigationView(Context context) {
        super(context);
    }

    public void init(Context context, @DrawableRes int resId, OnPagerTitleChangeListener changeListener, View.OnClickListener clickListener) {
        final View navItem = LayoutInflater.from(context).inflate(R.layout.item_nav_bottom, null);
        final ImageView titleImg = navItem.findViewById(R.id.title_img);
        //设置背景才有效
        //titleImg.setImageResource(resId);
        titleImg.setBackgroundResource(resId);

        if (changeListener == null) {
            setOnPagerTitleChangeListener(new OnPagerTitleChangeListener() {
                @Override
                public void onSelected(int index, int totalCount) {
                    titleImg.setSelected(true);
                }

                @Override
                public void onDeselected(int index, int totalCount) {
                    titleImg.setSelected(false);
                }

                @Override
                public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
                }

                @Override
                public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
                }
            });
        } else {
            setOnPagerTitleChangeListener(changeListener);
        }

        if (clickListener != null) {
            titleImg.setOnClickListener(clickListener);
            setOnClickListener(clickListener);
        }
        setContentView(navItem);
    }
}