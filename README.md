# Ando Template

## 导入
```groovy
implementation 'com.github.javakam:webview:3.0.0@aar'
implementation 'com.github.javakam:gallery:1.5.0@aar'

implementation 'com.github.javakam:dialog.core:1.3.5@aar'
implementation 'com.github.javakam:dialog.usage:1.3.5@aar'

implementation 'com.github.javakam:file.core:1.4.2@aar'
implementation 'com.github.javakam:file.selector:1.4.2@aar'
implementation 'com.github.javakam:file.compressor:1.4.2@aar'
implementation 'com.github.javakam:file.android-q:1.4.2@aar'
implementation 'androidx.documentfile:documentfile:1.0.1'

implementation 'com.github.javakam:library:1.2.0@aar'
implementation 'com.github.javakam:toolkit:1.0.0@aar'
implementation 'com.github.javakam:widget.banner:1.0.0@aar'
implementation 'com.github.javakam:widget.indicator.core:1.0.0@aar'
implementation 'com.github.javakam:widget.indicator.usage:1.0.0@aar'
```

## ando_ui_indicator
比`Google TabLayout`好用的方案, 参考自 <https://github.com/hackware1993/MagicIndicator>

## ando_ui_banner
参考项目: <https://github.com/H07000223/FlycoBanner_Master>
### 1.`Banner`在`RecyclerViewAdapter`中使用时:
```java
@Override
public void onViewDetachedFromWindow(BaseViewHolder holder) {
    final SimpleImageBanner banner = holder.getView(R.id.bannerImage);
    if (banner!=null) {
        banner.pauseScroll();
    }
    super.onViewDetachedFromWindow(holder);
}

@Override
public void onViewAttachedToWindow(@NotNull BaseViewHolder holder) {
    super.onViewAttachedToWindow(holder);
    final SimpleImageBanner banner = holder.getView(R.id.bannerImage);
    if (banner!=null) {
        banner.startScroll();
    }
}
```
### 2.`Banner`做引导页面
```java
public class UserGuideActivity extends BaseActivity {

    private CustomGuideBanner mGuideBanner;
    private AlertDialog mDialog;

    @Override
    public void initView(Bundle savedInstanceState) {
        showTipDialog();

        mGuideBanner = findViewById(R.id.banner_guide);

        /*
        切换效果:
         DepthTransformer
         FadeSlideTransformer
         FlowTransformer
         RotateDownTransformer
         RotateUpTransformer
         ZoomOutSlideTransformer
         */
        mGuideBanner
                .setIndicatorWidth(8)
                .setIndicatorHeight(8)
                .setIndicatorGap(12)
                .setIndicatorCornerRadius(3.5f)
                .setSelectAnimClass(ZoomInEnter.class)
                //.setTransformerClass(FadeSlideTransformer.class)
                .barPadding(10, 10, 10, 10)
                .setSource(Constants.getUserGuides())
                .startScroll();

        mGuideBanner.setOnJumpClickListener(new CustomGuideBanner.OnJumpClickListener() {
            @Override
            public void onJumpClick() {
                if (AccountManager.isLogin() || Constants.IS_SKIP_LOGIN) {
                    Intent intent = new Intent(UserGuideActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, android.R.anim.fade_out);
                    finish();
                } else {
                    PageIntent.jumpToLoginClearTop2Main(UserGuideActivity.this);
                    overridePendingTransition(0, android.R.anim.fade_out);
                    finish();
                }
            }
        });
    }

    private void showTipDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            return;
        }

        final View dialogTip = LayoutInflater.from(this).inflate(R.layout.dialog_tip_protocol, null, false);
        TextView tvContent = dialogTip.findViewById(R.id.tv_tip_content);
        TextView tvCancel = dialogTip.findViewById(R.id.tv_tip_cancel);
        TextView tvConfirm = dialogTip.findViewById(R.id.tv_tip_confirm);

        String protocol = getString(R.string.xxx_content);
        String innerProtocol = getString(R.string.account_dialog_tip_protocol);
        String innerPrivate = getString(R.string.account_dialog_tip_private);

        protocol = String.format(protocol, innerProtocol, innerPrivate);

        SpannableString smp = new SpannableString(protocol);
//        SpannableString smpProtocol = new SpannableString(innerProtocol);
//        SpannableString smpPrivate = new SpannableString(innerPrivate);

        //《用户协议》
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NotNull View view) {
                PageIntent.jumpToProtocol(UserGuideActivity.this);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //这里如果设置为false则不带下划线，true带有下划线
                ds.setUnderlineText(false);
            }
        };
        //《隐私政策》
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NotNull View view) {
                PageIntent.jumpToPrivacy(UserGuideActivity.this);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //这里如果设置为false则不带下划线，true带有下划线
                ds.setUnderlineText(false);
            }
        };

        //《用户协议》
        //设置点击的范围
        smp.setSpan(clickableSpan, protocol.indexOf("《") + 1, protocol.indexOf("》"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置前景色
        smp.setSpan(new ForegroundColorSpan(Color.parseColor("#5383F1")),
                protocol.indexOf("《"), protocol.indexOf("》") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        //《隐私政策》
        //设置点击的范围
        smp.setSpan(clickableSpan2, protocol.lastIndexOf("《") + 1, protocol.lastIndexOf("》"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置前景色
        smp.setSpan(new ForegroundColorSpan(Color.parseColor("#5383F1")),
                protocol.lastIndexOf("《"), protocol.lastIndexOf("》") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        tvContent.setText(smp);
        //设置添加链接
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());

        //取消
        ClickUtils.noShake(tvCancel, new ClickUtils.OnClickListener() {
            @Override
            public void onClick() {
                dismissDialog();
                Application.Companion.exit2();
            }
        });
        ClickUtils.noShake(tvConfirm, new ClickUtils.OnClickListener() {
            @Override
            public void onClick() {
                dismissDialog();
                dealWithFirstLoad();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogTip);
        builder.setCancelable(false);
        mDialog = builder.show();

        //设置对话框铺满屏幕
//        Window window = mDialog.getWindow();
//        WindowManager wm = getWindowManager();
//        Display display = wm.getDefaultDisplay();
//        Point point = new Point();
//        display.getSize(point);
//        if (window != null) {
//            final int horPadding = getResources().getDimensionPixelSize(R.dimen.dp_45);
//            window.getDecorView().setPadding(horPadding, 0, horPadding, 0);
//            WindowManager.LayoutParams lp = window.getAttributes();
//            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//            lp.height = (int) (point.y * 0.7);
//            window.setAttributes(lp);
//        }

    }

    private void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private void dealWithFirstLoad() {
        final FirstLoadUtils firstLoadUtils = new FirstLoadUtils(UserGuideActivity.this);
        if (firstLoadUtils.isFirstLoad()) {
            firstLoadUtils.setFirstLoad(false);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_guide;
    }

}
```


## Banner & Indicator 一起使用
```java
//bing BannerView with MagicIndicator
final MagicIndicator indicator = holder.getView(R.id.magicIndicator);
RoundRectNavigator roundNavigator = new RoundRectNavigator(mContext);
roundNavigator.setFollowTouch(true);//是否跟随手指滑动
roundNavigator.setTotalCount(mEntity.size());

roundNavigator.setItemColor(Color.LTGRAY);
roundNavigator.setIndicatorColor(Color.parseColor("#BA0022"));

roundNavigator.setItemWidth(18D);
roundNavigator.setItemSpacing(4D);
roundNavigator.setItemHeight(3D);
roundNavigator.setItemRadius(3D);

roundNavigator.setOnItemClickListener(new RoundRectNavigator.OnItemClickListener() {
    @Override
    public void onClick(int index) {
        banner.getViewPager().setCurrentItem(index);
    }
});
roundNavigator.notifyDataSetChanged();
indicator.setNavigator(roundNavigator);
//ViewPagerHelper.bind(indicator, banner.getViewPager());
banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        indicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        indicator.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        indicator.onPageScrollStateChanged(state);
    }
});
```

## CoordinatorLayout

- 🍎注意: ViewPager中必须有可以滑动的组件如:RecycleView或者ScrollView等, 才能实现效果

- 用 CoordinatorLayout 处理滚动 👉 https://juejin.cn/post/6844903519598690311

    - 对应源码 👉 https://github.com/chrisbanes/cheesesquare

- 动图展示app:layout_scrollFlags的5种滑动属性 👉 https://blog.csdn.net/LosingCarryJie/article/details/78917423


### 沉浸式状态栏
https://juejin.cn/post/6844903518982111245

## ando_library/ando_toolkit
```
repositories {
        maven { url "https://dl.bintray.com/javakam/AndoLibrary" }
}

implementation 'ando.library:library:1.0.0'
implementation 'ando.toolkit:toolkit:1.0.0'
```

### RecyclerView

RecyclerViewItemDecoration From https://github.com/zyyoona7/RecyclerViewItemDecoration

### Manage all files on a storage device

https://developer.android.com/training/data-storage/manage-all-files

```xml
android:requestLegacyExternalStorage="true"
```

### 音视频框架
https://github.com/yangjie10930/EpMedia

### 顺序执行异步任务
https://github.com/ddnosh/android-tiny-task

```kotlin
private var count: Int = 0
private fun test() {
    count = 0
    val task1 = object : BaseSyncTask() {
        override fun doTask() {
            count++
            Log.i("2333", "task1... $count")
            TinySyncExecutor.getInstance().finish()
        }
    }
    val task2 = object : BaseSyncTask() {
        override fun doTask() {
            count++
            Log.e("2333", "task2... $count")
            TinySyncExecutor.getInstance().finish()
        }
    }
    val task3 = object : BaseSyncTask() {
        override fun doTask() {
            count++
            Log.w("2333", "task3... $count")
            TinySyncExecutor.getInstance().finish()
        }
    }
    TinySyncExecutor.getInstance().enqueue(task1)
    TinySyncExecutor.getInstance().enqueue(task3)
    TinySyncExecutor.getInstance().enqueue(task2)
}
```


## ando dialog manger
```
todo 2021年3月30日 17:08:34
https://github.com/li-xiaojun/XPopup/blob/master/library/src/main/java/com/lxj/xpopup/widget/SmartDragLayout.java
```

> novoda 目前不支持 Gradle 6+ , 替换方案 `https://github.com/panpf/bintray-publish`

novoda

```
https://github.com/novoda/bintray-release/wiki/%E4%B8%AD%E6%96%87%E6%96%87%E6%A1%A3HOME

apply plugin: 'com.novoda.bintray-release'

gradlew clean build bintrayUpload -PbintrayUser=javakam -PbintrayKey=xxx -PdryRun=false

```
panpf

```
https://github.com/panpf/bintray-publish
gradlew clean build bintrayUpload -PbintrayUser=BINTRAY_USERNAME -PbintrayKey=BINTRAY_KEY -PdryRun=false
apply plugin: 'com.github.panpf.bintray-publish'
```

## 文件File & WebView
```gradle
api 'androidx.documentfile:documentfile:1.0.1'
//api 'com.ando.file:FileOperator:0.9.3-beta1'
api 'ando.file:core:1.2.0'         //核心库必选
api 'ando.file:android-q:1.2.0'    //AndroidQ & Android 11 兼容库
api 'ando.file:compressor:1.2.0'   //图片压缩,核心算法采用 Luban
api 'ando.file:selector:1.2.0'     //文件选择器

api 'ando.webview:webview:1.0.0'
api 'com.ando.string:StringExpandUtils:1.0.0'
```

## 网络框架(Retrofit & LiveData)
LiveData Adapter for Retrofit

https://gist.github.com/AkshayChordiya/15cfe7ca1842d6b959e77c04a073a98f

https://github.com/googlesamples/android-architecture-components/tree/master/GithubBrowserSample


## MVVM + Hilt
https://itnext.io/android-architecture-hilt-mvvm-kotlin-coroutines-live-data-room-and-retrofit-ft-8b746cab4a06

https://github.com/sdwfqin/AndroidQuick/tree/4.x/app-kt

## androidx Fragment 懒加载
https://juejin.im/post/6844904050698223624

## Glide-KTX
https://github.com/champChayangkoon/Glide-KTX

## GreenDao Gradle
```
gradlew.bat greendao --warning-mode all --stacktrace
```


## BottomNavigationView show/hide 而不是 replace
https://stackoverflow.com/questions/54087740/how-to-hide-bottomnavigationview-on-android-navigation-lib

> You could do something like this in your activity's onCreate.
When ever an item in the nav bar is selected it will show or hide the nav based on the fragment id's.

```kotlin
private fun setupNav() {
    val navController = findNavController(R.id.nav_host_fragment)
    findViewById<BottomNavigationView>(R.id.bottomNav)
        .setupWithNavController(navController)

    navController.addOnDestinationChangedListener { _, destination, _ ->
        when (destination.id) {
            R.id.mainFragment -> showBottomNav()
            R.id.mineFragment -> showBottomNav()
            else -> hideBottomNav()
        }
    }
}

private fun showBottomNav() {
    bottomNav.visibility = View.VISIBLE
}

private fun hideBottomNav() {
    bottomNav.visibility = View.GONE
}
```

## 混淆


## DataBinding 在XML中的具体使用方式
```

<androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    android:id="@+id/swipeRefresh"
    bind:colorSchemeResources="@{resId}"
    bind:onRefreshListener="@{() -> viewModel.onRefresh()}"
    bind:refreshing="@{viewModel.refreshing}"
    android:layout_width="match_parent"
    android:layout_height="match_parent"></androidx.swiperefreshlayout.widget.SwipeRefreshLayout>

<TextView
    android:id="@+id/tv_article_tabs"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text='@{"复杂的表达式显示结果  "+@string/app_name+" -> "  + viewModel.wxArticleTabs.size()}' />

<ImageView
    android:id="@+id/iv_article_tabs_bind"
    loadPic="@{viewModel.tempImageUrl}"
    android:layout_width="35dp"
    android:layout_height="35dp"
    android:layout_marginTop="3dp"
    android:scaleType="centerCrop" />

```

## 添加矢量图SVG
https://developer.android.com/studio/write/vector-asset-studio?hl=zh-cn

## 参考项目

https://github.com/k3marek/GithubBrowser

https://github.com/omjoonkim/GitHubBrowserApp

https://github.com/zyyoona7/KExtensions/blob/master/lib/src/main/java/com/zyyoona7/extensions/

https://github.com/shiweibsw/Android-kotlin-extend-utils/blob/master/app/src/main/java/com/kd/kotlin/extend/utils/

## Tips

1. NavController,BottomNavigationView 不同步问题

https://medium.com/@freedom.chuks7/how-to-use-jet-pack-components-bottomnavigationview-with-navigation-ui-19fb120e3fb9

2. xml中 fragment -> FragmentContainerView 异常

https://stackoverflow.com/questions/58320487/using-fragmentcontainerview-with-navigation-component

```
error:
navController = Navigation.findNavController(this, R.id.nav_host)

success:
val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
navController = navHostFragment.navController
```

3. Retrofit 姿势错误
```
 URL query string "{page}" must not have replace block. For dynamic query parameters use @Query.
     for method ApiService.getRecommendProjects
```

4. Glide AppGlideModule
```
Failed to find GeneratedAppGlideModule. You should include an annotationProcessor compile dependency on com.github.bumptech.glide:compiler in your application and a @GlideModule annotated AppGlideModule implementation or LibraryGlideModules will be silently ignored
```

5. LiveData() with no args
```
Failed to invoke public androidx.lifecycle.LiveData() with no args
```

6.BaseAdapter 用法
```kotlin
class CustomAdapter :
    BaseRecyclerAdapter<String>(R.layout.xxx, null) {
    override fun bindData(holder: BaseViewHolder, position: Int, item: String) {
    }
}

class CustomAdapter2 : BaseAdapter<String, CustomHolder>(null) {
    override fun getViewHolder(view: View): CustomHolder {
        return CustomHolder(view)
    }

    override fun bindData(holder: CustomHolder, position: Int, item: String) {
    }
}

class CustomHolder(v: View) : BaseViewHolder(v) {
}
```

#### SwitchButton todo 用法案例
https://github.com/kyleduo/SwitchButton

🍎 LiveData + Retrofit
https://github.com/pivincii/livedata_retrofit

https://www.ericdecanini.com/2019/11/11/3-ways-to-use-retrofit-with-livedata-in-the-mvvm-android-architecture/

https://github.com/square/retrofit/issues/3075

https://medium.com/@pivincii/using-retrofit-with-livedata-5c5a49544ba3

