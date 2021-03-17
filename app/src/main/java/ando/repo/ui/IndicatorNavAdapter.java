package ando.repo.ui;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;

import androidx.core.content.ContextCompat;

import ando.repo.R;
import ando.repo.bean.ChannelBean;
import ando.repo.widget.indicator.BaseCommonNavigatorAdapter;
import ando.repo.widget.indicator.PagerIndicatorProvider;
import ando.repo.widget.indicator.titles.CustomScaleTransitionPagerTitleView;
import ando.widget.indicator.navigator.abs.IPagerIndicator;
import ando.widget.indicator.navigator.abs.IPagerTitleView;

/**
 * Title: HomeNavigatorAdapter
 * <p>
 * Description:新闻列表 -> Indicator 适配器
 * </p>
 *
 * @author javakam
 * @date 2019/11/11  10:14
 */
public class IndicatorNavAdapter extends BaseCommonNavigatorAdapter<ChannelBean> {

    private boolean isSecondary;

    public IndicatorNavAdapter(boolean isSecondary) {
        this.isSecondary = isSecondary;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        final int textSize = isSecondary ? R.dimen.font_13 : R.dimen.font_15;

        final CustomScaleTransitionPagerTitleView titleView = new CustomScaleTransitionPagerTitleView(context);
        titleView.disableTextScale();//关闭文字缩放
        titleView.setText(mData.get(index).getTitle());
        titleView.setTextBoldWhenSelected(true);
        //自带字体缩放效果,所以不需要设置选中和未选中字体大小
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelOffset(textSize));
        titleView.setNormalColor(ContextCompat.getColor(context,R.color.black));
        titleView.setSelectedColor(ContextCompat.getColor(context,R.color.white));
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTabClickListener != null) {
                    onTabClickListener.onClick(index);
                }
            }
        });
        return titleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        return isSecondary ? null : PagerIndicatorProvider.getLineIndicator(context);
        //return isSecondary ? PagerIndicatorProvider.getSecondaryLineIndicator(context) : null;
    }

}