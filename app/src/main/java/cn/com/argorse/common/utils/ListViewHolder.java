package cn.com.argorse.common.utils;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.argorse.demo.BaseApplication;

/**
 * 类注释
 *
 * @author Yann
 * @date 2015-8-5 下午9:08:31
 */
public class ListViewHolder {
    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;
    private Context mContext;

    public View getConvertView() {
        return mConvertView;
    }

    public ListViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mContext = context;
        this.mViews = new SparseArray<View>();
        this.mPosition = position;
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        this.mConvertView.setTag(this);
    }

    public static ListViewHolder get(Context context, View convertView,
                                     ViewGroup parent, int layoutId, int position) {
        if (null == convertView) {
            return new ListViewHolder(context, parent, layoutId, position);
        } else {
            ListViewHolder holder = (ListViewHolder) convertView.getTag();
            holder.mPosition = position;

            return holder;
        }
    }


    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return T
     * @author Yann
     * @date 2015-8-5 下午9:38:39
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);

        if (null == view) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }

        return (T) view;
    }

    /**
     * 给ID为viewId的TextView设置文字text，并返回this
     *
     * @param viewId
     * @param text
     * @return ListViewHolder
     * @author Yann
     * @date 2015-8-5 下午11:05:17
     */
    public ListViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);

        return this;
    }

    public ListViewHolder setTextColor(int viewId, int color) {
        TextView tv = getView(viewId);
        tv.setTextColor(color);

        return this;
    }

    /**
     * 给ID为viewId的ImageView设置图片url，并返回this
     *
     * @param viewId
     * @param url
     * @return ListViewHolder
     * @author Yann
     * @date 2015-8-5 下午11:05:17
     */
    public ListViewHolder setImage(int viewId, String url) {
        ImageView iv = getView(viewId);
        ImageLoaderUtil.load(mContext, url,iv);
        return this;
    }

    /**
     * 给ID为viewId的View设置隐藏或者显示
     *
     * @param viewId
     * @return ListViewHolder
     * @author Yann
     * @date 2015-8-5 下午11:05:17
     */
    public ListViewHolder setVisibility(int viewId, int visibility) {
        View view = getView(viewId);
        view.setVisibility(visibility);

        return this;
    }

    public ListViewHolder setOnClickListener(int viewId, final OnClickListener l) {
        View view = getView(viewId);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onClick(v, mPosition);
            }
        });

        return this;
    }

    public interface OnClickListener {
        void onClick(View view, int position);
    }
}
