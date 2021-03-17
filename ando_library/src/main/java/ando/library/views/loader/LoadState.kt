package ando.library.views.loader

/**
 * # LoadState
 *
 * Description:加载状态
 *
 * @author javakam
 * @date 2019/11/15 15:02
 */
enum class LoadState(private val state: String, private val stateValue: Int) {
    /**
     *
     */
    UNLOADED("默认的状态", 1),
    LOADING("加载的状态", 2),
    ERROR("失败的状态", 3),
    EMPTY("空的状态", 4),
    SUCCESS("成功的状态", 5);

    fun stateValue(): Int {
        return stateValue
    }
}