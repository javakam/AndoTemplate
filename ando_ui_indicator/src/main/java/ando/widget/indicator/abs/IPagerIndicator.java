package ando.widget.indicator.abs;

import java.util.List;

import ando.widget.indicator.indicators.PositionData;

/**
 * 抽象的viewpager指示器，适用于CommonNavigator
 */
public interface IPagerIndicator {
    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void onPageSelected(int position);

    void onPageScrollStateChanged(int state);

    void onIndicatorPositionProvide(List<PositionData> dataList);
}