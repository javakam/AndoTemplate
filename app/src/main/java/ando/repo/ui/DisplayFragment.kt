package ando.repo.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment

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
        }
    }
}