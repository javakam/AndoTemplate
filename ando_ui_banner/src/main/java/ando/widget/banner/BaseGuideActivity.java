package ando.widget.banner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import ando.widget.banner.anim.ZoomInEnter;
import ando.widget.banner.widget.banner.SimpleGuideBanner;

/**
 * 启动引导页
 *
 * @author javakam
 * @date 2018/11/27 下午4:49
 */
public abstract class BaseGuideActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 2021年3月22日 17:06:16
//        ActivityUtils.hideTitleBar(this);
//        ActivityUtils.setFullScreen(this);

        setContentView(R.layout.ando_activity_guide);
        initGuideView(getGuideResourceList(), getSkipClass());
        onCreateActivity();
    }

    /**
     * activity启动后的初始化
     */
    protected void onCreateActivity() {

    }

    /**
     * 获取引导页资源的集合[可以是url也可以是资源id]
     */
    protected abstract List<Object> getGuideResourceList();

    /**
     * 获取跳转activity的类
     */
    protected abstract Class<? extends Activity> getSkipClass();

    /**
     * 初始化引导页动画
     *
     * @param guidesResIdList 引导图片
     * @param cls             点击后跳转的Activity类
     */
    public void initGuideView(List<Object> guidesResIdList, final Class<?> cls) {
        // TODO: 2021年3月22日 17:06:16
        //initGuideView(guidesResIdList, DepthTransformer.class, cls);
    }

    /**
     * 初始化引导页动画
     *
     * @param guidesResIdList  引导图片
     * @param transformerClass 引导图片切换的效果
     * @param cls              点击后跳转的Activity类
     */
    public void initGuideView(List<Object> guidesResIdList, Class<? extends ViewPager.PageTransformer> transformerClass, final Class<?> cls) {
        SimpleGuideBanner sgb = findViewById(R.id.sgb);

        sgb.setIndicatorWidth(6).setIndicatorHeight(6).setIndicatorGap(12)
                .setIndicatorCornerRadius(3.5f)
                .setSelectAnimClass(ZoomInEnter.class)
                .setTransformerClass(transformerClass).barPadding(0, 10, 0, 10)
                .setSource(guidesResIdList).startScroll();

        sgb.setOnJumpClickListener(new SimpleGuideBanner.OnJumpClickListener() {
            @Override
            public void onJumpClick() {
                startActivity(new Intent(BaseGuideActivity.this, cls));
                finish();
            }
        });
    }

    /**
     * 初始化引导页动画
     *
     * @param guidesResIdList  引导图片
     * @param transformerClass 引导图片切换的效果
     */
    public void initGuideView(List<Object> guidesResIdList, Class<? extends ViewPager.PageTransformer> transformerClass, SimpleGuideBanner.OnJumpClickListener onJumpClickListener) {
        SimpleGuideBanner sgb = findViewById(R.id.sgb);

        sgb.setIndicatorWidth(6).setIndicatorHeight(6).setIndicatorGap(12)
                .setIndicatorCornerRadius(3.5f)
                .setSelectAnimClass(ZoomInEnter.class)
                .setTransformerClass(transformerClass).barPadding(0, 10, 0, 10)
                .setSource(guidesResIdList).startScroll();

        sgb.setOnJumpClickListener(onJumpClickListener);
    }
}
