package com.seven.fzuborrow.ui.home;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.seven.fzuborrow.R;
import com.seven.fzuborrow.data.Good;
import com.seven.fzuborrow.data.User;
import com.seven.fzuborrow.network.Api;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GoodsAdapter extends ListAdapter<Good, RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private GoodClickListener listener;
    private TabClickListener tabListener;

    @Override
    public void submitList(@Nullable List<Good> list) {
        submitList(list, null);
    }

    @Override
    public void submitList(@Nullable List<Good> list, @Nullable Runnable commitCallback) {
        Good header = new Good();
        header.setGid(Integer.MIN_VALUE);

        Good footer = new Good();
        footer.setGid(Integer.MAX_VALUE);

        List<Good> goods = new ArrayList<>();
        goods.add(header);
        goods.addAll(list);
        goods.add(footer);
        super.submitList(goods, commitCallback);
    }

    public GoodsAdapter(GoodClickListener listener) {
        super(new DiffUtil.ItemCallback<Good>() {
            @Override
            public boolean areItemsTheSame(@NonNull Good oldItem, @NonNull Good newItem) {
                return oldItem.getGid() == newItem.getGid();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Good oldItem, @NonNull Good newItem) {
                return oldItem.getGid() == newItem.getGid();
            }
        });
        this.listener = listener;
    }

    private static final String TAG = "GoodsAdapter";

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_item, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_item, parent, false);
            final HeaderViewHolder holder = new HeaderViewHolder(view);
            return holder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_item, parent, false);
            final FooterViewHolder holder = new FooterViewHolder(view);
            return holder;
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
        if (holder instanceof ItemViewHolder) {
            layoutParams.setFullSpan(false);
            Good good = getItem(position);
            holder.itemView.setOnClickListener(v -> listener.onClick(good));
            Log.d(TAG, "onBindViewHolder: " + good.getName());
            ((ItemViewHolder) holder).name.setText(good.getName());
            ((ItemViewHolder) holder).profile.setText(good.getDetail());
            if(good.getImgurl()!=null) {
                Glide.with(holder.itemView).load(good.getImgurl()).into(((ItemViewHolder) holder).image);
            } else {
                Glide.with(holder.itemView).load(R.drawable.banner_placeholder).into(((ItemViewHolder) holder).image);
            }
            holder.itemView.setVisibility(View.VISIBLE);
            Api.get().findUserByUid(User.getLoggedInUser().getToken(), good.getUid()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(findUserResponse -> {
                        User user = findUserResponse.getUser();
                        ((ItemViewHolder) holder).username.setText(user.getUsername());
                        if(user.getImgurl()!=null) {
                            Glide.with(holder.itemView).load(user.getImgurl()).into(((ItemViewHolder) holder).avatar);
                        } else {
                            Glide.with(holder.itemView).load(R.drawable.banner_placeholder).into(((ItemViewHolder) holder).avatar);
                        }
                        ((ItemViewHolder) holder).username.setText(findUserResponse.getUser().getUsername());
                        holder.itemView.invalidate();
                    }, Throwable::printStackTrace);
        } else if (holder instanceof HeaderViewHolder) {
            layoutParams.setFullSpan(true);
            ((HeaderViewHolder) holder).tabLayout.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tabListener != null) {
                        String type = tab.getText().toString();
                        tabListener.onTabClick(type);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            if(!bannerVisible) {
                ((HeaderViewHolder) holder).bannerImage.setVisibility(View.GONE);
            } else {
                ((HeaderViewHolder) holder).bannerImage.setVisibility(View.VISIBLE);
            }
        } else {
            layoutParams.setFullSpan(true);
            holder.itemView.setVisibility(View.GONE);
        }
        holder.itemView.setLayoutParams(layoutParams);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView profile;
        ImageView image;

        ImageView avatar;
        TextView username;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.good_name);
            profile = itemView.findViewById(R.id.good_profile);
            image = itemView.findViewById(R.id.good_image);
            avatar = itemView.findViewById(R.id.iv_avatar);
            username = itemView.findViewById(R.id.tv_user_name);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        TabLayout tabLayout;
        ImageView bannerImage;
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tabLayout = itemView.findViewById(R.id.tab_layout);
            bannerImage = itemView.findViewById(R.id.iv_banner);
            tabLayout.addTab(tabLayout.newTab().setText("个人闲置"));
            tabLayout.addTab(tabLayout.newTab().setText("活动室"));
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        FooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public interface GoodClickListener {
        void onClick(Good good);
    }

    public interface TabClickListener {
        void onTabClick(String type);
    }

    public void addOnTabClickListener(TabClickListener listener) {
        this.tabListener = listener;
    }

    public void setBannerVisible(boolean bannerVisible) {
        this.bannerVisible = bannerVisible;
    }

    private boolean bannerVisible = true;
}
