package ando.repo.ui;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import ando.library.base.BaseStatePagersAdapter;

/**
 * @author javakam
 * @date 2019/11/4 12:49
 */
public class IndicatorScrollPagerAdapter extends BaseStatePagersAdapter {

    private final FragmentManager mFragmentManager;

    public IndicatorScrollPagerAdapter(FragmentManager fm) {
        //https://blog.csdn.net/c6E5UlI1N/article/details/90307961
        super(fm);
        this.mFragmentManager = fm;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mFragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss();
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //注释掉会出现重新增删碎片,残留旧页面碎片的问题
        // super.destroyItem(container, position, object);
        if (getMFragments() != null && !getMFragments().isEmpty()) {
            final Fragment fragment = getMFragments().get(position);
            mFragmentManager.beginTransaction().hide(fragment).commitAllowingStateLoss();
        }
    }

}