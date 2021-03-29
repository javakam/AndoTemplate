package ando.widget.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ando.widget.banner.loopviewpager.LoopViewPager;

/**
 * 引导页
 *
 * @author javakam
 * @date 2018/12/6 下午4:32
 */
public class GuideBanner extends BaseBanner<Object> {

    public GuideBanner(Context context) {
        this(context, null, 0);
    }

    public GuideBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuideBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, R.style.GuideBanner); //引导页关闭无限轮播
        initBanner();
    }

    protected void initBanner() {
        //不进行自动滚动
        setAutoScrollEnable(false);
        //防止闪烁
        if (mViewPager instanceof LoopViewPager) {
            ((LoopViewPager) mViewPager).setBoundaryCaching(true);
        }
    }

    @Override
    public View onCreateItemView(int position) {
        final View view = inflate(mContext, R.layout.banner_adapter_simple_guide, null);
        ImageView iv = view.findViewById(R.id.iv_banner);
        TextView tvJump = view.findViewById(R.id.tv_banner_jump);

        final Object resId = mData.get(position);
        //head position == 0 ; foot position == mData.size() - 1
        tvJump.setVisibility(position == mData.size() - 1 ? VISIBLE : GONE);
        getImageLoader().loadImage(iv, resId);
        tvJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onJumpClickListener != null) {
                    onJumpClickListener.onJump();
                }
            }
        });
        return view;
    }

    private OnJumpClickListener onJumpClickListener;

    /**
     * 点击跳过或者立即体验的监听
     */
    public interface OnJumpClickListener {
        /**
         * 跳过监听
         */
        void onJump();
    }

    public void setOnJumpClickListener(OnJumpClickListener onJumpClickListener) {
        this.onJumpClickListener = onJumpClickListener;
    }

}