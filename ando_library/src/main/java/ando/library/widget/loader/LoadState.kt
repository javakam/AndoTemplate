package ando.library.widget.loader

/**
 * # 加载状态
 *
 * @author javakam
 * @date 2019/11/15 15:02
 */
enum class LoadState(private val stateValue: Int) {
    /**
     *
     */
    UNLOADED(1),
    LOADING(2),
    ERROR(3),
    EMPTY(4),
    SUCCESS(5);

    fun stateValue(): Int {
        return stateValue
    }
}