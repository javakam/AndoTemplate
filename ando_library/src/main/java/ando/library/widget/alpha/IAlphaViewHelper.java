package ando.library.widget.alpha;

import android.view.View;

/**
 * 透明度辅助工具
 *
 * @author javakam
 * @date 2018/11/30 下午1:42
 */
public interface IAlphaViewHelper {

    /**
     * 处理setPressed
     */
    void onPressedChanged(View current, boolean pressed);

    /**
     * 处理setEnabled
     */
    void onEnabledChanged(View current, boolean enabled);

    /**
     * 设置是否要在 press 时改变透明度
     *
     * @param changeAlphaWhenPress 是否要在 press 时改变透明度
     */
    void setChangeAlphaWhenPress(boolean changeAlphaWhenPress);

    /**
     * 设置是否要在 disabled 时改变透明度
     *
     * @param changeAlphaWhenDisable 是否要在 disabled 时改变透明度
     */
    void setChangeAlphaWhenDisable(boolean changeAlphaWhenDisable);
}
