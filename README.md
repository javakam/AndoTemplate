# Ando Template

## 全部导入
```groovy
implementation 'com.github.javakam:webview:x.0.0@aar'
implementation 'com.github.javakam:gallery:x.0.0@aar'

implementation 'com.github.javakam:dialog.core:x.0.0@aar'
implementation 'com.github.javakam:dialog.usage:x.0.0@aar'
implementation 'com.github.javakam:dialog.bottomsheet:x.0.0@aar'

implementation 'com.github.javakam:file.core:x.0.0@aar'
implementation 'com.github.javakam:file.selector:x.0.0@aar'
implementation 'com.github.javakam:file.compressor:x.0.0@aar'
implementation 'com.github.javakam:file.android-q:x.0.0@aar'
implementation 'androidx.documentfile:documentfile:1.0.1'

implementation 'com.github.javakam:library:x.0.0@aar'
implementation 'com.github.javakam:toolkit:x.0.0@aar'
implementation 'com.github.javakam:widget.banner:x.0.0@aar'
implementation 'com.github.javakam:widget.indicator.core:x.0.0@aar'
implementation 'com.github.javakam:widget.indicator.usage:x.0.0@aar'
```

## ando_library/ando_toolkit
```
implementation 'ando.library:library:1.0.0'
implementation 'ando.toolkit:toolkit:1.0.0'
```

### `Library`(ando_library)
#### 1.导入
```groovy
implementation 'com.github.javakam:library:x.0.0@aar'
```
#### 2.内容
##### 基类(`base`) BaseActivity/BaseFragment/BaseApplication 等

##### 图片(`glide`) GlideUtils

##### 工具类(`utils`)
```kotlin
网络工具
顺序执行异步任务
Base64Utils
ClearCacheUtils
CrashHandler
GsonUtils
```

##### 组件(`widget`)
```kotlin
透明处理(alpha)
datetime
悬浮窗(float)
圆角ImageView(RadiusImageView)
状态视图(loader)
RecycleView(BaseQuickAdapter/BaseViewHolder/RecyclerDecorationProvider)
阴影视图(ShadowDrawable)
开关视图(SwitchButton)
灰色应用(GrayFrameLayout)
水波纹视图(RippleView)
多属性自定义Button(SuperButton)
```

##### 音视频框架
https://github.com/yangjie10930/EpMedia

##### 顺序执行异步任务
参考项目: https://github.com/ddnosh/android-tiny-task

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

##### SwitchButton
https://github.com/kyleduo/SwitchButton

### `ToolKit`(ando_toolkit)
工具类集合, 还包括常用`Kotlin`扩展函数文件
```kotlin
implementation 'com.github.javakam:toolkit:x.0.0@aar'
```
eg:
```kotlin
fun View.invisible() {
    this.run {
        if (isVisible) visibility = View.INVISIBLE
    }
}

fun View.gone() {
    this.run {
        if (isVisible) visibility = View.GONE
    }
}

fun View.noShake(interval: Long = 500L, block: (v: View) -> Unit) {
    this.apply {
        setOnClickListener(object : NoShakeClickListener(interval) {
            override fun onSingleClick(v: View) {
                block.invoke(v)
            }
        })
    }
}
```

### `Banner`(ando_ui_banner)
#### 1.导入
```groovy
implementation 'com.github.javakam:widget.banner:x.0.0@aar'
```

#### 2.不结合`RecyclerView`使用
```kotlin
val banner = rootView.findViewById<ImageBanner>(R.id.bannerImage)
banner.setSource(mBannerData)
banner.imageLoader = BannerImageLoader()
banner.colorDrawable = ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.transparent))
banner.setOnItemClickListener {
    toastShort("Index : $it")
}
banner.startScroll()

class BannerImageLoader : IBannerImageLoadStrategy {
    void loadImage(@NonNull ImageView imageView, Object path);
    ...
}

val mBannerData: List<BannerItem> = mutableListOf(
    BannerItem(
        "推荐",
        "http://pic.ntimg.cn/20130129/11507979_020415120167_2.jpg"
    ),
    BannerItem(
        "热点",
        "http://pic.ntimg.cn/file/20210320/29633157_104445000089_2.jpg"
    ),
    BannerItem(
        "动态",
        "http://pic.ntimg.cn/20130224/11507979_230737207196_2.jpg"
    ),
)
```
🌴配合`Indicator`一起使用
```kotlin
val indicator = headerView.findViewById<MagicIndicator>(R.id.indicator)
val roundNavigator = RoundRectNavigator(this)
roundNavigator.isFollowTouch = true //是否跟随手指滑动
roundNavigator.totalCount = mBannerData.size
roundNavigator.itemColor = Color.LTGRAY
roundNavigator.indicatorColor = ContextCompat.getColor(this, R.color.color_main_blue)
roundNavigator.setItemWidth(18.0)
roundNavigator.setItemSpacing(4.0)
roundNavigator.setItemHeight(4.0)
roundNavigator.setItemRadius(4.0)
roundNavigator.setOnItemClickListener { index -> banner.viewPager?.currentItem = index }
//roundNavigator.notifyDataSetChanged()
indicator.navigator = roundNavigator
//Banner 和 Indicator 绑定到一起, 同步滑动
banner.viewPager?.apply { ViewPagerHelper.bind(indicator, this) }
```

#### 3.结合`RecyclerView`使用 
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

#### 4.制作引导页面
```kotlin
val guideBanner = GuideBanner(this).apply {
    layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT
    )
    //动画
    //viewPager.setPageTransformer(true, FadeSlideTransformer())
    imageLoader = BannerImageLoader()
    //图片
    setSource(
        mutableListOf<Any>(
            //Remote
            //"http://pic.ntimg.cn/20130129/11507979_020415120167_2.jpg",
            //"http://pic.ntimg.cn/file/20210320/29633157_104445000089_2.jpg",
            //"http://pic.ntimg.cn/20130224/11507979_230737207196_2.jpg",
            //Local
            R.mipmap.pic1,
            R.mipmap.pic2,
            R.mipmap.pic3,
        )
    )
    startScroll()
    setOnJumpClickListener {
        toastShort("jump")
    }
}
```

#### 5.参考项目: <https://github.com/H07000223/FlycoBanner_Master>

### `Indicator`(ando_ui_indicator)
导入
```groovy
implementation 'com.github.javakam:widget.indicator.core:x.0.0@aar'
implementation 'com.github.javakam:widget.indicator.usage:x.0.0@aar'
```
`ando_ui_indicator_usage`为一些做好的控件类型

🌴比`Google TabLayout`好用的方案, 参考自 <https://github.com/hackware1993/MagicIndicator>

### CoordinatorLayout

- 🍎注意: ViewPager中必须有可以滑动的组件如:RecycleView或者ScrollView等, 才能实现效果

- 用 CoordinatorLayout 处理滚动 👉 https://juejin.cn/post/6844903519598690311

    - 对应源码 👉 https://github.com/chrisbanes/cheesesquare

- 动图展示app:layout_scrollFlags的5种滑动属性 👉 https://blog.csdn.net/LosingCarryJie/article/details/78917423

### 沉浸式状态栏
https://juejin.cn/post/6844903518982111245

### RecyclerView

RecyclerViewItemDecoration From https://github.com/zyyoona7/RecyclerViewItemDecoration

### Manage all files on a storage device

https://developer.android.com/training/data-storage/manage-all-files

```xml
android:requestLegacyExternalStorage="true"
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
```xml
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

```kotlin
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

🍎 LiveData + Retrofit
https://github.com/pivincii/livedata_retrofit

https://www.ericdecanini.com/2019/11/11/3-ways-to-use-retrofit-with-livedata-in-the-mvvm-android-architecture/

https://github.com/square/retrofit/issues/3075

https://medium.com/@pivincii/using-retrofit-with-livedata-5c5a49544ba3

