package com.seven.fzuborrow.ui.community;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.seven.fzuborrow.R;
import com.seven.fzuborrow.data.Disc;
import com.seven.fzuborrow.data.User;
import com.seven.fzuborrow.network.Api;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DiscAdapter extends ListAdapter<Disc, DiscAdapter.ViewHolder> {

    private SimpleDateFormat f = new SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault());

    private DiscClickListener listener;

    private LongSparseArray<User> userMap = new LongSparseArray<>();

    DiscAdapter(DiscClickListener listener) {
        super(new DiffUtil.ItemCallback<Disc>() {
            @Override
            public boolean areItemsTheSame(@NonNull Disc oldItem, @NonNull Disc newItem) {
                return oldItem.getDid() == newItem.getDid();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Disc oldItem, @NonNull Disc newItem) {
                return oldItem.getDid() == newItem.getDid();
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.disc_item, parent, false));
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Disc disc = getItem(position);
        if (!userMap.containsKey(disc.getUid())) {
            Api.get().findUserByUid(User.getLoggedInUser().getToken(),disc.getUid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(findUserResponse -> {
                        userMap.put(disc.getUid(),findUserResponse.getUser());
                        holder.tvUsername.setText(userMap.get(disc.getUid()).getUsername());
                        Glide.with(holder.itemView).load(userMap.get(disc.getUid()).getImgurl()).into(holder.ivUserAvatar);
                    },e-> Toast.makeText(holder.itemView.getContext(), e.toString(), Toast.LENGTH_SHORT).show());
        } else {
            holder.tvUsername.setText(userMap.get(disc.getUid()).getUsername());
            Glide.with(holder.itemView).load(userMap.get(disc.getUid()).getImgurl()).into(holder.ivUserAvatar);
        }
        holder.tvPublishTime.setText(f.format(disc.getCtime() * 1000L));
        holder.tvDetail.setText(disc.getTitle());
        Glide.with(holder.itemView).load(disc.getImgurl()).into(holder.ivImage);
        holder.tvComments.setText(disc.getCounts() + "");
        holder.tvLikes.setText(disc.getLikes() + "");
        holder.tvLikes.setOnClickListener(v -> Api.get().addDiscLikes(User.getLoggedInUser().getToken(), disc.getDid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addLikesResponse -> {
                    disc.setLikes(disc.getLikes() + 1);
                    holder.tvLikes.setText(disc.getLikes() + "");
                },e-> Toast.makeText(holder.itemView.getContext(), "网络连接异常", Toast.LENGTH_SHORT).show()));
        holder.itemView.setOnClickListener(v -> listener.onClick(disc));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserAvatar;
        TextView tvUsername;
        TextView tvPublishTime;
        TextView tvDetail;
        ImageView ivImage;
        TextView tvComments;
        TextView tvLikes;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.iv_user_image);
            tvUsername = itemView.findViewById(R.id.tv_user_name);
            tvPublishTime = itemView.findViewById(R.id.tv_time);
            tvDetail = itemView.findViewById(R.id.tv_content);
            ivImage = itemView.findViewById(R.id.iv_image);
            tvComments = itemView.findViewById(R.id.tv_comment_count);
            tvLikes = itemView.findViewById(R.id.tv_like_count);
        }
    }

    interface DiscClickListener {
        void onClick(Disc disc);
    }

}
