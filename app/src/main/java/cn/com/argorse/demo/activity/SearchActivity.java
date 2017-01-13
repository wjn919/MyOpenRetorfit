package cn.com.argorse.demo.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import cn.com.argorse.common.utils.CommonListAdapter;
import cn.com.argorse.common.utils.FormatStr;
import cn.com.argorse.common.utils.ListViewHolder;
import cn.com.argorse.common.view.VerticalSwipeRefreshLayout;
import cn.com.argorse.demo.BaseActivity;
import cn.com.argorse.demo.Entity.realm.HistorySearch;
import cn.com.argorse.demo.R;
import io.realm.RealmResults;

/**
 * Created by wjn on 2016/12/21.
 * 搜索页面
 */
public class SearchActivity extends BaseActivity {
    ListView lvlistsearch;
    VerticalSwipeRefreshLayout swipecontainer;
    private View mLvHeaderView;
    private ArrayList<HistorySearch> mDataList;
    private HistorySearchAdapter mAdapter;
    private ImageView deleteSearch;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initViews() {

        swipecontainer = (VerticalSwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipecontainer.setOnRefreshListener(new LvSLOnRefreshListener());
        swipecontainer.setColorSchemeResources(R.color.swipeLayout_scheme_1, R.color.swipeLayout_scheme_2, R.color.swipeLayout_scheme_3, R.color.swipeLayout_scheme_4);
        lvlistsearch = (ListView) findViewById(R.id.lv_list_search);
        lvlistsearch.addHeaderView(initHeaderView());
        deleteSearch = (ImageView) mLvHeaderView.findViewById(R.id.iv_search_list_delete);

    }


    @Override
    protected void initEvents() {
        mHeaderSearchIv.setOnClickListener(this);
        deleteSearch.setOnClickListener(this);
        lvlistsearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0){
                    String text = mDataList.get(i-1).getHistoryName();
                    Bundle bundle = new Bundle();
                    bundle.putString("search", text);
                    startActivity(SearchResultActivity.class, bundle);

                }
            }
        });

    }

    @Override
    protected void initData() {
        mHeaderSearchLl.setVisibility(View.VISIBLE);
        mHeaderTv.setVisibility(View.GONE);
        mHeaderSearchEt.setVisibility(View.VISIBLE);
        mHeaderSearchTv.setVisibility(View.GONE);
        mHeaderSearchIv.setVisibility(View.VISIBLE);

        mDataList = new ArrayList<>();
        mAdapter = new HistorySearchAdapter(mActivity, mDataList, R.layout.adapter_history_search_item);
        lvlistsearch.setAdapter(mAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        querryAll();
    }



    /**
     * 更多样式
     *
     * @return
     */
    private View initHeaderView() {
        if (mLvHeaderView == null)
            mLvHeaderView = View.inflate(mActivity, R.layout.search_list_header_view, null);
        return mLvHeaderView;
    }
    @Override
    public void onClick(View view) {
        if (view == mHeaderSearchIv) {
            String search = mHeaderSearchEt.getText().toString().trim();
            if(FormatStr.isNotNull(search)){
                insertRealm(search);
                Bundle bundle = new Bundle();
                bundle.putString("search", search);
                startActivity(SearchResultActivity.class, bundle);
            }

        }else if(view == deleteSearch){
            deleteAll();
        }

        /**
         * 下拉刷新
         *
         * @author hcp
         */


    }
    //查询数据库
    private void querryAll() {

        RealmResults<HistorySearch> historyResults = realm.where(HistorySearch.class).findAll();
        mDataList.clear();
        mDataList.addAll(historyResults);
        mAdapter.notifyDataSetChanged();
        swipecontainer.setRefreshing(false);
        swipecontainer.setEnabled(true);


    }

    //删除所有历史结果
    private void deleteAll() {
        RealmResults<HistorySearch> results =  realm.where(HistorySearch.class).findAll();
        realm.beginTransaction();
        results.clear();
        realm.commitTransaction();
        querryAll();
    }

    //插入历史数据
    private void insertRealm(String search) {
        realm.beginTransaction();
        HistorySearch historySearch = realm.createObject(HistorySearch.class);
        historySearch.setHistoryName(search);
        realm.commitTransaction();
    }

    class LvSLOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {

            querryAll();



        }
    }

    private class HistorySearchAdapter extends CommonListAdapter<HistorySearch>{


        public HistorySearchAdapter(Context context, List<HistorySearch> datas, int layoutId) {
            super(context, datas, layoutId);
        }

        @Override
        public void convert(ListViewHolder holder, HistorySearch historySearch, int postion) {
            holder.setText(R.id.tv_history_search,historySearch.getHistoryName());
        }
    }


}
