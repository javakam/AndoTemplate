# Ando Template

## ando_ui_indicator
比`Google TabLayout`好用的方案, 参考自 <https://github.com/hackware1993/MagicIndicator>

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


## 单例
```kotlin
companion object {
    private var usbUtils: UsbUtils? = null
    fun getInstance(): UsbUtils {
        if (usbUtils == null) {
            synchronized(UsbUtils::class.java) {
                if (usbUtils == null) {
                    usbUtils = UsbUtils()
                }
            }
        }
        return usbUtils!!
    }
}
```


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

🍎 LiveData + Retrofit
https://github.com/pivincii/livedata_retrofit

https://www.ericdecanini.com/2019/11/11/3-ways-to-use-retrofit-with-livedata-in-the-mvvm-android-architecture/

https://github.com/square/retrofit/issues/3075

https://medium.com/@pivincii/using-retrofit-with-livedata-5c5a49544ba3

