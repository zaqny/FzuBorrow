package com.seven.fzuborrow.ui.notifications.apply

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.seven.fzuborrow.Constants.*
import com.seven.fzuborrow.R
import com.seven.fzuborrow.data.Apply
import com.seven.fzuborrow.data.User
import com.seven.fzuborrow.network.Api
import com.seven.fzuborrow.network.response.FindApplyResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_apply_history.*

class ApplyHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_history)
        if (intent.getStringExtra("mode") == "my_apply") {
            toolbar_title.text = "我的申请"
        } else {
            toolbar_title.text = "我的借出"
        }
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }
        view_pager.adapter = object :
            FragmentStatePagerAdapter (
                supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
            override fun getItem(position: Int): Fragment {
                return fragmentList[position]
            }

            override fun getCount(): Int {
                return fragmentList.size
            }

            override fun getItemPosition(`object`: Any): Int {
                return POSITION_NONE
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return when (position) {
                    0 -> "未处理"
                    1 -> "使用中"
                    2 -> "归还中"
                    3 -> "已拒绝"
                    4 -> "已完成"
                    else -> ""
                }
            }
        }
        view_pager.offscreenPageLimit = 2
        tab_layout.setupWithViewPager(view_pager)

        swipe_refresh.setOnRefreshListener {
            refreshList()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    val fragmentList = mutableListOf<Fragment>()
    @SuppressLint("CheckResult")
    private fun refreshList() {
        var isOut: Boolean
        var observable: Observable<FindApplyResponse>
        if (intent.getStringExtra("mode") == "my_apply") {
            isOut = false
            observable = Api.get().findApply(User.getLoggedInUser().token)
        } else {
            isOut = true
            observable = Api.get().findBeApply(User.getLoggedInUser().token)
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val currentPage = view_pager.currentItem
                fragmentList.clear()
                view_pager.adapter?.notifyDataSetChanged()
                val spf = getSharedPreferences("notification", 0)!!
                val name = if(isOut)"readed_be_apply" else "readed_apply"
                spf.edit().putStringSet(name, it.applyList.map { it.rid.toString() }.toSet()).commit()

                Log.d(
                    "ApplyHistoryActivity", "refreshList: " +
                            it.applyList.map { it.grade }
                )
                fragmentList.addAll(listOf(
                    ApplyFragment.newInstance(it.applyList.filter { it.status == APPLY_STATUS_PENDING } as ArrayList<Apply>,
                        isOut),
                    ApplyFragment.newInstance(it.applyList.filter { it.status == APPLY_STATUS_USING } as ArrayList<Apply>,
                        isOut),
                    ApplyFragment.newInstance(it.applyList.filter { it.status == APPLY_STATUS_WAITING } as ArrayList<Apply>,
                        isOut),
                    ApplyFragment.newInstance(it.applyList.filter { it.status == APPLY_STATUS_REJECTED } as ArrayList<Apply>,
                        isOut),
                    ApplyFragment.newInstance(it.applyList.filter { it.status == APPLY_STATUS_FINISHED } as ArrayList<Apply>,
                        isOut)
                ))
                view_pager.adapter?.notifyDataSetChanged()
                (view_pager.adapter as FragmentStatePagerAdapter)
                view_pager.currentItem = currentPage
                if (swipe_refresh.isRefreshing) {
                    swipe_refresh.isRefreshing = false
                    Toast.makeText(this, "刷新完成", Toast.LENGTH_SHORT).show()
                }
            }, { Toast.makeText(this, "网络连接异常", Toast.LENGTH_SHORT).show() })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }
}
