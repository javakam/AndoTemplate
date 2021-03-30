package ando.repo.widget.indicator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import ando.library.base.BaseStatePagersAdapter;
import ando.repo.R;
import ando.repo.bean.ChannelBean;
import ando.repo.ui.indicator.adapter.IndicatorFragmentAdapter;
import ando.repo.ui.indicator.adapter.IndicatorScrollNavAdapter;
import ando.toolkit.ActivityUtils;
import ando.toolkit.ResUtils;
import ando.widget.indicator.MagicIndicator;
import ando.widget.indicator.ViewPagerHelper;
import ando.widget.indicator.abs.IPagerIndicator;
import ando.widget.indicator.abs.IPagerTitleView;
import ando.widget.indicator.indicators.LinePagerIndicator;
import ando.widget.indicator.navigator.CommonNavigator;
import ando.widget.indicator.navigator.titles.ColorTransitionPagerTitleView;
import ando.widget.indicator.navigator.titles.SimplePagerTitleView;

/**
 * # MagicIndicatorHelper
 *
 * @author javakam
 * @date 2020/1/8  15:50
 */
public class MagicIndicatorHelper {

    private final Context context;
    private final FragmentManager mFragmentManager;

    private MagicIndicator mIndicator;
    private ViewPager mViewPager;

    public MagicIndicatorHelper(Context context, FragmentManager fm) {
        this.context = context;
        this.mFragmentManager = fm;
    }

    public void bind(MagicIndicator magicIndicator, ViewPager viewPager) {
        this.mIndicator = magicIndicator;
        this.mViewPager = viewPager;
    }

    private <T> CommonNavigator initIndicator(BaseCommonNavigatorAdapter<T> navigatorAdapter, BaseStatePagersAdapter pagerAdapter,
                                              List<T> data, List<String> titles, List<Fragment> fragments, int adjustModeThreshold, boolean adjustMode) {
        if (fragments == null || mIndicator == null) {
            return null;
        }
        //Navigator
        final CommonNavigator commonNavigator = new CommonNavigator(context);
        // 自适应模式，适用于数目固定的、少量的title  , 多于 adjustModeThreshold 个 Tab 则滑动显示
        if (data.size() <= adjustModeThreshold || adjustMode) {
            commonNavigator.setAdjustMode(true);
        }

        commonNavigator.setSkimOver(false);
        commonNavigator.setAdapter(navigatorAdapter);

        //PagerAdapter
        //mViewPager.setOffscreenPageLimit(mChannels.size());//缓存所有子页
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(pagerAdapter);
        mIndicator.setNavigator(commonNavigator);

        //mFragmentContainerHelper.attachMagicIndicator(mIndicator);
        ViewPagerHelper.bind(mIndicator, mViewPager);

        navigatorAdapter.setData(data);
        pagerAdapter.updateData(fragments, titles);
        mViewPager.setCurrentItem(0, false);
        return commonNavigator;
    }

    /**
     * 顶部导航 + Fragment + 不带ViewPager
     */
    public <T> void initIndicatorNoVp(Activity activity, BaseCommonNavigatorAdapter<T> navigatorAdapter) {
        if (!ActivityUtils.INSTANCE.isActivityLive(activity) || navigatorAdapter == null || mIndicator == null) {
            return;
        }
        CommonNavigator commonNavigator = new CommonNavigator(activity);
        commonNavigator.setScrollPivotX(0.65f);
        commonNavigator.setAdjustMode(true); // 自适应模式，适用于数目固定的、少量的title
        commonNavigator.setAdapter(navigatorAdapter);
        mIndicator.setNavigator(commonNavigator);
    }

    /**
     * 顶部导航 + Fragment + ViewPager
     */
    public void initIndicatorStyle1(List<ChannelBean> channels, List<Fragment> fragments) {
        if (fragments == null || mIndicator == null) {
            return;
        }

        List<String> titles = new ArrayList<>();
        for (ChannelBean channel : channels) {
            titles.add(channel.getTitle());
        }
        IndicatorScrollNavAdapter navAdapter = new IndicatorScrollNavAdapter();
        navAdapter.setViewPager(mViewPager);
        initIndicator(navAdapter, new IndicatorFragmentAdapter(mFragmentManager), channels, titles, fragments,
                3, true);
    }

    public void initIndicatorStyle2(List<ChannelBean> channels, List<Fragment> fragments) {
        if (fragments == null || mIndicator == null) {
            return;
        }

        List<String> titles = new ArrayList<>();
        for (ChannelBean channel : channels) {
            titles.add(channel.getTitle());
        }
        final BaseCommonNavigatorAdapter<ChannelBean> navAdapter = new BaseCommonNavigatorAdapter<ChannelBean>() {

            @Override
            public int getCount() {
                return channels.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView titleView = new ColorTransitionPagerTitleView(context);
                titleView.setMinWidth(ResUtils.INSTANCE.getDimensionPixelSize(R.dimen.dp_100));
                titleView.setNormalColor(Color.GRAY);
                titleView.setSelectedColor(Color.BLACK);
                titleView.setText(channels.get(index).getTitle());
                titleView.setOnClickListener(v -> mViewPager.setCurrentItem(index));
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_MATCH_EDGE);
                linePagerIndicator.setColors(ContextCompat.getColor(context, R.color.color_main_blue));
                return linePagerIndicator;
            }
        };

        final CommonNavigator commonNavigator =
                initIndicator(navAdapter, new IndicatorFragmentAdapter(mFragmentManager), channels, titles, fragments,
                        4, false);
        //分割线
        if (commonNavigator != null) {
            LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
            titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            titleContainer.setDividerPadding(28);
            titleContainer.setDividerDrawable(ContextCompat.getDrawable(context, R.drawable.shape_indicator_splitter));
        }
    }

//    /**
//     * 登录
//     */
//    public void initIndicatorLogin(List<ChannelBean> channels, List<BaseFragment> fragments) {
//        if (fragments == null || mIndicator == null) {
//            return;
//        }
//
//        List<String> titles = new ArrayList<>();
//        for (ChannelBean channel : channels) {
//            titles.add(channel.getTitle());
//        }
//        initIndicator(new LoginNavAdapter(mViewPager), new ChannelPagerAdapter(mFragmentManager), channels, titles, fragments, 4, false);
//    }

}