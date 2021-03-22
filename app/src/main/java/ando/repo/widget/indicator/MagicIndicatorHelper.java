package ando.repo.widget.indicator;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import ando.library.base.BaseStatePagersAdapter;
import ando.repo.bean.ChannelBean;
import ando.repo.ui.indicator.IndicatorScrollNavAdapter;
import ando.repo.ui.indicator.IndicatorScrollPagerAdapter;
import ando.toolkit.ActivityUtils;
import ando.widget.indicator.MagicIndicator;
import ando.widget.indicator.ViewPagerHelper;
import ando.widget.indicator.navigator.CommonNavigator;

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

    /**
     * 大于3个渠道
     * <pre>
     *      自适应模式，适用于数目固定的、少量的 title , 多于 Threshold 个 Tab则滑动显示 CommonNavigator.setAdjustMode(true);
     * </pre>
     */
    public static final int sAdjustModeThresholdTwo = 2;
    public static final int sAdjustModeThresholdThree = 3;
    public static final int sAdjustModeThresholdFour = 4;

    public MagicIndicatorHelper(Context context, FragmentManager fm) {
        this.context = context;
        this.mFragmentManager = fm;
    }

    public void bind(MagicIndicator magicIndicator, ViewPager viewPager) {
        this.mIndicator = magicIndicator;
        this.mViewPager = viewPager;
    }

    private <T> void initIndicator(BaseCommonNavigatorAdapter<T> navigatorAdapter, BaseStatePagersAdapter pagerAdapter,
                                   List<T> data, List<String> titles, List<Fragment> fragments, int adjustModeThreshold, boolean adjustMode) {
        if (fragments == null || mIndicator == null) {
            return;
        }
        //Navigator
        final CommonNavigator commonNavigator = new CommonNavigator(context);
        // 自适应模式，适用于数目固定的、少量的title  , 多于三个Tab则滑动显示
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
    public void initIndicatorVp(List<ChannelBean> childrenChannels, List<Fragment> fragments) {
        if (fragments == null || mIndicator == null) {
            return;
        }

        List<String> titles = new ArrayList<>();
        for (ChannelBean channel : childrenChannels) {
            titles.add(channel.getTitle());
        }
        IndicatorScrollNavAdapter navAdapter = new IndicatorScrollNavAdapter();
        navAdapter.setViewPager(mViewPager);
        initIndicator(navAdapter, new IndicatorScrollPagerAdapter(mFragmentManager), childrenChannels, titles, fragments, sAdjustModeThresholdFour, false);
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
//        initIndicator(new LoginNavAdapter(mViewPager), new ChannelPagerAdapter(mFragmentManager), channels, titles, fragments, sAdjustModeThresholdFour, false);
//
//    }

}