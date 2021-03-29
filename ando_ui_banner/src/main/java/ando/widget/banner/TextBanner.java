package ando.widget.banner.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import ando.widget.banner.R;

/**
 * 文字轮播
 *
 * @author javakam
 * @date 2018/12/6 下午4:35
 */
public class SimpleTextBanner extends CustomBanner<String> {

    private View itemView;
    private int itemBgColor = -1;

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
    public View onCreateItemView(int position) {
        itemView = inflate(mContext, R.layout.banner_adapter_simple_text, null);
        if (itemBgColor != -1) {
            itemView.setBackgroundColor(itemBgColor);
        }
        TextView tv = itemView.findViewById(R.id.tv_banner);
        tv.setText(mData.get(position));
        return itemView;
    }

    @Override
    protected void onDetachedFromWindow() {
        //解决内存泄漏的问题
        pauseScroll();
        super.onDetachedFromWindow();
    }

    public TextView getTextView() {
        if (itemView != null) {
            return itemView.findViewById(R.id.tv_banner);
        }
        return null;
    }

    public void setItemBgColor(@ColorInt int color) {
        this.itemBgColor = color;
    }

}