package ando.library.widget.edittext;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Simple implementation of the {@link TextWatcher} interface with stub
 * implementations of each method. Extend this if you do not intend to override
 * every method of {@link TextWatcher}.
 * </p>
 * Author javakam
 * Date 2018/11/2 9:54
 */
public class SimpleTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // This space for rent
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // This space for rent
    }

    @Override
    public void afterTextChanged(Editable s) {
        // This space for rent
    }
}