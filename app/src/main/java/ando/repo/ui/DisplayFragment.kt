package ando.repo.ui

import ando.toolkit.RandomUtils
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DisplayFragment : Fragment() {
    companion object {
        fun newInstance(position: Int, title: String? = null): DisplayFragment {
            val args = Bundle()
            args.putInt("pos", position)
            args.putString("title", title)
            val fragment = DisplayFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FrameLayout(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            val title = arguments?.getString("title")
            if (!title.isNullOrBlank()) {
                addView(TextView(context).apply {
                    text = "            Title: $title"
                    textSize = 30F
                })
            }

            val pos = arguments?.getInt("pos") ?: 0
            val variable: Int = pos * 20 + RandomUtils.getRandom(5)
            val color: Int = Color.argb(
                150,
                40 * variable,
                50 * RandomUtils.getRandom(5),
                50 * variable
            )
            setBackgroundColor(color)

            val isEditText = (pos == 1)
            if (isEditText) {
                //ScrollView + EditText
                val scroll = NestedScrollView(context)
                scroll.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                val edt = EditText(context)
                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                params.gravity = Gravity.BOTTOM
                edt.layoutParams = params
                edt.hint = "测试输入法显示"
                edt.setBackgroundColor(Color.GRAY)
                scroll.addView(edt)
                addView(TextView(context).apply { text = "EditText 在最底下..." })
                addView(scroll)
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