package ando.repo.widget.indicator;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import ando.library.base.BaseFragment;
import ando.library.base.BaseStatePagersAdapter;
import ando.toolkit.ActivityUtils;
import ando.widget.indicator.MagicIndicator;
import ando.widget.indicator.ViewPagerHelper;
import ando.widget.indicator.navigator.CommonNavigator;

/**
 * Title: MagicIndicatorHelper
 * <p>
 * Description:
 * </p>
 *
 * @author javakam
 * @date 2020/1/8  15:50
 */
public class MagicIndicatorHelper {

    private Context context;
    private FragmentManager mFragmentManager;
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
                                   List<T> data, List<String> titles, List<BaseFragment> fragments, int adjustModeThreshold, boolean adjustMode) {
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

        navigatorAdapter.updateData(data);
        pagerAdapter.updateData(fragments, titles);
        mViewPager.setCurrentItem(0, false);

    }

    private void initIndicator(BaseCommonNavigatorAdapter<String> navigatorAdapter, BaseStatePagersAdapter pagerAdapter,
                               List<String> titles, List<BaseFragment> fragments, boolean adjustMode) {
        if (fragments == null || mIndicator == null) {
            return;
        }
        //Navigator
        final CommonNavigator commonNavigator = new CommonNavigator(context);
        // 自适应模式，适用于数目固定的、少量的title  , 多于三个Tab则滑动显示
        if (titles.size() <= sAdjustModeThresholdFour || adjustMode) {
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

        navigatorAdapter.updateData(titles);
        pagerAdapter.updateData(fragments, titles);
        mViewPager.setCurrentItem(0, false);

    }

    /**
     * 一级分类样式, 没有 ViewPager
     */
    public <T> void initIndicatorHome(Activity activity, BaseCommonNavigatorAdapter<T> navigatorAdapter) {
        if (!ActivityUtils.INSTANCE.isActivityLive(activity) || navigatorAdapter == null || mIndicator == null) {
            return;
        }
        CommonNavigator commonNavigator = new CommonNavigator(activity);
        commonNavigator.setScrollPivotX(0.65f);
        commonNavigator.setAdjustMode(true); // 自适应模式，适用于数目固定的、少量的title
        commonNavigator.setAdapter(navigatorAdapter);
        mIndicator.setNavigator(commonNavigator);
    }

//    /**
//     * 二级分类样式, 带有 ViewPager
//     */
//    public void initIndicatorHomeSecondary(List<Channel.ChildrenBean> childrenChannels, List<BaseFragment> fragments) {
//        if (fragments == null || mIndicator == null) {
//            return;
//        }
//
//        List<String> titles = new ArrayList<>();
//        for (Channel.ChildrenBean channel : childrenChannels) {
//            titles.add(channel.getTitle());
//        }
//        initIndicator(new NewsNavChildAdapter(mViewPager), new ChannelChildPagerAdapter(mFragmentManager), childrenChannels, titles, fragments, sAdjustModeThresholdFour, false);
//    }


//    /**
//     * 登录
//     */
//    public void initIndicatorLogin(List<Channel.ChildrenBean> channels, List<BaseFragment> fragments) {
//        if (fragments == null || mIndicator == null) {
//            return;
//        }
//
//        List<String> titles = new ArrayList<>();
//        for (Channel.ChildrenBean channel : channels) {
//            titles.add(channel.getTitle());
//        }
//        initIndicator(new LoginNavAdapter(mViewPager), new ChannelPagerAdapter(mFragmentManager), channels, titles, fragments, sAdjustModeThresholdFour, false);
//
//    }

    public MagicIndicator getMagicIndicator() {
        return mIndicator;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

}