package ando.library.widget.recycler.decoration

/**
 * From https://github.com/zyyoona7/RecyclerViewItemDecoration
 */
object RecyclerDecorationProvider {
    fun linear() = LinearItemDecoration.Builder()

    fun grid() = GridItemDecoration.Builder()

    fun staggeredGrid() = StaggeredGridItemDecoration.Builder()
}