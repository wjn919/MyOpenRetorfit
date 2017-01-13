package cn.com.argorse.common.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;


/**
 * 目的是可以让RecyclerView添加头和尾布局，和RecyclerWrapAdapter连用达到效果
 */
public class WrapRecyclerView extends RecyclerView {

    private ArrayList<View> mHeaderViews = new ArrayList<>() ;

    private ArrayList<View> mFootViews = new ArrayList<>() ;

    private Adapter mAdapter ;

    public WrapRecyclerView(Context context) {
        super(context);
    }

    public WrapRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addHeaderView(View view){
        mHeaderViews.clear();
        mHeaderViews.add(view);
        if (mAdapter != null){
            if (!(mAdapter instanceof RecyclerWrapAdapter)){
                mAdapter = new RecyclerWrapAdapter(this, mHeaderViews,mFootViews,mAdapter) ;
//                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void addFootView(View view){
        mFootViews.clear();
        mFootViews.add(view);
        if (mAdapter != null){
            if (!(mAdapter instanceof RecyclerWrapAdapter)){
                mAdapter = new RecyclerWrapAdapter(this,mHeaderViews,mFootViews,mAdapter) ;
              // mAdapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    public void setAdapter(Adapter adapter) {

        if (mHeaderViews.isEmpty()&&mFootViews.isEmpty()){
            super.setAdapter(adapter);
        }else {
            adapter = new RecyclerWrapAdapter(this, mHeaderViews,mFootViews,adapter) ;

            super.setAdapter(adapter);
        }
        mAdapter = adapter ;
    }

}
