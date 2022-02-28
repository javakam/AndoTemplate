package ando.webview.indicator;

public interface IWebIndicator {

    void show();

    void hide();

    void reset();

    void setProgress(int newProgress);

}