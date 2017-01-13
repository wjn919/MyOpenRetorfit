package cn.com.argorse.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.argorse.demo.Entity.MainGridViewData;
import cn.com.argorse.demo.Entity.ShareGridViewData;
import cn.com.argorse.demo.R;


/**
 * Created by wjn on 2015/10/27.
 * 主页面的Gridview的adapter
 */
public class MyShareGridAdapter<Object> extends ArrayAdapter<Object> {
    private LayoutInflater mInflater;
    private ShareGridViewData mData;

    public MyShareGridAdapter(Context context, int resource, Object[] objects, ShareGridViewData mData) {
        super(context, resource, objects);
        mInflater = LayoutInflater.from(context);
        this.mData = mData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //nflate添加我们自己的布局
        LinearLayout layout = new LinearLayout(getContext());
        mInflater.inflate(R.layout.adapter_main_grid_item_view, layout, true);

        //布局里有一个image和一个text
        ImageView iv_main_icon;
        TextView tv_main_iconmessage;
        iv_main_icon = (ImageView) layout.findViewById(R.id.iv_main_icon);
        tv_main_iconmessage = (TextView) layout.findViewById(R.id.tv_main_iconmessage);

        //根据position，取对应的数据。
        tv_main_iconmessage.setText(mData.getDisplaymainmessage(position));
        iv_main_icon.setImageResource(mData.getMainActivityIcon(position));

        //将额外的参数设置到View的TAG属性里，以提供在点击事件回调函数里面处理。
        layout.setTag(position+"");

        return layout;
    }
}
