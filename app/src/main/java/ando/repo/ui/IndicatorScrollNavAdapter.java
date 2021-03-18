package ando.repo.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.Nullable;

import ando.repo.R;
import ando.repo.bean.ChannelBean;
import ando.repo.widget.indicator.BaseCommonNavigatorAdapter;
import ando.repo.widget.indicator.MagicIndicatorHelper;
import ando.repo.widget.indicator.titles.CustomPagerTitleView;
import ando.toolkit.NoShakeClickListener;
import ando.toolkit.ResUtils;
import ando.widget.indicator.navigator.abs.IPagerIndicator;
import ando.widget.indicator.navigator.abs.IPagerTitleView;

/**
 * # CommonNavigatorAdapter
 *
 * @author javakam
 * @date 2019/11/11  10:14
 */
public class IndicatorScrollNavAdapter extends BaseCommonNavigatorAdapter<ChannelBean> {

    public IndicatorScrollNavAdapter(ViewPager mViewPager) {
        super(mViewPager);
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        CustomPagerTitleView titleView = new CustomPagerTitleView(context);
        titleView.setEnablePadding(true);
        titleView.setText(mData.get(index).getTitle());

        final boolean isMoreThanThree = mData.size() > MagicIndicatorHelper.sAdjustModeThresholdThree;
        if (isMoreThanThree) {
            titleView.setMinWidth(ResUtils.INSTANCE.getDimensionPixelSize(R.dimen.dp_100));
        }
        titleView.setTextSize(isMoreThanThree ? R.dimen.font_13 : R.dimen.font_15);

        titleView.setGravity(Gravity.CENTER);
        titleView.setNormalColor(Color.BLACK);
        titleView.setSelectedColor(Color.WHITE);
        titleView.setSelectedBackground(R.drawable.sel_indicator_bg);//灰色 or 蓝色

        titleView.setOnClickListener(new NoShakeClickListener() {
            @Override
            protected void onSingleClick(@Nullable View v) {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(index, false);
                }
            }
        });
        return titleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        return null;
    }

}