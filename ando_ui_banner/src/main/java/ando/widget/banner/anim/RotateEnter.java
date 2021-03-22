package ando.widget.banner.anim;

import android.animation.ObjectAnimator;
import android.view.View;

/**
 * @author javakam
 * @date 2018/11/25 下午6:51
 */
public class RotateEnter extends BaseAnimator {

    public RotateEnter() {
        this.mDuration = 200;
    }

    @Override
    public void setAnimation(View view) {
        this.mAnimatorSet.playTogether(ObjectAnimator.ofFloat(view, "rotation", 0, 180));
    }
}
