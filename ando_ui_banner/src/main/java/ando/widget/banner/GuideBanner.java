package ando.widget.banner.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ando.widget.banner.R;

/**
 * 引导页
 *
 * @author javakam
 * @date 2018/12/6 下午4:32
 */
public class SimpleGuideBanner extends CustomBanner<Object> {

    public SimpleGuideBanner(Context context) {
        this(context, null, 0);
    }

    public SimpleGuideBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleGuideBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initBanner();
    }

    protected void initBanner() {
        setAutoScrollEnable(false); //不进行自动滚动
    }

    @Override
    public View onCreateItemView(int position) {
        final View view = inflate(mContext, R.layout.banner_adapter_simple_guide, null);
        ImageView iv = view.findViewById(R.id.iv_banner);
        TextView tvJump = view.findViewById(R.id.tv_banner_jump);
        TextView tvStart = view.findViewById(R.id.tv_banner_start);

        final Object resId = mData.get(position);
        tvJump.setVisibility(position == 0 ? VISIBLE : GONE);
        tvStart.setVisibility(position == mData.size() - 1 ? VISIBLE : GONE);
        getImageLoader().loadImage(iv, resId);

        tvJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onJumpClickListener != null) {
                    onJumpClickListener.onJumpClick();
                }
            }
        });
        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onJumpClickListener != null) {
                    onJumpClickListener.onJumpClick();
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
        void onJumpClick();
    }

    public void setOnJumpClickListener(OnJumpClickListener onJumpClickListener) {
        this.onJumpClickListener = onJumpClickListener;
    }

}