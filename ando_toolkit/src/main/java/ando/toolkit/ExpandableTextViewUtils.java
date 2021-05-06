package ando.toolkit;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.text.Layout;
import android.text.NoCopySpan;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.AlignmentSpan;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;

/**
 * https://github.com/MrTrying/ExpandableText-Example
 * <p>
 * https://juejin.cn/post/6844903952757047309
 */
public class ExpandableTextViewUtils {
    private static final String TAG = ExpandableTextViewUtils.class.getSimpleName();

    public static final String ELLIPSIS_STRING = new String(new char[]{'\u2026'});
    private static final int DEFAULT_MAX_LINE = 3;
    private static final String DEFAULT_OPEN_SUFFIX = " 展开";
    private static final String DEFAULT_CLOSE_SUFFIX = " 收起";
    volatile boolean animating = false;
    boolean isClosed = false;
    private final TextView mTextView;
    private int mMaxLines = DEFAULT_MAX_LINE;
    /**
     * TextView可展示宽度，包含paddingLeft和paddingRight
     */
    private int initWidth = 0;
    private SpannableStringBuilder mOpenSpannableStr, mCloseSpannableStr;

    private boolean isOpenOrCloseByUserHandle = false;//false 自动触发"展开/收起";true 自定义事件控制
    private boolean hasAnimation = false;
    private Animation mOpenAnim, mCloseAnim;
    private int mOpenHeight, mCloseHeight;
    private boolean mExpandable;
    private boolean mCloseInNewLine;
    @Nullable
    private SpannableString mOpenSuffixSpan, mCloseSuffixSpan;
    private String mOpenSuffixStr = DEFAULT_OPEN_SUFFIX;
    private String mCloseSuffixStr = DEFAULT_CLOSE_SUFFIX;
    private int mOpenSuffixColor, mCloseSuffixColor;

    private CharSequenceToSpannableHandler mCharSequenceToSpannableHandler;
    private OnStateListener mStateListener;
    private OnClickListener mClickListener;

    public static ExpandableTextViewUtils obtain(TextView textView) {
        return new ExpandableTextViewUtils(textView);
    }

    public ExpandableTextViewUtils(TextView textView) {
        this.mTextView = textView;
        initialize();
    }

    /**
     * 初始化
     */
    private void initialize() {
        mOpenSuffixColor = mCloseSuffixColor = Color.parseColor("#F23030");
        this.mTextView.setMovementMethod(OverLinkMovementMethod.getInstance());
        this.mTextView.setIncludeFontPadding(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.mTextView.forceHasOverlappingRendering(false);
        }
        updateOpenSuffixSpan();
        updateCloseSuffixSpan();
    }

    public ExpandableTextViewUtils setOriginalText(CharSequence originalText) {
        mExpandable = false;
        mCloseSpannableStr = new SpannableStringBuilder();
        final int maxLines = mMaxLines;
        SpannableStringBuilder tempText = charSequenceToSpannable(originalText);
        mOpenSpannableStr = charSequenceToSpannable(originalText);

        if (maxLines != -1) {
            Layout layout = createStaticLayout(tempText);
            mExpandable = layout.getLineCount() > maxLines;
            if (mExpandable) {
                //拼接展开内容
                if (mCloseInNewLine) {
                    mOpenSpannableStr.append("\n");
                }
                if (mCloseSuffixSpan != null) {
                    mOpenSpannableStr.append(mCloseSuffixSpan);
                }
                //计算原文截取位置
                int endPos = layout.getLineEnd(maxLines - 1);
                if (originalText.length() <= endPos) {
                    mCloseSpannableStr = charSequenceToSpannable(originalText);
                } else {
                    mCloseSpannableStr = charSequenceToSpannable(originalText.subSequence(0, endPos));
                }
                SpannableStringBuilder tempText2 = charSequenceToSpannable(mCloseSpannableStr).append(ELLIPSIS_STRING);
                if (mOpenSuffixSpan != null) {
                    tempText2.append(mOpenSuffixSpan);
                }
                //循环判断，收起内容添加展开后缀后的内容
                Layout tempLayout = createStaticLayout(tempText2);
                while (tempLayout.getLineCount() > maxLines) {
                    int lastSpace = mCloseSpannableStr.length() - 1;
                    if (lastSpace == -1) {
                        break;
                    }
                    if (originalText.length() <= lastSpace) {
                        mCloseSpannableStr = charSequenceToSpannable(originalText);
                    } else {
                        mCloseSpannableStr = charSequenceToSpannable(originalText.subSequence(0, lastSpace));
                    }
                    tempText2 = charSequenceToSpannable(mCloseSpannableStr).append(ELLIPSIS_STRING);
                    if (mOpenSuffixSpan != null) {
                        tempText2.append(mOpenSuffixSpan);
                    }
                    tempLayout = createStaticLayout(tempText2);

                }
                int lastSpace = mCloseSpannableStr.length() - mOpenSuffixSpan.length();
                if (lastSpace >= 0 && originalText.length() > lastSpace) {
                    CharSequence redundantChar = originalText.subSequence(lastSpace, lastSpace + mOpenSuffixSpan.length());
                    int offset = hasEnCharCount(redundantChar) - hasEnCharCount(mOpenSuffixSpan) + 1;
                    lastSpace = offset <= 0 ? lastSpace : lastSpace - offset;
                    mCloseSpannableStr = charSequenceToSpannable(originalText.subSequence(0, lastSpace));
                }
                //计算收起的文本高度
                mCloseHeight = tempLayout.getHeight() + this.mTextView.getPaddingTop() + this.mTextView.getPaddingBottom();

                mCloseSpannableStr.append(ELLIPSIS_STRING);
                if (mOpenSuffixSpan != null) {
                    mCloseSpannableStr.append(mOpenSuffixSpan);
                }
            }
        }
        isClosed = mExpandable;
        if (mExpandable) {
            this.mTextView.setText(mCloseSpannableStr);
            //设置监听
            this.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onViewClick(v);
                    }
                }
            });
        } else {
            mTextView.setText(mOpenSpannableStr);
        }
        return this;
    }

    private int hasEnCharCount(CharSequence str) {
        int count = 0;
        if (!TextUtils.isEmpty(str)) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c >= ' ' && c <= '~') {
                    count++;
                }
            }
        }
        return count;
    }

    public void switchOpenClose() {
        if (mExpandable) {
            isClosed = !isClosed;
            if (isClosed) {
                close();
            } else {
                open();
            }
        }
    }

    /**
     * 设置是否有动画
     */
    public ExpandableTextViewUtils setHasAnimation(boolean hasAnimation) {
        this.hasAnimation = hasAnimation;
        return this;
    }

    /**
     * 展开
     */
    private void open() {
        if (hasAnimation) {
            Layout layout = createStaticLayout(mOpenSpannableStr);
            mOpenHeight = layout.getHeight() + this.mTextView.getPaddingTop() + this.mTextView.getPaddingBottom();
            executeOpenAnim();
        } else {
            this.mTextView.setMaxLines(Integer.MAX_VALUE);
            this.mTextView.setText(mOpenSpannableStr);
            if (mStateListener != null) {
                mStateListener.onOpen();
            }
        }
    }

    /**
     * 收起
     */
    private void close() {
        if (hasAnimation) {
            executeCloseAnim();
        } else {
            this.mTextView.setMaxLines(mMaxLines);
            this.mTextView.setText(mCloseSpannableStr);
            if (mStateListener != null) {
                mStateListener.onClose();
            }
        }
    }

    /**
     * 执行展开动画
     */
    private void executeOpenAnim() {
        //创建展开动画
        if (mOpenAnim == null) {
            mOpenAnim = new ExpandCollapseAnimation(mTextView, mCloseHeight, mOpenHeight);
            mOpenAnim.setFillAfter(true);
            mOpenAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mTextView.setMaxLines(Integer.MAX_VALUE);
                    mTextView.setText(mOpenSpannableStr);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //  动画结束后textview设置展开的状态
                    mTextView.getLayoutParams().height = mOpenHeight;
                    mTextView.requestLayout();
                    animating = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        if (animating) {
            return;
        }
        animating = true;
        mTextView.clearAnimation();
        mTextView.startAnimation(mOpenAnim);
    }

    /**
     * 执行收起动画
     */
    private void executeCloseAnim() {
        //创建收起动画
        if (mCloseAnim == null) {
            mCloseAnim = new ExpandCollapseAnimation(mTextView, mOpenHeight, mCloseHeight);
            mCloseAnim.setFillAfter(true);
            mCloseAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animating = false;
                    mTextView.setMaxLines(mMaxLines);
                    mTextView.setText(mCloseSpannableStr);
                    mTextView.getLayoutParams().height = mCloseHeight;
                    mTextView.requestLayout();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        if (animating) {
            return;
        }
        animating = true;
        mTextView.clearAnimation();
        mTextView.startAnimation(mCloseAnim);
    }

    @SuppressLint("ObsoleteSdkInt")
    @SuppressWarnings("deprecation")
    private Layout createStaticLayout(SpannableStringBuilder spannable) {
        int contentWidth = initWidth - mTextView.getPaddingLeft() - mTextView.getPaddingRight();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder builder = StaticLayout.Builder.obtain(spannable, 0, spannable.length(), mTextView.getPaint(), contentWidth);
            builder.setAlignment(Layout.Alignment.ALIGN_NORMAL);
            builder.setIncludePad(mTextView.getIncludeFontPadding());
            builder.setLineSpacing(mTextView.getLineSpacingExtra(), mTextView.getLineSpacingMultiplier());
            return builder.build();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return new StaticLayout(spannable, mTextView.getPaint(), contentWidth, Layout.Alignment.ALIGN_NORMAL,
                    mTextView.getLineSpacingMultiplier(), mTextView.getLineSpacingExtra(), mTextView.getIncludeFontPadding());
        } else {//兼容16以下版本
            return new StaticLayout(spannable, mTextView.getPaint(), contentWidth, Layout.Alignment.ALIGN_NORMAL,
                    getFloatField("mSpacingMult", 1F), getFloatField("mSpacingAdd", 0F), mTextView.getIncludeFontPadding());
        }
    }

    private float getFloatField(String fieldName, float defaultValue) {
        float value = defaultValue;
        if (TextUtils.isEmpty(fieldName)) {
            return value;
        }
        try {
            // 获取该类的所有属性值域
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (TextUtils.equals(fieldName, field.getName())) {
                    value = field.getFloat(this);
                    break;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }

    private SpannableStringBuilder charSequenceToSpannable(@NonNull CharSequence charSequence) {
        SpannableStringBuilder spannableStringBuilder = null;
        if (mCharSequenceToSpannableHandler != null) {
            spannableStringBuilder = mCharSequenceToSpannableHandler.charSequenceToSpannable(charSequence);
        }
        if (spannableStringBuilder == null) {
            spannableStringBuilder = new SpannableStringBuilder(charSequence);
        }
        return spannableStringBuilder;
    }

    /**
     * 初始化TextView的可展示宽度
     */
    public ExpandableTextViewUtils initWidth(int width) {
        initWidth = width;
        return this;
    }

    public ExpandableTextViewUtils setMaxLines(int maxLines) {
        this.mMaxLines = maxLines;
        mTextView.setMaxLines(maxLines);
        return this;
    }

    /**
     * 设置展开后缀text
     */
    public ExpandableTextViewUtils setOpenSuffix(String openSuffix) {
        mOpenSuffixStr = openSuffix;
        updateOpenSuffixSpan();
        return this;
    }

    /**
     * 设置展开后缀文本颜色
     */
    public ExpandableTextViewUtils setOpenSuffixColor(@ColorInt int openSuffixColor) {
        mOpenSuffixColor = openSuffixColor;
        updateOpenSuffixSpan();
        return this;
    }

    /**
     * 设置收起后缀text
     */
    public ExpandableTextViewUtils setCloseSuffix(String closeSuffix) {
        mCloseSuffixStr = closeSuffix;
        updateCloseSuffixSpan();
        return this;
    }

    /**
     * 设置收起后缀文本颜色
     */
    public ExpandableTextViewUtils setCloseSuffixColor(@ColorInt int closeSuffixColor) {
        mCloseSuffixColor = closeSuffixColor;
        updateCloseSuffixSpan();
        return this;
    }

    /**
     * 收起后缀是否另起一行
     */
    public ExpandableTextViewUtils setCloseInNewLine(boolean closeInNewLine) {
        mCloseInNewLine = closeInNewLine;
        updateCloseSuffixSpan();
        return this;
    }

    public ExpandableTextViewUtils setOpenOrCloseByUserHandle(boolean isHandleByUser) {
        this.isOpenOrCloseByUserHandle = isHandleByUser;
        return this;
    }

    /**
     * 设置文本内容处理
     */
    public ExpandableTextViewUtils setCharSequenceToSpannableHandler(CharSequenceToSpannableHandler handler) {
        mCharSequenceToSpannableHandler = handler;
        return this;
    }

    public ExpandableTextViewUtils setOnStateListener(OnStateListener listener) {
        this.mStateListener = listener;
        return this;
    }

    public ExpandableTextViewUtils setOnClickListener(OnClickListener listener) {
        this.mClickListener = listener;
        return this;
    }

    /**
     * 更新展开后缀Spannable
     */
    private void updateOpenSuffixSpan() {
        if (TextUtils.isEmpty(mOpenSuffixStr)) {
            mOpenSuffixSpan = null;
            return;
        }
        mOpenSuffixSpan = new SpannableString(mOpenSuffixStr);
        mOpenSuffixSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, mOpenSuffixStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mOpenSuffixSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (!isOpenOrCloseByUserHandle) {
                    switchOpenClose();
                }
                if (mClickListener != null) {
                    mClickListener.onOpenClick();
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(mOpenSuffixColor);
                ds.setUnderlineText(false);
            }
        }, 0, mOpenSuffixStr.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    }

    /**
     * 更新收起后缀Spannable
     */
    private void updateCloseSuffixSpan() {
        if (TextUtils.isEmpty(mCloseSuffixStr)) {
            mCloseSuffixSpan = null;
            return;
        }
        mCloseSuffixSpan = new SpannableString(mCloseSuffixStr);
        mCloseSuffixSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, mCloseSuffixStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (mCloseInNewLine) {
            AlignmentSpan alignmentSpan = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE);
            mCloseSuffixSpan.setSpan(alignmentSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mCloseSuffixSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (!isOpenOrCloseByUserHandle) {
                    switchOpenClose();
                }
                if (mClickListener != null) {
                    mClickListener.onCloseClick();
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(mCloseSuffixColor);
                ds.setUnderlineText(false);
            }
        }, 1, mCloseSuffixStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public interface OnStateListener {
        void onOpen();

        void onClose();
    }

    public interface OnClickListener {
        void onOpenClick();

        void onCloseClick();

        void onViewClick(@NonNull View v);
    }

    public interface CharSequenceToSpannableHandler {
        @NonNull
        SpannableStringBuilder charSequenceToSpannable(CharSequence charSequence);
    }

    /**
     * 修复文本可滑动问题
     */
    private static class OverLinkMovementMethod extends LinkMovementMethod {

        public static boolean canScroll = false;

        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_MOVE) {
                if (!canScroll) {
                    return true;
                }
            }
            return super.onTouchEvent(widget, buffer, event);
        }

        public static MovementMethod getInstance() {
            if (sInstance == null) {
                sInstance = new OverLinkMovementMethod();
            }

            return sInstance;
        }

        private static OverLinkMovementMethod sInstance;
        private static Object FROM_BELOW = new NoCopySpan.Concrete();
    }

    private static class ExpandCollapseAnimation extends Animation {
        private final View mTargetView;//动画执行view
        private final int mStartHeight;//动画执行的开始高度
        private final int mEndHeight;//动画结束后的高度

        ExpandCollapseAnimation(View target, int startHeight, int endHeight) {
            mTargetView = target;
            mStartHeight = startHeight;
            mEndHeight = endHeight;
            setDuration(400);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            mTargetView.setScrollY(0);
            //计算出每次应该显示的高度,改变执行view的高度，实现动画
            mTargetView.getLayoutParams().height = (int) ((mEndHeight - mStartHeight) * interpolatedTime + mStartHeight);
            mTargetView.requestLayout();
        }
    }
}