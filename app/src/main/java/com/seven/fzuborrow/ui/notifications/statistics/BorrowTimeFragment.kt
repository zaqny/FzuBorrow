package com.seven.fzuborrow.ui.notifications.statistics


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide

import com.seven.fzuborrow.R
import kotlinx.android.synthetic.main.fragment_borrow_time.view.*

/**
 * A simple [Fragment] subclass.
 */
class BorrowTimeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_borrow_time, container, false)
        Glide.with(this).load(R.drawable.bg_statistic_4).into(root.imageView2)
        return root
    }


}
