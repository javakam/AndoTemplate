package ando.widget.indicator;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

/**
 * 简化和ViewPager绑定
 */
public class ViewPagerHelper {

    public static void bind(@NonNull final MagicIndicator magicIndicator, @NonNull ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                magicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);
            }
        });
    }
}
