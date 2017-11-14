package com.borui.weishare.fragment;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.borui.weishare.MyApplication;
import com.borui.weishare.R;
import com.borui.weishare.net.APIAddress;
import com.borui.weishare.net.Cache;
import com.borui.weishare.net.VolleyUtil;
import com.borui.weishare.util.DensityUtil;
import com.borui.weishare.util.SPUtil;
import com.borui.weishare.view.EndlessRecyclerOnScrollListener;
import com.borui.weishare.vo.BaseVo;
import com.borui.weishare.vo.Shares;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by borui on 2017/6/29.
 */

public class ShareCateFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.grid_share)
    RecyclerView gridShare;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    ShareCateAdapter adapter;
    //    boolean isViewCreate=false;
    StaggeredGridLayoutManager layoutManager;
    int cateCode;
    @BindView(R.id.tv_footer)
    TextView tvFooter;
    int page;
    boolean end;
    private Handler handler=new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_cate, null);
        ButterKnife.bind(this, view);
        cateCode = getArguments().getInt("cateCode");
//        isViewCreate=true;
        refreshLayout.setOnRefreshListener(this);
        initGride();
        if (Cache.shareCache.get(cateCode) != null && Cache.shareCache.get(cateCode).size() > 0) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    setGridShare();
                }
            },200);
        } else {
            loadCate(true);
        }
        return view;
    }

    private void initGride(){

//        gridShare.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                layoutManager.invalidateSpanAssignments();
//            }
//        });
        gridShare.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = DensityUtil.dip2px(10);
                outRect.right = DensityUtil.dip2px(10);
                outRect.bottom = DensityUtil.dip2px(15);
            }

        });
        gridShare.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                if(!end){
                    tvFooter.setText("正在加载更多...");
                    loadCate(false);
                }
            }
        });
    }
    private void setGridShare() {

        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        gridShare.setLayoutManager(layoutManager);

        int width=gridShare.getWidth()/2-DensityUtil.dip2px(20);
        adapter = new ShareCateAdapter(getContext(), cateCode,width);
        adapter.setOnOperateClickListener(new ShareCateAdapter.OnOperateClickListener() {
            @Override
            public void onLikeClick(int position) {
                like(position);
            }

            @Override
            public void onCollectClick(int position) {
                collect(position);
            }
        });
        gridShare.setAdapter(adapter);
    }

    private void like(int position){

        Shares.ShareItem item=Cache.shareCache.get(cateCode).get(position);

        Map<String,String> params=new HashMap<>();
        params.put("token", Cache.currenUser.getMsg());
        params.put("shareId",item.getId()+"");
        params.put("userId",Cache.currenUser.getData().getId()+"");
        VolleyUtil.getInstance().doPost(APIAddress.LIKE,params,new TypeToken<BaseVo>(){}.getType(),"like#"+position);
    }
    private void collect(int position){

        Shares.ShareItem item=Cache.shareCache.get(cateCode).get(position);

        Map<String,String> params=new HashMap<>();
        params.put("token", Cache.currenUser.getMsg());
        params.put("shareId",item.getId()+"");
        params.put("userId",Cache.currenUser.getData().getId()+"");
        VolleyUtil.getInstance().doPost(APIAddress.COLLECTION,params,new TypeToken<BaseVo>(){}.getType(),"collect#"+position);
    }
    private void loadCate(boolean refresh) {

        if(refresh){
            end=false;
            page=1;
        }
        Map<String,String> params=new HashMap<>();
        params.put("longitude", SPUtil.getString(getContext(),SPUtil.KEY_LONGITUDE));
        params.put("latitude",SPUtil.getString(getContext(),SPUtil.KEY_LATITUDE));
        params.put("merchantType",cateCode+"");
        params.put("distance","50");
//        params.put("startTime","");
        params.put("pageSize","20");
        params.put("page",page+"");
        VolleyUtil.getInstance().doPost(APIAddress.QUERYSHARES,params,new TypeToken<Shares>(){}.getType(),""+cateCode);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResult(BaseVo obj) {
        if(obj instanceof Shares){
            onResult((Shares)obj);
        }else{
            if(obj.getTag().startsWith("like")){
                if(obj.getCode().equals("0")){
                    String[] strs=obj.getTag().split("#");
                    int position=Integer.parseInt(strs[1]);
                    Cache.shareCache.get(cateCode).get(position).setLiked(Cache.shareCache.get(cateCode).get(position).getLiked()+1);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(),"已赞",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),obj.getMsg(),Toast.LENGTH_SHORT).show();
                }
            }
            if(obj.getTag().startsWith("collect")){
                if(obj.getCode().equals("0")){
                    String[] strs=obj.getTag().split("#");
                    int position=Integer.parseInt(strs[1]);
                    Cache.shareCache.get(cateCode).get(position).setCollections(Cache.shareCache.get(cateCode).get(position).getCollections()+1);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(),"收藏成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),obj.getMsg(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void onResult(Shares shares) {

        if (!shares.getTag().equals("" + cateCode))
            return;
        refreshLayout.setRefreshing(false);
        tvFooter.setText("");

        if (shares.getCode().equals("0")) {

            if(shares.getData()==null||shares.getData().size()==0){
                tvFooter.setText("暂无更多数据");
                end=true;
            }else{
                if(page<=1){
                    Cache.shareCache.get(cateCode).clear();
                    Cache.shareCache.put(cateCode,shares.getData());
                    setGridShare();
                }else{

                    Cache.shareCache.get(cateCode).addAll(shares.getData());
                    adapter.notifyItemRangeChanged(adapter.getItemCount(), shares.getData().size());
                }
                page++;
            }

        }
    }

    @Override
    public void onRefresh() {
        loadCate(true);
    }

}
