package ando.repo.widget.indicator;

import androidx.viewpager.widget.ViewPager;

import java.util.List;

import ando.repo.widget.indicator.titles.OnTabClickListener;
import ando.widget.indicator.abs.CommonNavigatorAdapter;

public abstract class BaseCommonNavigatorAdapter<T> extends CommonNavigatorAdapter {

    private List<T> mData;
    private ViewPager mViewPager;
    private OnTabClickListener onTabClickListener;

    protected List<T> getData() {
        return mData;
    }

    public void addData(List<T> data) {
        this.mData = data;
    }

    public void setData(List<T> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public void setViewPager(ViewPager mViewPager) {
        this.mViewPager = mViewPager;
    }

    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
    }

    public void triggerTabClickEvent(int index) {
        if (this.onTabClickListener != null) {
            this.onTabClickListener.onClick(index);
        }
    }

    public void triggerScrollWithTabClickEvent(int index, boolean smoothScroll) {
        if (this.mViewPager != null) {
            this.mViewPager.setCurrentItem(index, smoothScroll);
        }
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

}