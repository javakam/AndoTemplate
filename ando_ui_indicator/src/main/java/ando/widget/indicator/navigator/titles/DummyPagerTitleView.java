package ando.widget.indicator.navigator.titles;

import android.content.Context;
import android.view.View;

import ando.widget.indicator.navigator.abs.IPagerTitleView;

/**
 * 空指示器标题，用于只需要指示器而不需要title的需求
 */
public class DummyPagerTitleView extends View implements IPagerTitleView {

    public DummyPagerTitleView(Context context) {
        super(context);
    }

    @Override
    public void onSelected(int index, int totalCount) {
    }

    @Override
    public void onDeselected(int index, int totalCount) {
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
    }
}
