package ando.repo.widget.indicator;

import androidx.viewpager.widget.ViewPager;

import java.util.List;

import ando.repo.widget.indicator.titles.OnTabClickListener;
import ando.widget.indicator.navigator.abs.CommonNavigatorAdapter;

public abstract class BaseCommonNavigatorAdapter<T> extends CommonNavigatorAdapter {

    protected ViewPager mViewPager;
    protected List<T> mData;
    protected OnTabClickListener onTabClickListener;

    public BaseCommonNavigatorAdapter() {
    }

    public BaseCommonNavigatorAdapter(ViewPager mViewPager) {
        this.mViewPager = mViewPager;
    }

    public void setData(List<T> data) {
        this.mData = data;
    }

    public void updateData(List<T> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
    }

    public OnTabClickListener getOnTabClickListener() {
        return onTabClickListener;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

}