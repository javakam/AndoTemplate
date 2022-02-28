package ando.webview.indicator;

import android.webkit.WebView;

public class WebIndicatorController {

    private IWebIndicator indicator;

    public void progress(WebView v, int newProgress) {
        if (newProgress == 0) {
            reset();
        } else if (newProgress > 0 && newProgress <= 10) {
            show();
        } else if (newProgress > 10 && newProgress < 95) {
            setProgress(newProgress);
        } else {
            setProgress(newProgress);
            finish();
        }
    }

    public void reset() {
        if (indicator != null) {
            indicator.reset();
        }
    }

    public void finish() {
        if (indicator != null) {
            indicator.hide();
        }
    }

    public void setProgress(int n) {
        if (indicator != null) {
            indicator.setProgress(n);
        }
    }

    public void show() {
        if (indicator != null) {
            indicator.show();
        }
    }

    public WebIndicatorController inject(IWebIndicator indicator) {
        this.indicator = indicator;
        return this;
    }
}