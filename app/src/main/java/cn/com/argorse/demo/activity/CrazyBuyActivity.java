package cn.com.argorse.demo.activity;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.argorse.common.http.HttpUtils;
import cn.com.argorse.common.utils.AlertUtils;
import cn.com.argorse.common.utils.CommonListAdapter;
import cn.com.argorse.common.utils.CommonRecyclerAdapter;
import cn.com.argorse.common.utils.ListViewHolder;
import cn.com.argorse.common.utils.RecyclerViewHolder;
import cn.com.argorse.common.view.AutoAdjustRecylerView;
import cn.com.argorse.common.view.VerticalSwipeRefreshLayout;
import cn.com.argorse.demo.BaseActivity;
import cn.com.argorse.demo.Entity.BaseObserver;
import cn.com.argorse.demo.Entity.GoodsType;
import cn.com.argorse.demo.Entity.ResultsEntity;
import cn.com.argorse.demo.R;
import cn.com.argorse.demo.api.testApi;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wjn on 2016/12/21.
 */
public class CrazyBuyActivity extends BaseActivity  {

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
    private CrazyBuyListAdapter mAdapter;
    private int loadType;
    private View mLvHeaderView;
    private int type =1;
    private int lastType = 1;
    private int currentType = 1;
    private ArrayList<GoodsType> mDatas;
    private AutoAdjustRecylerView mRecyclerView;
    private GalleryAdapterTwo mRecyclerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_crazy_buy;
    }

    @Override
    protected void initViews() {
        //下拉刷新
        swipeContainer = (VerticalSwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new LvSLOnRefreshListener());
        swipeContainer.setColorSchemeResources(R.color.swipeLayout_scheme_1, R.color.swipeLayout_scheme_2, R.color.swipeLayout_scheme_3, R.color.swipeLayout_scheme_4);

        mListLv = (ListView) findViewById(R.id.lv_list_crazy_buy);
        mListLv.addFooterView(initFooterMoreView());
        mListLv.addHeaderView(initHeaderView());
        mListLv.setOnScrollListener(new LvOnScrollListener());
    }


    @Override
    protected void initEvents() {

    }

    @Override
    protected void initData() {
        mDataList = new ArrayList<>();
        mDatas = new ArrayList<GoodsType>();
        for(int i = 0;i<20;i++){
            GoodsType goodsType = new GoodsType();
            goodsType.setGoodsType(i+"");
            goodsType.setGoodsName("android"+i);
            if(i==0){
                goodsType.setIschose(true);
            }else{
                goodsType.setIschose(false);
            }
            mDatas.add(goodsType);
        }
        mAdapter = new CrazyBuyListAdapter(mActivity, mDataList, R.layout.adapter_search_item);
        mListLv.setAdapter(mAdapter);
        initHeaderTab();
        loadData(LOAD_REFRESH,type);

    }




    private void initHeaderTab() {
        mRecyclerView = (AutoAdjustRecylerView) mLvHeaderView.findViewById(R.id.id_recyclerview_horizontal);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mActivity);
        horizontalLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(horizontalLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerAdapter = new GalleryAdapterTwo(this, mDatas,R.layout.choose_goodstype_item_view);

        mRecyclerView.setAdapter(mRecyclerAdapter);

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
        testApi msgApi = HttpUtils.getInstance().create(testApi.class);
        msgApi.getMessage(1)
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
                                 AlertUtils.showToast(mActivity,type+"");
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


    }



    /**
     * 重新加载数据的监听
     *
     * @author roc
     */
    class PromptRefreshListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showLoadingView();
            loadData(LOAD_REFRESH,type);
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
            loadData(LOAD_REFRESH,type);


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
            mLvHeaderView = View.inflate(mActivity, R.layout.crazy_buy_header_view, null);
        return mLvHeaderView;
    }

    /**
     * 账单列表适配器
     */
    private class CrazyBuyListAdapter extends CommonListAdapter<ResultsEntity> {

        public CrazyBuyListAdapter(Context context, List<ResultsEntity> datas, int layoutId) {
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
                            loadData(LOAD_MORE,type);
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


    private class GalleryAdapterTwo extends CommonRecyclerAdapter<GoodsType>{


        public GalleryAdapterTwo(Context ctx, List<GoodsType> list, int LayoutId) {
            super(ctx, list, LayoutId);
        }

        @Override
        public void bindData(RecyclerViewHolder holder, final int position, GoodsType item) {
            holder.setText(R.id.tv_popupwindow_item_name,item.getGoodsName());
            holder.getView(R.id.tv_popupwindow_item_name).setBackgroundResource(mDatas.get(position).isIschose() ? R.mipmap.pm_quanbushangpin_erjileibiao_xuanzhong_xiahuaxian : R.mipmap.pm_quanbushangpin_erjileibiao_weixuanzhong);
            ((TextView)holder.getView(R.id.tv_popupwindow_item_name)).setTextColor(getResources().getColor(mDatas.get(position).isIschose() ? R.color.font_red : R.color.font_black));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRecyclerView.checkAutoAdjust(position);// 为了让导航栏可以点击的时候自动调整位置
                    for (int i = 0; i < mDatas.size(); i++) {
                        mDatas.get(i).setIschose(i == position);
                    }
                    mRecyclerAdapter.notifyDataSetChanged();
                    type = Integer.parseInt(mDatas.get(position).getGoodsType());
                    loadData( LOAD_REFRESH,type);
                }
            });

        }
    }
}
