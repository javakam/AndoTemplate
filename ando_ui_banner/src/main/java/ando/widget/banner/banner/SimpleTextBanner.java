package ando.widget.banner.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import ando.widget.banner.R;
import ando.widget.banner.banner.base.BaseIndicatorBanner;

/**
 * 简单的文字轮播
 *
 * @author javakam
 * @date 2018/12/6 下午4:35
 */
public class SimpleTextBanner extends BaseIndicatorBanner<String, SimpleTextBanner> {
    public SimpleTextBanner(Context context) {
        super(context);
    }

    public SimpleTextBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleTextBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onTitleSelect(TextView tv, int position) {
    }

    @Override
    public View onCreateItemView(int position) {
        View inflate = inflate(mContext, R.layout.banner_adapter_simple_text, null);
        TextView tv = inflate.findViewById(R.id.tv_banner);
        tv.setText(mData.get(position));
        return inflate;
    }

    @Override
    protected void onDetachedFromWindow() {
        //解决内存泄漏的问题
        pauseScroll();
        super.onDetachedFromWindow();
    }
}