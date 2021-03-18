package ando.repo.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DisplayFragment : Fragment() {
    companion object {
        fun newInstance(position: Int): DisplayFragment {
            val args = Bundle()
            args.putInt("pos", position)
            val fragment = DisplayFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            val variable: Int = arguments?.getInt("pos") ?: 0 * 20 + 2
            val color: Int = Color.argb(150, 90 * variable, 50, 80 * variable)
            setBackgroundColor(color)

            val isEditText = (variable == 1)
            if (isEditText) {
                val edt = EditText(context)
                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                //params.topMargin = 50
                params.gravity = Gravity.BOTTOM
                edt.layoutParams = params
                edt.hint = "测试输入法显示"
                edt.setBackgroundColor(Color.GRAY)
                addView(edt)
            } else {
                //RecyclerView
                val recycler = RecyclerView(context)
                recycler.itemAnimator = null
                recycler.layoutManager = LinearLayoutManager(context)
                val adapter = DisplayAdapter()
                adapter.mData = mutableListOf(
                    "aaa",
                    "bbb",
                    "ccc",
                    "ddd",
                    "eee",
                    "fff",
                )
                recycler.adapter = adapter
                addView(recycler)
            }
        }
    }

    private class DisplayAdapter : RecyclerView.Adapter<DisplayAdapter.VH>() {
        var mData: List<String>? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(
                LayoutInflater.from(parent.context)
                    .inflate(android.R.layout.simple_list_item_1, parent, false)
            )
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.text1.text = mData?.get(position)
        }

        override fun getItemCount(): Int = mData?.size ?: 0

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val text1: TextView by lazy { view.findViewById(android.R.id.text1) }
        }
    }

}