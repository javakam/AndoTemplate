package ando.indicator.example;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.Arrays;
import java.util.List;

import ando.widget.indicator.MagicIndicator;
import ando.widget.indicator.ViewPagerHelper;
import ando.widget.indicator.abs.CommonNavigatorAdapter;
import ando.widget.indicator.abs.IPagerIndicator;
import ando.widget.indicator.abs.IPagerTitleView;
import ando.widget.indicator.navigator.CommonNavigator;
import ando.widget.indicator.navigator.titles.CommonPagerTitleView;

public class LoadCustomLayoutExampleActivity extends AppCompatActivity {
    private static final String[] CHANNELS = new String[]{"NOUGAT", "DONUT", "ECLAIR", "KITKAT"};
    private List<String> mDataList = Arrays.asList(CHANNELS);
    private ExamplePagerAdapter mExamplePagerAdapter = new ExamplePagerAdapter(mDataList);

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_custom_layout_example);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mExamplePagerAdapter);

        initMagicIndicator1();
    }

    private void initMagicIndicator1() {
        MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator1);
        magicIndicator.setBackgroundColor(Color.BLACK);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(context);

                // load custom layout
                View customLayout = LayoutInflater.from(context).inflate(R.layout.simple_pager_title_layout, null);
                final ImageView titleImg = (ImageView) customLayout.findViewById(R.id.title_img);
                final TextView titleText = (TextView) customLayout.findViewById(R.id.title_text);
                titleImg.setImageResource(android.R.mipmap.sym_def_app_icon);
                titleText.setText(mDataList.get(index));
                commonPagerTitleView.setContentView(customLayout);

                commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {

                    @Override
                    public void onSelected(int index, int totalCount) {
                        titleText.setTextColor(Color.WHITE);
                    }

                    @Override
                    public void onDeselected(int index, int totalCount) {
                        titleText.setTextColor(Color.LTGRAY);
                    }

                    @Override
                    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
                        titleImg.setScaleX(1.3f + (0.8f - 1.3f) * leavePercent);
                        titleImg.setScaleY(1.3f + (0.8f - 1.3f) * leavePercent);
                    }

                    @Override
                    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
                        titleImg.setScaleX(0.8f + (1.3f - 0.8f) * enterPercent);
                        titleImg.setScaleY(0.8f + (1.3f - 0.8f) * enterPercent);
                    }
                });

                commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });

                return commonPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }
}
