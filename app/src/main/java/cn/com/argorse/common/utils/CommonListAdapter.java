package cn.com.argorse.common.utils;/*
 * @Title CommonListAdapter.java
 * @Copyright Copyright 2010-2015 Yann Software Co,.Ltd All Rights Reserved.
 * @Description：
 * @author Yann
 * @date 2015-8-5 下午10:39:05
 * @version 1.0
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 *
 */
public abstract class CommonListAdapter<T> extends BaseAdapter
{
	protected Context mContext;
	protected List<T> mDatas;//数据集
	protected LayoutInflater mInflater;
	protected int mlayoutId;//需要加载的布局

	public CommonListAdapter(Context context, List<T> datas, int layoutId)
	{
		this.mContext = context;
		this.mDatas = datas;
		this.mlayoutId = layoutId;
		mInflater = LayoutInflater.from(context);
	}

	/**
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount()
	{
		return mDatas == null ? 0 : mDatas.size();
	}

	/**
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public T getItem(int position)
	{
		return mDatas.get(position);
	}

	/**
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position)
	{
		return position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{ListViewHolder holder = ListViewHolder.get(mContext, convertView, parent, mlayoutId, position);

		convert(holder, getItem(position),position);

		return holder.getConvertView();
	}

	public abstract void convert(ListViewHolder holder, T t,int postion);
}
