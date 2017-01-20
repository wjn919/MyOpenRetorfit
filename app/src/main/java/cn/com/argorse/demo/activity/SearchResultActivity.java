package cn.com.argorse.demo.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.argorse.common.http.HttpUtils;
import cn.com.argorse.common.utils.AlertUtils;
import cn.com.argorse.common.utils.CommonListAdapter;
import cn.com.argorse.common.utils.FormatStr;
import cn.com.argorse.common.utils.ListViewHolder;
import cn.com.argorse.common.view.VerticalSwipeRefreshLayout;
import cn.com.argorse.demo.BaseActivity;
import cn.com.argorse.demo.BaseApplication;
import cn.com.argorse.demo.Entity.BaseObserver;
import cn.com.argorse.demo.Entity.ResultsEntity;
import cn.com.argorse.demo.R;
import cn.com.argorse.demo.api.testApi;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wjn on 2016/12/21.
 * 搜索结果页面
 */
public class SearchResultActivity extends BaseActivity {

    private VerticalSwipeRefreshLayout swipeContainer;//下拉刷新
    private ListView mListLv;//ListView
    private View mLvFooterMoreV;//尾部布局
    /**
     * 刷新 数据加载完毕
     **/
    private final int LOAD_REFRESH = 0;
    /**
     * 更多 数据加载完毕
     **/
    private final int LOAD_MORE = 1;
    boolean isLoadMoreOK = true;
    final int rowsMore = 10, rowsDefault = 20;
    int beginRow = 0, rows = rowsDefault;
    private ArrayList<ResultsEntity> mDataList;
    private SearchResultListAdapter mAdapter;
    private int loadType;
    private View mLvHeaderView;
    private TextView headerPop;
    private TextView headerCheap;
    private TextView headerLow;
    private TextView headerBuy;
    private TextView headerNew;
    private int type = 1;

    private int lastType = 1;
    private int currentType = 1;
    private String searResult;

    @Override
    protected void getIntentBundle() {
        searResult = getIntent().getStringExtra("search");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_result;
    }

    @Override
    protected void initViews() {
        //下拉刷新
        swipeContainer = (VerticalSwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new LvSLOnRefreshListener());
        swipeContainer.setColorSchemeResources(R.color.swipeLayout_scheme_1, R.color.swipeLayout_scheme_2, R.color.swipeLayout_scheme_3, R.color.swipeLayout_scheme_4);

        mListLv = (ListView) findViewById(R.id.lv_list_search_result);
        mListLv.addFooterView(initFooterMoreView());
        mListLv.addHeaderView(initHeaderView());
        mListLv.setOnScrollListener(new LvOnScrollListener());
    }


    @Override
    protected void initEvents() {

    }

    @Override
    protected void initData() {
        if (FormatStr.isNotNull(searResult)) {
            mHeaderTv.setText(searResult);
        }
        mDataList = new ArrayList<>();
        mAdapter = new SearchResultListAdapter(mActivity, mDataList, R.layout.adapter_search_item);
        mListLv.setAdapter(mAdapter);
        initHeaderTab();
        loadData(LOAD_REFRESH, type);
        mListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertUtils.showToast(mActivity, i + "");
                Bundle bundle = new Bundle();
                bundle.putString("imageUrl", "http://ww3.sinaimg.cn/large/610dc034gw1fbsfgssfrwj20u011h48y.jpg");
                startActivity(ShoppingDetailsActivity.class, bundle, REQUEST_ACTIVITY);
            }
        });

    }


    private void initHeaderTab() {
        headerNew = (TextView) mLvHeaderView.findViewById(R.id.msg_tab_new);
        headerPop = (TextView) mLvHeaderView.findViewById(R.id.msg_tab_pop);
        headerCheap = (TextView) mLvHeaderView.findViewById(R.id.msg_tab_cheap);
        headerLow = (TextView) mLvHeaderView.findViewById(R.id.msg_tab_price_low);
        headerBuy = (TextView) mLvHeaderView.findViewById(R.id.msg_tab_buy);
        headerNew.setOnClickListener(this);
        headerPop.setOnClickListener(this);
        headerCheap.setOnClickListener(this);
        headerLow.setOnClickListener(this);
        headerBuy.setOnClickListener(this);

    }


    /**
     * 获取交易记录列表
     */
    private void loadData(int mLoadType, final int type) {
        loadType = mLoadType;
        if (loadType == LOAD_REFRESH) {
            beginRow = 0;// 开始条数
            rows = rowsDefault;// 加载条数
        } else {
            isLoadMoreOK = false;
            beginRow = mDataList.size();// 开始条数
            rows = rowsMore;// 加载条数
        }
        testApi msgApi = HttpUtils.getInstance(BaseApplication.Server_Url).create(testApi.class);
        msgApi.getMessage(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<ResultsEntity>>(this) {

                    @Override
                    public void onStart() {
                        if (loadType == LOAD_REFRESH) {// 刷新
                            mLvFooterMoreV.setVisibility(View.GONE);
                            if (mDataList.size() < 1) {
                                showLoadingView();
                            }
                        } else {
                            setFooterMoreView(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        mLvFooterMoreV.setVisibility(View.GONE);
                        if (LOAD_REFRESH == loadType && mDataList.size() < 1) {
                            showPromptView(e.getMessage(), new PromptRefreshListener());
                        }


                    }

                    @Override
                    public void onSuccess(List<ResultsEntity> response) {
                        if (loadType == LOAD_REFRESH) {
                            mDataList.clear();// 刷新
                        }
                        AlertUtils.showToast(mActivity, type + "");
                        lastType = type;
                        mDataList.addAll(response);
                        if (loadType == LOAD_MORE) {
                            setFooterMoreView(false);
                        }
                        if (loadType == LOAD_REFRESH)
                            mListLv.setSelection(0);// 切换收到送出后滑动到顶部

                        if (mDataList.size() < 1)
                            showPromptView(getResources().getString(R.string.no_message), null);
                        mAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCompleted() {
                        isLoadMoreOK = true;
                        if (loadType == LOAD_REFRESH) {// 刷新
                            swipeContainer.setRefreshing(false);
                            // 更新完后调用该方法结束刷新
                            swipeContainer.setEnabled(true);
                            dismissLoadingView();
                        }


                    }
                });


    }


    /**
     * 设置listview底部显示的内容
     *
     * @param isLoading 是否加载中
     * @return void
     * @Title: setFooterMoreView
     */
    private void setFooterMoreView(boolean isLoading) {
        if (mLvFooterMoreV != null) {
            mLvFooterMoreV.setVisibility(View.VISIBLE);
            mLvFooterMoreV.findViewById(R.id.pb_more).setVisibility(isLoading ? View.VISIBLE : View.GONE);
            ((TextView) mLvFooterMoreV.findViewById(R.id.tv_more)).setText(isLoading ? R.string.footer_more_loading : R.string.footer_more_nomore);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == headerNew) {
            type = 1;
            currentType = type;
            headerNew.setTextColor(getResources().getColor(R.color.font_red));
            headerPop.setTextColor(getResources().getColor(R.color.font_gray));
            headerCheap.setTextColor(getResources().getColor(R.color.font_gray));
            headerLow.setTextColor(getResources().getColor(R.color.font_gray));
            headerBuy.setTextColor(getResources().getColor(R.color.font_gray));
            headerNew.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_xuanzhong_xiahuaxian));
            headerPop.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));
            headerCheap.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));
            headerLow.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));
            headerBuy.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));

            if (currentType != lastType) {
                loadData(LOAD_REFRESH, type);

            }


        } else if (v == headerPop) {
            type = 2;
            currentType = type;
            headerNew.setTextColor(getResources().getColor(R.color.font_gray));
            headerPop.setTextColor(getResources().getColor(R.color.font_red));
            headerCheap.setTextColor(getResources().getColor(R.color.font_gray));
            headerLow.setTextColor(getResources().getColor(R.color.font_gray));
            headerBuy.setTextColor(getResources().getColor(R.color.font_gray));

            headerNew.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));
            headerPop.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_xuanzhong_xiahuaxian));
            headerCheap.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));
            headerLow.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));
            headerBuy.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));

            if (currentType != lastType) {
                loadData(LOAD_REFRESH, type);

            }
        } else if (v == headerCheap) {
            type = 3;
            currentType = type;
            headerNew.setTextColor(getResources().getColor(R.color.font_gray));
            headerPop.setTextColor(getResources().getColor(R.color.font_gray));
            headerCheap.setTextColor(getResources().getColor(R.color.font_red));
            headerLow.setTextColor(getResources().getColor(R.color.font_gray));
            headerBuy.setTextColor(getResources().getColor(R.color.font_gray));

            headerNew.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));
            headerPop.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));
            headerCheap.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_xuanzhong_xiahuaxian));
            headerLow.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));
            headerBuy.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));

            if (currentType != lastType) {
                loadData(LOAD_REFRESH, type);

            }

        } else if (v == headerLow) {
            type = 4;
            currentType = type;
            headerNew.setTextColor(getResources().getColor(R.color.font_gray));
            headerPop.setTextColor(getResources().getColor(R.color.font_gray));
            headerCheap.setTextColor(getResources().getColor(R.color.font_gray));
            headerLow.setTextColor(getResources().getColor(R.color.font_red));
            headerBuy.setTextColor(getResources().getColor(R.color.font_gray));

            headerNew.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));
            headerPop.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));
            headerCheap.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));
            headerLow.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_xuanzhong_xiahuaxian));
            headerBuy.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));

            if (currentType != lastType) {
                loadData(LOAD_REFRESH, type);

            }

        } else if (v == headerBuy) {
            type = 5;
            currentType = type;
            headerNew.setTextColor(getResources().getColor(R.color.font_gray));
            headerPop.setTextColor(getResources().getColor(R.color.font_gray));
            headerCheap.setTextColor(getResources().getColor(R.color.font_gray));
            headerLow.setTextColor(getResources().getColor(R.color.font_gray));
            headerBuy.setTextColor(getResources().getColor(R.color.font_red));

            headerNew.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));
            headerPop.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));
            headerCheap.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));
            headerLow.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong));
            headerBuy.setBackground(getResources().getDrawable(R.mipmap.pm_quanbushangpin_erjileibiao_xuanzhong_xiahuaxian));

            if (currentType != lastType) {
                loadData(LOAD_REFRESH, type);

            }

        }


    }


    /**
     * 重新加载数据的监听
     *
     * @author roc
     */
    class PromptRefreshListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            showLoadingView();
            loadData(LOAD_REFRESH, type);
        }

    }

    /**
     * 下拉刷新
     *
     * @author hcp
     */
    class LvSLOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            loadData(LOAD_REFRESH, type);


        }
    }


    /**
     * 更多样式
     *
     * @return
     */
    private View initFooterMoreView() {
        if (mLvFooterMoreV == null)
            mLvFooterMoreV = View.inflate(mActivity, R.layout.inc_v_more, null);
        mLvFooterMoreV.setVisibility(View.GONE);
        // 上拉更多
        return mLvFooterMoreV;
    }

    /**
     * 更多样式
     *
     * @return
     */
    private View initHeaderView() {
        if (mLvHeaderView == null)
            mLvHeaderView = View.inflate(mActivity, R.layout.search_result_header_view, null);
        return mLvHeaderView;
    }

    /**
     * 账单列表适配器
     */
    private class SearchResultListAdapter extends CommonListAdapter<ResultsEntity> {

        public SearchResultListAdapter(Context context, List<ResultsEntity> datas, int layoutId) {
            super(context, datas, layoutId);
        }


        @Override
        public void convert(ListViewHolder holder, ResultsEntity messageEntity, final int position) {
            holder.setText(R.id.messgae_item_title_tv, messageEntity.getWho())
                    .setText(R.id.messgae_item_time_tv, messageEntity.getCreatedAt())
                    .setText(R.id.messgae_item_content_tv, messageEntity.getDesc());

        }


    }

    /**
     * list scroll
     */
    class LvOnScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 当不滚动时
//	                Log.v("已经停止：SCROLL_STATE_IDLE");
                    // 判断滚动到底部
                    if (!swipeContainer.isRefreshing() && view.getLastVisiblePosition() == (view.getCount() - 1)) {
                        // 上拉加载更多
                        if (isLoadMoreOK) {
                            loadData(LOAD_MORE, type);
                        }
                    }
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
//	                Log.v("开始滚动：SCROLL_STATE_FLING");
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
//	            	Log.v("正在滚动：SCROLL_STATE_TOUCH_SCROLL");
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

        }

    }


}
