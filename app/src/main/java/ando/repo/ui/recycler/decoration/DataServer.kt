package ando.repo.ui.recycler.decoration

/**
 * @author   zyyoona7
 * @version  v1.0
 * @since    2018/12/13.
 */
class DataServer {

    companion object {

        @JvmStatic
        fun createLinearData(size: Int = 30) = createData("这是 LinearLayoutManager ItemDecoration 展示。", size)

        @JvmStatic
        fun createGridData(size: Int = 30) = createData("这是 GridLayoutManager ItemDecoration 展示。", size)

        private fun createData(text: String, size: Int): List<String> {
            val list = arrayListOf<String>()
            for (i in 0 until size) {
                list.add(text)
            }
            return list
        }
    }
}