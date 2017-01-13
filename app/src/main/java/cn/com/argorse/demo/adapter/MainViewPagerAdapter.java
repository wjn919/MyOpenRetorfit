package cn.com.argorse.demo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import java.util.List;

import cn.com.argorse.common.utils.AlertUtils;
import cn.com.argorse.common.utils.ImageLoaderUtil;
import cn.com.argorse.demo.BaseApplication;
import cn.com.argorse.demo.Entity.ResultsEntity;
import cn.com.argorse.demo.R;
import cn.com.argorse.demo.activity.ShoppingDetailsActivity;

/**
 * Created by wjn on 2016/12/14.
 */

public class MainViewPagerAdapter extends PagerAdapter {

    private final Activity activity;
    private List<ResultsEntity> urlList;

    public MainViewPagerAdapter(Activity activity, List<ResultsEntity> urlList) {
        this.urlList = urlList;
        this.activity = activity;
        /*mViews = new LinkedList<>();
        if(viewlist!=null){
            for(int i =0;i<viewlist.size();i++){
                View view = View.inflate(activity, R.layout.viewpager_item,null);
                ImageView iv= (ImageView) view.findViewById(R.id.iv_viewpager_item);
                ImageLoaderUtil.load(activity, BaseApplication.Image_Url+viewlist.get(i).getImgUrl(),iv);
                mViews.add(iv);
            }

        }*/
    }

    @Override
    public int getCount() {
        //设置成最大，使用户看不到边界
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0==arg1;
    }
    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
        //Warning：不要在这里调用removeView
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //对ViewPager页号求模取出View列表中要显示的项
        position %=urlList .size();
        if (position<0){
            position = urlList.size()+position;
        }

        View view = View.inflate(activity, R.layout.viewpager_item,null);
        ImageView iv= (ImageView) view.findViewById(R.id.iv_viewpager_item);
        ImageLoaderUtil.load(activity, urlList.get(position).getUrl(),iv);

        final int finalPosition = position;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertUtils.showToast(activity, finalPosition +"");
              Intent intent = new Intent(activity,ShoppingDetailsActivity.class);
                intent.putExtra("imageUrl" ,urlList.get(finalPosition).getUrl());
                activity.startActivity(intent);

            }
        });
        //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp =view.getParent();
        if (vp!=null){
            ViewGroup parent = (ViewGroup)vp;
            parent.removeView(view);
        }
        container.addView(view,0);
        //add listeners here if necessary
        return view;


    }
}