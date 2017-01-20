package cn.com.argorse.demo.activity;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.argorse.common.http.HttpUtils;
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
 * Created by wjn on 2016/12/22.
 */

public class NormalListViewActivity extends BaseActivity {
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
    private NormalListAdapter mAdapter;
    private int loadType;
    private View mLvHeaderView;
    private String searResult;
    private String showType;
    private String showTypeText;
    private ImageView headerImage;
    private TextView headerText;

    @Override
    protected void getIntentBundle() {
        showType = getIntent().getStringExtra("showType");
        showTypeText = getIntent().getStringExtra("showTypeText");
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_normal_list;
    }

    @Override
    protected void initViews() {
        //下拉刷新
        swipeContainer = (VerticalSwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new LvSLOnRefreshListener());
        swipeContainer.setColorSchemeResources(R.color.swipeLayout_scheme_1, R.color.swipeLayout_scheme_2, R.color.swipeLayout_scheme_3, R.color.swipeLayout_scheme_4);

        mListLv = (ListView) findViewById(R.id.lv_list_normal);
        mListLv.addFooterView(initFooterMoreView());
        initHeaderView();
        mListLv.setOnScrollListener(new LvOnScrollListener());

    }


    @Override
    protected void initEvents() {

    }

    @Override
    protected void initData() {
        if(FormatStr.isNotNull(searResult)){
            mHeaderTv.setText(searResult);
        }
        mDataList = new ArrayList<>();
        mAdapter = new NormalListAdapter(mActivity, mDataList, R.layout.adapter_search_item);
        mListLv.setAdapter(mAdapter);
        loadData(LOAD_REFRESH);
        if(FormatStr.isNotNull(showType)&&FormatStr.isNotNull(showTypeText)){
            mHeaderTv.setText(showTypeText);
        }
        headerImage = (ImageView) mLvHeaderView.findViewById(R.id.iv_normal_list_header);
        headerText = (TextView)mLvHeaderView.findViewById(R.id.tv_normal_list_header);

        if(showType.equals("1")){//超值爆款
            mLvHeaderView.setVisibility(View.GONE);
        }else if(showType.equals("2")){//超值9.9
            mListLv.addHeaderView(mLvHeaderView);
            mLvHeaderView.setVisibility(View.VISIBLE);
            headerImage.setBackground(getResources().getDrawable(R.mipmap.ninebaoyou));
            headerText.setText("超值9块9，劵后全场9块9包邮");

        }else if(showType.equals("3")){
            mListLv.addHeaderView(mLvHeaderView);
            mLvHeaderView.setVisibility(View.VISIBLE);
            headerImage.setBackground(getResources().getDrawable(R.mipmap.baicai));
            headerText.setText("白菜券，精选6元以下好货，值得购买！");

        }

    }

    /**
     * 获取交易记录列表
     */
    private void loadData(int mLoadType) {
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
        msgApi.getMessage(3)
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
    class PromptRefreshListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            showLoadingView();
            loadData(LOAD_REFRESH);
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
            loadData(LOAD_REFRESH);


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
        if (mLvHeaderView == null){
            mLvHeaderView = View.inflate(mActivity, R.layout.normal_list_header_view, null);
        }
        mLvHeaderView.setVisibility(View.GONE);
        return mLvHeaderView;
    }

    /**
     * 账单列表适配器
     */
    private class NormalListAdapter extends CommonListAdapter<ResultsEntity> {

        public NormalListAdapter(Context context, List<ResultsEntity> datas, int layoutId) {
            super(context, datas, layoutId);
        }


        @Override
        public void convert(ListViewHolder holder, ResultsEntity messageEntity, final int position) {
            holder.setText(R.id.messgae_item_title_tv, messageEntity.getWho())
                    .setText(R.id.messgae_item_time_tv, messageEntity.getCreatedAt())
                    .setText(R.id.messgae_item_content_tv, messageEntity.getDesc());
            holder.setImage(R.id.messgae_item_icon,messageEntity.getUrl());

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
                            loadData(LOAD_MORE);
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
