package cn.com.argorse.demo.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.argorse.common.http.HttpUtils;
import cn.com.argorse.common.utils.AlertUtils;
import cn.com.argorse.common.utils.CommonRecyclerAdapter;
import cn.com.argorse.common.utils.RecyclerViewHolder;
import cn.com.argorse.common.view.VerticalSwipeRefreshLayout;
import cn.com.argorse.common.view.WrapRecyclerView;
import cn.com.argorse.demo.BaseApplication;
import cn.com.argorse.demo.BaseFragment;
import cn.com.argorse.demo.Entity.BaseObserver;
import cn.com.argorse.demo.Entity.ResultsEntity;
import cn.com.argorse.demo.R;
import cn.com.argorse.demo.activity.ImageActivity;
import cn.com.argorse.demo.api.testApi;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by wjn on 2016/11/23.
 */
public class ArticleFragment extends BaseFragment {
    public static final String BUNDLE_KEY_CATALOG = "BUNDLE_KEY_CATALOG";
    private int mCatalog = 1;
    private WrapRecyclerView mRecyclerView;
    private ArrayList<ResultsEntity> mDataList;
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
    private int loadType;
    private CommonRecyclerAdapter<ResultsEntity> mAdapter;
    private boolean isFirst;
    private View mLvFooterMoreV;
    private VerticalSwipeRefreshLayout swipeContainer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_article;
    }

    @Override
    protected void initViews() {
        getArgument();
        String msg = getSearchKeyword();

        //下拉刷新
        swipeContainer = (VerticalSwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new LvSLOnRefreshListener());
        swipeContainer.setColorSchemeResources(R.color.swipeLayout_scheme_1, R.color.swipeLayout_scheme_2, R.color.swipeLayout_scheme_3, R.color.swipeLayout_scheme_4);


        mRecyclerView = (WrapRecyclerView) findViewById(R.id.rl_list_main);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mRecyclerView.addFootView(initFooterMoreView());
    }


    private void getArgument() {
        Bundle args = getArguments();
        if (args != null) {
            mCatalog = args.getInt(BUNDLE_KEY_CATALOG, 0);
        }
    }

    @NonNull
    private String getSearchKeyword() {
        String keyword = "";
        switch (mCatalog) {
            case 0:
                keyword = "Android";
                break;
            case 1:
                keyword = "iOS";
                break;
            case 2:
                keyword = "前端";
                break;
        }
        return keyword;
    }

    @Override
    protected void initEvents() {
        mRecyclerView.addOnScrollListener(new LvOnScrollListener());
    }

    @Override
    protected void initData() {
        mDataList = new ArrayList<>();
        mAdapter = new MyAdapter(mActivity, mDataList, R.layout.list_item_layout_hospital);

    }

    @Override
    protected void loadData() {
        loadData(LOAD_REFRESH);
    }

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

        testApi hosApi = HttpUtils.getInstance(BaseApplication.Server_Url).create(testApi.class);
        hosApi.getMessage(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<ResultsEntity>>(getActivity()) {

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
                            isFirst = true;
                        }
                        mDataList.addAll(response);

                        AlertUtils.showToast(mActivity, mCatalog + " ");
                        if (loadType == LOAD_REFRESH) {
                            if (isFirst) {
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        }
                        if (loadType == LOAD_MORE) {
                            setFooterMoreView(false);
                        }


                        if (mDataList.size() < 1) {
                            showPromptView(getResources().getString(R.string.no_message), null);
                        }

                        mAdapter.notifyItemInserted(mAdapter.getItemCount());//必须用此方法才能进行
                        mAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onCompleted() {
                        isLoadMoreOK = true;
                        isFirst = false;
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
     * 重新加载数据的监听
     *
     * @author roc
     */
    class PromptRefreshListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            showLoadingView();
            loadData();

        }

    }

    class MyAdapter extends CommonRecyclerAdapter<ResultsEntity> {


        public MyAdapter(Context ctx, List<ResultsEntity> list, int LayoutId) {
            super(ctx, list, LayoutId);
        }

        @Override
        public void bindData(RecyclerViewHolder holder, final int position, ResultsEntity item) {
            holder.setText(R.id.common_tv, item.getWho()).setImage(R.id.common_iv, item.getUrl());
            final ImageView iv = (ImageView) holder.getView(R.id.common_iv);
            //5.0之后点击动画，共享模式
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iv.setTransitionName("share");
            }
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = mDatas.get(position).getUrl();
                    Bundle bundle = new Bundle();
                    bundle.putString("url", url);
                    startActivity(ImageActivity.class, bundle, REQUEST_ACTIVITY, iv);


                }
            });
        }
    }


    /**
     * 更多样式
     *
     * @return
     */
    private View initFooterMoreView() {
        if (mLvFooterMoreV == null)
            mLvFooterMoreV = LayoutInflater.from(getContext()).inflate(R.layout.inc_v_more, mRecyclerView, false);//这样写就可以解决不居中的问题。

        mLvFooterMoreV.setVisibility(View.GONE);
        // 上拉更多
        return mLvFooterMoreV;
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

    /**
     * list滚动监听
     */
    class LvOnScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView view, int scrollState) {
            switch (scrollState) {
                case RecyclerView.SCROLL_STATE_IDLE:// 当不滚动时
//	                Log.v("已经停止：SCROLL_STATE_IDLE");
                    // 判断滚动到底部
                    if (/*!swipeContainer.isRefreshing() &&*/ isVisBottom(view)) {
                        // 上拉加载更多
                        if (isLoadMoreOK) {
                            loadData(LOAD_MORE);
                        }
                    }
                    break;

            }
        }


    }

    public static boolean isVisBottom(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        if (visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == recyclerView.SCROLL_STATE_IDLE) {
            return true;
        } else {
            return false;
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
}
