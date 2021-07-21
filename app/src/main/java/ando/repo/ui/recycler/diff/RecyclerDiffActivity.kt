package ando.repo.ui.recycler.diff

import ando.repo.R
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * @author javakam
 * @date 2021-07-21  10:25
 */
class RecyclerDiffActivity : AppCompatActivity() {

    private val mRecyclerView: RecyclerView by lazy { findViewById(R.id.diff_rv) }
    private val mBtReset: Button by lazy { findViewById(R.id.item_change_reset) }
    private val mBtChangeSomeItem: Button by lazy { findViewById(R.id.item_change_btn) }
    private val mBtChangeItemZero: Button by lazy { findViewById(R.id.notify_change_btn) }

    private var mAdapter: DiffUtilAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_diff)
        title = "RecyclerView Diff"
        initRv()
        initClick()
    }

    private fun initRv() {
        mAdapter = DiffUtilAdapter(getOldList())
        mRecyclerView.adapter = mAdapter
        val view: View = layoutInflater.inflate(R.layout.item_widget_ver_header, mRecyclerView, false)
        mAdapter?.addHeaderView(view)

        // 必须设置 Diff Callback
        mAdapter?.setDiffCallback(DiffDemoCallback())
    }

    private fun initClick() {
        mBtReset.setOnClickListener {
            mAdapter?.setDiffNewData(getOldList().toMutableList())
        }
        mBtChangeSomeItem.setOnClickListener {
            val newData = getNewList()
            mAdapter?.setDiffNewData(newData.toMutableList())
        }
        mBtChangeItemZero.setOnClickListener { // change item 0
            mAdapter?.data?.set(
                0, DiffUtilDemoEntity(
                    1,
                    "😊😊Item " + 0,
                    "改变位置0的暑假 (notifyItemChanged)",
                    "06-12"
                )
            )
            mAdapter?.notifyItemChanged(0 + (mAdapter?.headerLayoutCount ?: 0), DiffUtilAdapter.ITEM_0_PAYLOAD)
        }
    }


    /**
     * 重新为最开始的数据
     */
    private fun getOldList(): List<DiffUtilDemoEntity> {
        val list: MutableList<DiffUtilDemoEntity> = ArrayList()
        for (i in 0..9) {
            list.add(
                DiffUtilDemoEntity(
                    i,
                    "Origin Title $i",
                    "Origin Content $i",
                    "06-12"
                )
            )
        }
        return list
    }

    /**
     * 重新获取数据
     *
     * 新数据改变: 1删除1号和3号数据;位置0和4的数据, 从而达到局部更新的效果
     */
    private fun getNewList(): List<DiffUtilDemoEntity> {
        val list: MutableList<DiffUtilDemoEntity> = ArrayList()
        for (i in 0..9) {
            /*
            Simulate deletion of data No. 1 and No. 3
            模拟删除1号和3号数据
             */
            if (i == 1 || i == 3) {
                continue
            }

            /*
            Simulate modification title of data No. 0
            模拟修改0号数据的title
             */if (i == 0) {
                list.add(
                    DiffUtilDemoEntity(
                        i,
                        "😊Item $i",
                        "This item $i content",
                        "06-12"
                    )
                )
                continue
            }

            /*
            Simulate modification content of data No. 4
            模拟修改4号数据的content发生变化
             */if (i == 4) {
                list.add(
                    DiffUtilDemoEntity(
                        i,
                        "Item $i",
                        "Oh~~~~~~, Item $i content have change",
                        "06-12"
                    )
                )
                continue
            }
            list.add(
                DiffUtilDemoEntity(
                    i,
                    "Origin Title $i",
                    "Origin Content $i",
                    "06-12"
                )
            )
        }
        return list
    }
}