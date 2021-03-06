package com.seven.fzuborrow.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.seven.fzuborrow.Constants;
import com.seven.fzuborrow.R;
import com.seven.fzuborrow.ui.home.add.AddActivity;
import com.seven.fzuborrow.ui.home.detail.GoodDetailActivity;
import com.seven.fzuborrow.ui.home.search.GoodSearchActivity;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    GoodsAdapter goodsAdapter;

    SwipeRefreshLayout swipeRefreshLayout;

    String type = "个人闲置";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        goodsAdapter = new GoodsAdapter(good -> {
            Intent intent = new Intent(this.getActivity(),
                    GoodDetailActivity.class);
            intent.putExtra("good", good);
            startActivity(intent);
        });
        goodsAdapter.addOnTabClickListener(type -> {
            this.type = type;
            if (type.equals("活动室")) {
                homeViewModel.getGoodsFromServer(Constants.GOOD_TYPE_ROOM);
            } else if (type.equals("个人闲置")) {
                homeViewModel.getGoodsFromServer(Constants.GOOD_TYPE_GOOD);
            }
        });
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(goodsAdapter);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int[] lastVisibles = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPositions(null);
                    int lastVisible = Math.max(lastVisibles[0], lastVisibles[1]);
                    if (lastVisible + 1 == recyclerView.getAdapter().getItemCount()) {
                        Log.d("TAG", "到底了");
                    }
                }
            }
        });
        root.findViewById(R.id.search_bar).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GoodSearchActivity.class);
            startActivity(intent);
        });
        root.findViewById(R.id.bt_add_good).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddActivity.class);
            startActivity(intent);
        });
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (type.equals("活动室")) {
                homeViewModel.getGoodsFromServer(Constants.GOOD_TYPE_ROOM);
            } else if (type.equals("个人闲置")) {
                homeViewModel.getGoodsFromServer(Constants.GOOD_TYPE_GOOD);
            }
        });
        subscribeUi();
        return root;
    }

    private void subscribeUi() {

        homeViewModel.getGoods().observe(this, goods -> {
            goodsAdapter.submitList(goods);
            if(swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "刷新完成", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static final String TAG = "HomeFragment";

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

}