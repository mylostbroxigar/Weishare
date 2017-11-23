package com.borui.weishare.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.borui.weishare.PayActivity;
import com.borui.weishare.R;
import com.borui.weishare.net.APIAddress;
import com.borui.weishare.net.Cache;
import com.borui.weishare.net.VolleyUtil;
import com.borui.weishare.vo.AuditingVo;
import com.borui.weishare.vo.BaseVo;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by borui on 2017/11/10.
 */

public class MerchantFragment extends Fragment {
    View rootView;
    @BindView(R.id.btn_recharge)
    Button btnRecharge;
    @BindView(R.id.lv_check)
    ListView lvCheck;
    Unbinder unbinder;
    CheckAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_merchant, null);
        unbinder = ButterKnife.bind(this, rootView);
        adapter = new CheckAdapter(getContext());
        lvCheck.setAdapter(adapter);
        loadAuditing();
        return rootView;
    }

    private void loadAuditing(){
        HashMap<String,String> params=new HashMap<>();
        params.put("token", Cache.currenUser.getMsg());
        params.put("auditingStatus","1");
        VolleyUtil.getInstance().doPost(APIAddress.GET_MERCHANT_AUDITING,params,new TypeToken<AuditingVo>(){}.getType(),"loadAuditing");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_recharge)
    public void onViewClicked() {
        startActivity(new Intent(getContext(), PayActivity.class));
    }
}
