package ando.widget.banner;

/**
 * 轮播条目
 *
 * @author javakam
 * @date 2018/11/25 下午7:01
 */
public class BannerItem {

    public String title;
    public Object imgUrl;
    public Object obj1;
    public Object obj2;
    public Object obj3;

    public BannerItem(String title, Object imgUrl) {
        this.title = title;
        this.imgUrl = imgUrl;
    }

    public BannerItem(String title, Object imgUrl, Object obj1) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.obj1 = obj1;
    }

    public BannerItem(String title, Object imgUrl, Object obj1, Object obj2, Object obj3) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.obj1 = obj1;
        this.obj2 = obj2;
        this.obj3 = obj3;
    }
}