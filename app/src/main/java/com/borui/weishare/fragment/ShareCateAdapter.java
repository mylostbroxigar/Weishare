package com.borui.weishare.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.borui.weishare.R;
import com.borui.weishare.vo.Shares;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by borui on 2017/9/29.
 */

public class ShareCateAdapter extends RecyclerView.Adapter<ShareCateAdapter.ViewHolder> {

    private Context context;
    private Shares shares;

    public ShareCateAdapter(Context context){
        this.context=context;
    }
    public void setShares(Shares shares) {
        this.shares = shares;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_shares_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Shares.DataBean shareData=shares.getData().get(position);
        Glide.with(context).load(shareData.getCover()).thumbnail(0.1f).fitCenter().into(holder.ivShareThumb);
        Glide.with(context).load(shareData.getHead()).into(holder.ivHead);
        holder.tvShareComment.setText(shareData.getComment());
        holder.tvLike.setText(""+shareData.getLikenum());
        holder.tvCollect.setText(""+shareData.getCollectnum());
        holder.tvName.setText(shareData.getNickname());
        holder.tvSign.setText(shareData.getSign());
    }

    @Override
    public int getItemCount() {
        return shares==null?0:shares.getData().size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_share_thumb)
        ImageView ivShareThumb;
        @BindView(R.id.tv_share_comment)
        TextView tvShareComment;
        @BindView(R.id.tv_location)
        TextView tvLocation;
        @BindView(R.id.tv_like)
        TextView tvLike;
        @BindView(R.id.tv_collect)
        TextView tvCollect;
        @BindView(R.id.iv_head)
        ImageView ivHead;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_sign)
        TextView tvSign;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}