package com.borui.weishare.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.borui.weishare.R;
import com.borui.weishare.net.APIAddress;
import com.borui.weishare.net.Cache;
import com.borui.weishare.vo.Shares;
import com.bumptech.glide.Glide;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by borui on 2017/9/29.
 */

public class ShareCateAdapter extends RecyclerView.Adapter<ShareCateAdapter.ViewHolder> {

    private Context context;
    private int cateCode;
    private int frameWidth;

    public ShareCateAdapter(Context context,int cateCode,int frameWidth){
        this.context=context;
        this.cateCode=cateCode;
        this.frameWidth=frameWidth;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_shares_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Shares.ShareItem shareItem=Cache.shareCache.get(cateCode).get(position);
        ViewGroup.LayoutParams layoutParams=holder.ivShareThumb.getLayoutParams();
        layoutParams.width=frameWidth;
        layoutParams.height=getHeight(shareItem);
        holder.ivShareThumb.setLayoutParams(layoutParams);

        Glide.with(context).load(APIAddress.IMAGEPATH +shareItem.getPics().get(0).getPicPath()).thumbnail(0.1f).fitCenter().into(holder.ivShareThumb);
        Glide.with(context).load(APIAddress.IMAGEPATH +shareItem.getPersonalPicture()).into(holder.ivHead);


        holder.tvShareComment.setText(shareItem.getTitle());
        holder.tvLike.setText(""+shareItem.getLiked());
        holder.tvCollect.setText(""+shareItem.getCollections());
        holder.tvName.setText(shareItem.getRealname());
//        holder.tvSign.setText(shareItem.getSign());
    }

    private int getHeight(Shares.ShareItem shareItem){
        return shareItem.getPics().get(0).getPicHeight()*frameWidth/shareItem.getPics().get(0).getPicWidth();
    }
    @Override
    public int getItemCount() {
        return Cache.shareCache.get(cateCode)==null?0: Cache.shareCache.get(cateCode).size();
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
