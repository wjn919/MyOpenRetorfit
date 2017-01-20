package cn.com.argorse.demo.adapter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

import cn.com.argorse.common.utils.AlertUtils;
import cn.com.argorse.common.utils.ImageLoaderUtil;
import cn.com.argorse.demo.Entity.ResultsEntity;
import cn.com.argorse.demo.R;
import cn.com.argorse.demo.activity.ShoppingDetailsActivity;


/**
 * Created by wjn on 2017/1/19.
 */

public class MyTypeListAdapter extends BaseAdapter {
    private final ArrayList<ResultsEntity> mDataList;
    Context mContext;
    LayoutInflater inflater;
    final int TYPE_1 = 0;
    final int TYPE_2 = 1;
    final int TYPE_3 = 2;

    public int getSubnum() {
        return subnum;
    }

    public void setSubnum(int subnum) {
        this.subnum = subnum;
    }

    int subnum = 0;

    public MyTypeListAdapter(Context context, ArrayList<ResultsEntity> dataList) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.mDataList = dataList;
    }

    @Override
    public int getCount() {
        return (mDataList==null||mDataList.size()==0)?0:mDataList.size()+2;
    }

    //每个convert view都会调用此方法，获得当前所需要的view样式
    @Override
    public int getItemViewType(int position) {
        if (position == 0||position==3) {
            return TYPE_1;
        } else if (position < 3){
            return TYPE_2;
        } else{
            return TYPE_3;
        }


    }

    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 3;
    }

    @Override
    public Object getItem(int arg0) {
            return mDataList.get(arg0);

    }

    @Override
    public long getItemId(int position) {
        return  position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        viewHolder1 holder1 = null;
        viewHolder2 holder2 = null;
        viewHolder3 holder3 = null;
        int type = getItemViewType(position);

        //无convertView，需要new出各个控件
        if (convertView == null) {
            Log.e("convertView = ", " NULL");

            //按当前所需的样式，确定new的布局
            switch (type) {
                case TYPE_1:
                    convertView = inflater.inflate(R.layout.listitem1, parent, false);
                    holder1 = new viewHolder1();
                    holder1.textView = (TextView) convertView.findViewById(R.id.card_show);
                     Log.e("convertView = ", "NULL TYPE_1");
                    convertView.setTag(holder1);
                    break;
                case TYPE_2:
                    convertView = inflater.inflate(R.layout.adapter_card_details_item, parent, false);
                    holder2 = new viewHolder2();

                    holder2.item_type1 = (LinearLayout) convertView.findViewById(R.id.adapter_item_card_type1);
                    holder2.imageView = (ImageView) convertView.findViewById(R.id.card_item_icon);
                    holder2.title = (TextView) convertView.findViewById(R.id.card_item_title_tv);
                    holder2.time = (TextView) convertView.findViewById(R.id.card_item_time_tv);
                    holder2.content = (TextView) convertView.findViewById(R.id.card_item_content_tv);
                    Log.e("convertView = ", "NULL TYPE_2");
                    convertView.setTag(holder2);
                    break;
                case TYPE_3:
                    convertView = inflater.inflate(R.layout.list_item_card_details_submit, parent, false);
                    holder3 = new viewHolder3();
                    holder3.item_type2 = (RelativeLayout) convertView.findViewById(R.id.adapter_item_card_type2);
                    holder3.textView = (TextView) convertView.findViewById(R.id.card_details_submit_tv);
                    holder3.imageView = (ImageView) convertView.findViewById(R.id.card_details_submit_iv);
                    Log.e("convertView = ", "NULL TYPE_3");
                    convertView.setTag(holder3);
                    break;
            }
        } else {
            //有convertView，按样式，取得不用的布局
            switch (type) {
                case TYPE_1:
                    holder1 = (viewHolder1) convertView.getTag();
                    Log.e("convertView !!!!!!= ", "NULL TYPE_1");
                    break;
                case TYPE_2:
                    holder2 = (viewHolder2) convertView.getTag();
                    Log.e("convertView !!!!!!= ", "NULL TYPE_2");
                    break;
                case TYPE_3:
                    holder3 = (viewHolder3) convertView.getTag();
                    Log.e("convertView !!!!!!= ", "NULL TYPE_3");
                    break;
            }
        }

        //设置资源
        switch (type) {
            case TYPE_1:
                if(position==0){
                    holder1.textView.setText("推荐商品");
                }else if(position==3){
                    holder1.textView.setText(getSubnum()+"条评论");
               }
                break;
            case TYPE_2:
                position = position-1;
                ImageLoaderUtil.load(mContext,mDataList.get(position).getUrl(),holder2.imageView);
                holder2.title.setText(mDataList.get(position).getWho());
                holder2.time.setText(mDataList.get(position).getCreatedAt());
                holder2.content.setText(mDataList.get(position).getDesc());
                final int finalPosition = position;
                holder2.item_type1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertUtils.showToast(mContext, finalPosition +","+mDataList.get(finalPosition).getCreatedAt());
                       Intent intent = new Intent(mContext,ShoppingDetailsActivity.class);
                        intent.putExtra("imageUrl",mDataList.get(finalPosition).getUrl());
                        mContext.startActivity(intent);
                    }
                });

                break;
            case TYPE_3:
                position = position-2;
                ImageLoaderUtil.load(mContext,mDataList.get(position).getUrl(),holder3.imageView);
                holder3.textView.setText(mDataList.get(position).getDesc());
                final int finalPosition2 = position;
                holder3.item_type2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertUtils.showToast(mContext, finalPosition2 +","+mDataList.get(finalPosition2).getCreatedAt());
                    }
                });
                break;
        }

        return convertView;
    }


    //各个布局的控件资源
    class viewHolder1 {
        TextView textView;
    }

    class viewHolder2 {
        LinearLayout item_type1;
        ImageView imageView;
        TextView title;
        TextView time;
        TextView content;

    }

    class viewHolder3 {
        ImageView imageView;
        TextView textView;
      RelativeLayout item_type2;
    }

}



