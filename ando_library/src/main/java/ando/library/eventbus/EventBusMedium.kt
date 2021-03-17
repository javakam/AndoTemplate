package ando.library.eventbus

/**
 * # EventBusMedium 传输介质
 *
 * @author javakam
 * @date 2019-05-25 14:18:11
 */
class EventBusMedium {

    var id: Int? = 0
    var what: Int? = 0
    var obj: Any? = null
    var obj1: Any? = null
    var obj2: Any? = null

    constructor(id: Int?, what: Int?, obj: Any?, obj1: Any?, obj2: Any?) {
        this.id = id
        this.what = what
        this.obj = obj
        this.obj1 = obj1
        this.obj2 = obj2
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventBusMedium

        if (id != other.id) return false
        if (what != other.what) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (what ?: 0)
        return result
    }

}