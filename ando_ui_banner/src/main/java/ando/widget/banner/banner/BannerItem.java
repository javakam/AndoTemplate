package ando.widget.banner.banner;

/**
 * 图片轮播条目
 *
 * @author javakam
 * @date 2018/11/25 下午7:01
 */
public class BannerItem {

    public String title;
    public String imgUrl;
    public Object obj1;
    public Object obj2;
    public Object obj3;

    public BannerItem(String title, String imgUrl, Object obj1, Object obj2,Object obj3) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.obj1 = obj1;
        this.obj2 = obj2;
        this.obj3 = obj3;
    }

    public String getTitle() {
        return title;
    }

    public BannerItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public BannerItem setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
        return this;
    }

    public Object getObj1() {
        return obj1;
    }

    public BannerItem setObj1(Object obj1) {
        this.obj1 = obj1;
        return this;
    }

    public Object getObj2() {
        return obj2;
    }

    public BannerItem setObj2(Object obj2) {
        this.obj2 = obj2;
        return this;
    }

    public Object getObj3() {
        return obj3;
    }

    public void setObj3(Object obj3) {
        this.obj3 = obj3;
    }
}