package ando.library.widget.edittext;

import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

/**
 * Title: EditTextHelper {@link SimpleTextWatcher}
 * <p>
 * Description:EditText、清除按钮的简化处理工具类
 * </p>
 * Author javakam
 * Date 2018/11/2  10:10
 */
public class EditTextHelper {

    /**
     * Relationship:TextWatcher->SimpleTextWatcher->CallBack
     */
    public static class CallBack extends SimpleTextWatcher {
        public void onClearClick(View v) {
            // This space for rent
        }
    }

    public static void addTextWatcher(final EditText[] editTexts, final View... clearViews) {
        addTextWatcher(editTexts, clearViews, null);
    }

    public static void addTextWatcher(final EditText[] editTexts, final View[] clearViews,
                                      @Nullable final CallBack callBack) {
        final int len = editTexts.length;
        if (len == clearViews.length) {
            for (int i = 0; i < len; i++) {
                addTextWatcher(editTexts[i], clearViews[i], callBack);
            }
        }
    }

    public static void addTextWatcher(final EditText editText, final View clearView) {
        addTextWatcher(editText, clearView, null);
    }

    public static void addTextWatcher(final EditText editText, final View clearView
            , @Nullable final CallBack callBack) {
        editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (callBack != null) {
                    callBack.beforeTextChanged(s, start, count, after);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onChanged(s.toString().trim().length(), clearView);
                if (callBack != null) {
                    callBack.onTextChanged(s, start, before, count);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                afterChanged(s, clearView);
                if (callBack != null) {
                    callBack.afterTextChanged(s);
                }
            }
        });
        clearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editText.getText().toString().trim())) {
                    editText.setText("");
                    clearView.setVisibility(View.GONE);
                    if (callBack != null) {
                        callBack.onClearClick(v);
                    }
                }
            }
        });
    }

    private static void onChanged(int length, View clearView) {
        if (length > 0 && clearView.getVisibility() == View.GONE) {
            clearView.setVisibility(View.VISIBLE);
        } else {
            clearView.setVisibility(View.GONE);
        }
    }

    private static void afterChanged(Editable s, final View clearView) {
        if (s.length() > 0) {
            clearView.setVisibility(View.VISIBLE);
        }
    }
}
