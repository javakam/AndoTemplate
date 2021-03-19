package ando.repo.ui.indicator;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;

import androidx.core.content.ContextCompat;

import ando.repo.R;
import ando.repo.bean.ChannelBean;
import ando.repo.widget.indicator.BaseCommonNavigatorAdapter;
import ando.repo.widget.indicator.MagicIndicatorHelper;
import ando.repo.widget.indicator.PagerIndicatorProvider;
import ando.repo.widget.indicator.titles.CustomScaleTransitionPagerTitleView;
import ando.toolkit.ResUtils;
import ando.widget.indicator.abs.IPagerIndicator;
import ando.widget.indicator.abs.IPagerTitleView;

/**
 * # IndicatorNavAdapter
 *
 * @author javakam
 * @date 2019/11/11  10:14
 */
public class IndicatorFixedNavAdapter extends BaseCommonNavigatorAdapter<ChannelBean> {

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        final int textSize = R.dimen.font_15;

        final CustomScaleTransitionPagerTitleView titleView = new CustomScaleTransitionPagerTitleView(context);
        boolean isMoreThanThree = mData.size() > MagicIndicatorHelper.sAdjustModeThresholdThree;
        if (isMoreThanThree) {
            titleView.setMinWidth(ResUtils.INSTANCE.getDimensionPixelSize(R.dimen.dp_100));
        }
        titleView.disableTextScale();//关闭文字缩放
        titleView.setText(mData.get(index).getTitle());
        titleView.setTextBoldWhenSelected(true);
        //自带字体缩放效果,所以不需要设置选中和未选中字体大小
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelOffset(textSize));
        titleView.setNormalColor(ContextCompat.getColor(context, R.color.black));
        titleView.setSelectedColor(ContextCompat.getColor(context, R.color.white));
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

    //注意: `IPagerIndicator高度` 必须和 `xml中的MagicIndicator` 相同
    @Override
    public IPagerIndicator getIndicator(Context context) {
        return PagerIndicatorProvider.getLineIndicator(context);
    }

}