package ando.repo.widget.indicator;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import ando.repo.R;
import ando.repo.config.GlobalKt;
import ando.repo.widget.indicator.titles.MainNavigationView;
import ando.widget.indicator.IndicatorUtils;
import ando.widget.indicator.abs.IPagerIndicator;
import ando.widget.indicator.abs.IPagerNavigator;
import ando.widget.indicator.abs.IPagerTitleView;
import ando.widget.indicator.indicators.BezierPagerIndicator;
import ando.widget.indicator.indicators.LinePagerIndicator;
import ando.widget.indicator.navigator.titles.CommonPagerTitleView;
import ando.widget.indicator.usage.navigator.CircleNavigator;
import ando.widget.indicator.usage.navigator.ScaleCircleNavigator;
import ando.widget.indicator.usage.navigator.titles.ScaleTransitionPagerTitleView;

/**
 * # IPagerIndicator
 *
 * @author javakam
 * @date 2019/11/11  9:44
 */
public class PagerIndicatorProvider {

    /**
     * 贝塞尔曲线样式
     */
    public static IPagerIndicator getBezierPagerIndicator(Context context) {
        BezierPagerIndicator indicator = new BezierPagerIndicator(context);
        indicator.setColors(Color.parseColor("#ff4a42"),
                Color.parseColor("#fcde64"),
                Color.parseColor("#73e8f4"),
                Color.parseColor("#76b0ff"),
                Color.parseColor("#c683fe"));
        return indicator;
    }


    public static IPagerNavigator getScaleCircleNavigator(Context context, int bannerSize, ScaleCircleNavigator.OnCircleClickListener listener) {
        final ScaleCircleNavigator scaleCircleNavigator = new ScaleCircleNavigator(context);
        scaleCircleNavigator.setCircleCount(bannerSize);
        scaleCircleNavigator.setNormalCircleColor(Color.LTGRAY);
        scaleCircleNavigator.setSelectedCircleColor(Color.DKGRAY);
        scaleCircleNavigator.setCircleClickListener(listener);
        return scaleCircleNavigator;
    }


    /**
     * Channel 一级分类样式
     */
    public static IPagerIndicator getLineIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);

        //设置高度
        float navigatorHeight = GlobalKt.indicatorHeight();
        indicator.setLineHeight(navigatorHeight);
        indicator.setColors(ContextCompat.getColor(context, R.color.color_main_blue));
        return indicator;
    }

    /**
     * 广告轮播图
     */
    public static IPagerNavigator getCircleNavigator(Context context, int bannerSize, CircleNavigator.OnCircleClickListener listener) {
        final CircleNavigator circleNavigator = new CircleNavigator(context);
        circleNavigator.setCircleCount(bannerSize);
        circleNavigator.setCircleColor(Color.RED);
        circleNavigator.setCircleClickListener(listener);
//        magicIndicator.setNavigator(circleNavigator);
//        ViewPagerHelper.bind(magicIndicator, mViewPager);

        //跟随收拾缩放大小
        circleNavigator.setFollowTouch(false);
        return circleNavigator;
    }

    /**
     * 投诉 -> 最热,最新,已回复,处理中,已完成
     */
    public static IPagerIndicator tousuIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        //float borderWidth = IndicatorUtils.dip2px(context, 1);
        //float lineHeight = navigatorHeight - 2 * borderWidth;
        //indicator.setLineHeight(lineHeight);
        //indicator.setRoundRadius(lineHeight / 2);
        //indicator.setYOffset(borderWidth);

        //横线样式
        indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);//自动缩放 MODE_EXACTLY
        indicator.setLineHeight(IndicatorUtils.dip2px(context, 1.5D));//设置高度
        indicator.setRoundRadius(IndicatorUtils.dip2px(context, 2D));
//        indicator.setLineWidth(IndicatorUtils.dip2px(context, 30));
        indicator.setStartInterpolator(new AccelerateInterpolator());
        indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));

        //跳动效果
//        indicator.setYOffset(IndicatorUtils.dip2px(context, 3));
//        indicator.setStartInterpolator(new AccelerateInterpolator());
//        indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));

        indicator.setColors(ContextCompat.getColor(context, R.color.color_main_blue));
        return indicator;
    }

    /* ********************************* IPagerTitleView ************************************** */

    /**
     * 底部导航的 PagerTitle
     */
    public static IPagerTitleView getMainNavigationView(Context context, @DrawableRes int resId,
                                                        CommonPagerTitleView.OnPagerTitleChangeListener changeListener,
                                                        View.OnClickListener clickListener) {
        final MainNavigationView mainNavigationView = new MainNavigationView(context);
        mainNavigationView.init(context, resId, changeListener, clickListener);
        return mainNavigationView;
    }

    /**
     * 带字体缩放效果的 PagerTitle
     * <pre>
     *      注: 若字体颜色也跟着变,使用 com.ando.indicator.navigator.titles.ColorTransitionPagerTitleView
     * </pre>
     */
    public static IPagerTitleView getPagerTitleView(Context context, String text, @DimenRes int textSize, View.OnClickListener listener) {
        final ScaleTransitionPagerTitleView titleView = new ScaleTransitionPagerTitleView(context);
        titleView.setEnableColorTrans(false);
        titleView.setEnableScale(false);
        titleView.setText(text);
        titleView.setTextBoldWhenSelected(true);
        //自带字体缩放效果,所以不需要设置选中和未选中字体大小
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                context.getResources().getDimensionPixelOffset(textSize == 0 ? R.dimen.font_15 : textSize));
        titleView.setNormalColor(Color.GRAY);
        titleView.setSelectedColor(Color.RED);
        titleView.setOnClickListener(listener);
        return titleView;
    }

}