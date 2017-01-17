package cn.com.argorse.demo.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.com.argorse.common.http.HttpUtils;
import cn.com.argorse.common.utils.CommonRecyclerAdapter;
import cn.com.argorse.common.utils.AlertUtils;
import cn.com.argorse.common.utils.RecyclerViewHolder;
import cn.com.argorse.common.view.VerticalSwipeRefreshLayout;
import cn.com.argorse.common.view.WrapRecyclerView;
import cn.com.argorse.demo.BaseApplication;
import cn.com.argorse.demo.BaseFragment;
import cn.com.argorse.demo.Entity.BaseObserver;

import cn.com.argorse.demo.Entity.ResultsEntity;
import cn.com.argorse.demo.R;
import cn.com.argorse.demo.activity.CrazyBuyActivity;
import cn.com.argorse.demo.activity.ImageActivity;
import cn.com.argorse.demo.activity.InndianaActivity;
import cn.com.argorse.demo.activity.MainActivity;
import cn.com.argorse.demo.activity.NormalListViewActivity;
import cn.com.argorse.demo.activity.SearchActivity;
import cn.com.argorse.demo.adapter.MainViewPagerAdapter;
import cn.com.argorse.demo.api.testApi;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wjn on 2016/11/8.
 *
 * @author wjn
 *         首页
 */
public class MainFragment extends BaseFragment implements CommonRecyclerAdapter.OnItemClickListener, View.OnClickListener {

    private WrapRecyclerView mRecyclerView;
    private VerticalSwipeRefreshLayout swipeContainer;
    private View mLvFooterMoreV;
    private int loadType;
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
    private CommonRecyclerAdapter<ResultsEntity> mAdapter;
    private boolean isFirst = true;
    private View mLvHeaderMoreV;
    private ViewPager mViewPager;
    private LinearLayout mPointGroup;
    private RelativeLayout ll_vp;
    private ImageView[] imagePointViews;
    private List<ResultsEntity> urlList;
    private MainViewPagerAdapter mViewPagerAdapter;
    private MainFragment.ImageHandler handler = new MainFragment.ImageHandler(new WeakReference<MainFragment>(this));
    private int sum = 2;
    private int num = 2;
    private LinearLayout mainpop;//超值爆款
    private LinearLayout mainnine;//9.9包邮
    private LinearLayout maincheap;//白菜价
    private TextView maininndiana;//会夺宝
    private TextView crazybuy;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initViews() {
        //下拉刷新
        swipeContainer = (VerticalSwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new LvSLOnRefreshListener());
        swipeContainer.setColorSchemeResources(R.color.swipeLayout_scheme_1, R.color.swipeLayout_scheme_2, R.color.swipeLayout_scheme_3, R.color.swipeLayout_scheme_4);
        mRecyclerView = (WrapRecyclerView) findViewById(R.id.rl_list_main);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));


    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initData() {

        mDataList = new ArrayList<>();
        urlList = new ArrayList<>();
        mAdapter = new MyMainAdapter(mActivity, mDataList, R.layout.list_item_layout_hospital);
        mRecyclerView.addFootView(initFooterMoreView());
        mRecyclerView.addHeaderView(initHeaderMoreView());
        //设置item点击事件
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.addOnScrollListener(new LvOnScrollListener());
        initViewPager();
        //initViewPagerData(urlList);
        (((MainActivity) getActivity()).getHeaderView()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(SearchActivity.class);
            }
        });


        mainpop = (LinearLayout) mLvHeaderMoreV.findViewById(R.id.main_pop_sale);
        mainnine = (LinearLayout) mLvHeaderMoreV.findViewById(R.id.main_sale_nine);
        maincheap = (LinearLayout) mLvHeaderMoreV.findViewById(R.id.main_cheap_sale);
        mainpop.setOnClickListener(this);
        mainnine.setOnClickListener(this);
        maincheap.setOnClickListener(this);


        maininndiana = (TextView) mLvHeaderMoreV.findViewById(R.id.tv_main_header_inndiana);
        crazybuy = (TextView) mLvHeaderMoreV.findViewById(R.id.tv_main_header_crazy_buy);

        maininndiana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(InndianaActivity.class);
            }
        });
        crazybuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(CrazyBuyActivity.class);
            }
        });


    }


    @Override
    protected void loadData() {
        initUrl();
        loadData(LOAD_REFRESH);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.scrollToPosition(0);
        ((MainActivity) getActivity()).showTitle();
        ((MainActivity) getActivity()).showHeaderSearch();


    }

    private void initViewPager() {
        mViewPager = (ViewPager) mLvHeaderMoreV.findViewById(R.id.vp_header_main_guidePages);
        RelativeLayout.LayoutParams vpLp = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();
        vpLp.height = mScreenWidth * (Integer.parseInt(getResources().getString(R.string.viewpager_height))) / (Integer.parseInt(getResources().getString(R.string.viewpager_width)));
        mViewPager.setLayoutParams(vpLp);
        mPointGroup = (LinearLayout) mLvHeaderMoreV.findViewById(R.id.ll_header_main_point_viewGroup);
        ll_vp = (RelativeLayout) mLvHeaderMoreV.findViewById(R.id.ll_header_vp);
        ll_vp.setVisibility(View.GONE);
    }

    private void initViewPagerData(List<ResultsEntity> urlList) {
        //设置小圆点
        //小圆点的个数和图片个数一致
        mPointGroup.removeAllViews();//避免重复
        imagePointViews = new ImageView[urlList.size()];
        // 设置每个小圆点距离左边的间距
        LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        margin.setMargins(10, 0, 0, 10);
        for (int i = 0; i < urlList.size(); i++) {
            ImageView imageView = new ImageView(getActivity());
            // 设置每个小圆点的宽高
            imageView.setLayoutParams(new LinearLayout.LayoutParams(15, 15));
            imagePointViews[i] = imageView;
            if (i == 0) {

                // 默认选中第一张图片
                imagePointViews[i].setBackgroundResource(R.mipmap.page_indicator_focused);
            } else {
                // 其他图片都设置未选中状态
                imagePointViews[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
            }
            mPointGroup.addView(imagePointViews[i], margin);

        }
    }

    private void initUrl() {

        testApi viewPagerApi = HttpUtils.getInstance(BaseApplication.Server_Url).create(testApi.class);
        viewPagerApi.getMessage(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<ResultsEntity>>(getContext()) {
                    @Override
                    public void onStart() {
                        if (urlList.size() < 1) {
                            showLoadingView();
                        }

                    }

                    @Override
                    public void onSuccess(List<ResultsEntity> list) {
                        if (loadType == LOAD_REFRESH) {
                            urlList.clear();// 刷新
                        }
                        urlList.addAll(list);
                        num--;
                        if (num == 0) {
                            updateUi();
                        }
                    }


                    @Override
                    public void onCompleted() {
                        sum--;
                        if (sum == 0) {
                            updateComplete();

                        }
                    }


                });
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
        testApi hosApi = HttpUtils.getInstance(BaseApplication.Server_Url).create(testApi.class);
        hosApi.getMessage(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<ResultsEntity>>(getActivity())
                {
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
                                if (loadType == LOAD_REFRESH) {
                                    num--;
                                    if (num == 0) {
                                        updateUi();
                                        return;
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
                        if (loadType == LOAD_MORE) {
                            updateComplete();
                        } else if (loadType == LOAD_REFRESH) {
                            sum--;
                            if (sum == 0) {
                                if (sum == 0) {
                                    updateComplete();

                                }
                            }
                        }


                    }
                });
    }


    private void updateUi() {
        if (isFirst) {
            mRecyclerView.setAdapter(mAdapter);
        }
        initViewPagerData(urlList);
        mViewPagerAdapter = new MainViewPagerAdapter(mActivity, urlList);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnPageChangeListener(paggeListener);
        mViewPager.setCurrentItem(Integer.MAX_VALUE / 2);
        //开始轮播效果
        handler.sendEmptyMessageDelayed(MainFragment.ImageHandler.MSG_UPDATE_IMAGE_FIRST, MainFragment.ImageHandler.MSG_DELAY);
        ll_vp.setVisibility(View.VISIBLE);


    }

    private void updateComplete() {
        isLoadMoreOK = true;
        isFirst = false;
        if (loadType == LOAD_REFRESH) {// 刷新
            swipeContainer.setRefreshing(false);
            swipeContainer.setEnabled(true);
            dismissLoadingView();

        }
        num = 2;
        sum = 2;
    }

    @Override
    public void onItemClick(View itemView, int pos) {
        AlertUtils.showToast(mActivity, (pos - 1) + "");

    }

    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        if (view == mainpop) {
            bundle.putString("showType", "1");
            bundle.putString("showTypeText", getResources().getString(R.string.pop_sale));
            startActivity(NormalListViewActivity.class, bundle);
        } else if (view == mainnine) {
            bundle.putString("showType", "2");
            bundle.putString("showTypeText", getResources().getString(R.string.nine_sale));
            startActivity(NormalListViewActivity.class, bundle);
        } else if (view == maincheap) {
            bundle.putString("showType", "3");
            bundle.putString("showTypeText", getResources().getString(R.string.cheap_sale));
            startActivity(NormalListViewActivity.class, bundle);
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
            loadData();

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
            loadType = LOAD_REFRESH;
            handler.removeMessages(1);
            handler.removeMessages(2);
            handler.removeMessages(3);
            handler.removeMessages(4);
            handler.removeMessages(5);
            loadData();

        }
    }

   /* */

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
                    if (!swipeContainer.isRefreshing() && isVisBottom(view)) {
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
     * 头布局
     *
     * @return
     */
    private View initHeaderMoreView() {
        if (mLvHeaderMoreV == null)
            mLvHeaderMoreV = LayoutInflater.from(getContext()).inflate(R.layout.main_recycler_header, mRecyclerView, false);//这样写就可以解决不居中的问题。

        return mLvHeaderMoreV;
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

    class MyMainAdapter extends CommonRecyclerAdapter<ResultsEntity> {


        public MyMainAdapter(Context ctx, List<ResultsEntity> list, int LayoutId) {
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

    //viewpager自动循环的Handler
    private static class ImageHandler extends Handler {

        /**
         * 请求更新显示的View。
         */
        protected static final int MSG_UPDATE_IMAGE = 1;
        /**
         * 请求暂停轮播。
         */
        protected static final int MSG_KEEP_SILENT = 2;
        /**
         * 请求恢复轮播。
         */
        protected static final int MSG_BREAK_SILENT = 3;
        /**
         * 记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。
         * 例如当前如果在第一页，本来准备播放的是第二页，而这时候用户滑动到了末页，
         * 则应该播放的是第一页，如果继续按照原来的第二页播放，则逻辑上有问题。
         */
        protected static final int MSG_PAGE_CHANGED = 4;
        public static final int MSG_UPDATE_IMAGE_FIRST = 5;
        //轮播间隔时间
        protected static final long MSG_DELAY = 3000;


        //使用弱引用避免Handler泄露.这里的泛型参数可以不是Activity，也可以是Fragment等
        private WeakReference<MainFragment> weakReference;
        private int currentItem = 0;

        protected ImageHandler(WeakReference<MainFragment> wk) {
            weakReference = wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            MainFragment activity = weakReference.get();
            if (activity == null) {
                //Activity已经回收，无需再处理UI了
                return;
            }
            //检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题。
            if (activity.handler.hasMessages(MSG_UPDATE_IMAGE)) {
                activity.handler.removeMessages(MSG_UPDATE_IMAGE);
            }
            switch (msg.what) {
                case MSG_UPDATE_IMAGE:
                    currentItem++;
                    activity.mViewPager.setCurrentItem(currentItem);
                    //准备下次播放
                    activity.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_UPDATE_IMAGE_FIRST:
                    currentItem++;
                    activity.mViewPager.setCurrentItem(currentItem);
                    //准备下次播放
                    activity.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE_FIRST, MSG_DELAY);
                    break;
                case MSG_KEEP_SILENT:
                    //只要不发送消息就暂停了
                    break;
                case MSG_BREAK_SILENT:
                    activity.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_PAGE_CHANGED:
                    //记录当前的页号，避免播放的时候页面显示不正确。
                    currentItem = msg.arg1;
                    break;
                default:
                    break;
            }
        }
    }

    //页面转换的listner
    private ViewPager.OnPageChangeListener paggeListener = new ViewPager.OnPageChangeListener() {

        //配合Adapter的currentItem字段进行设置。
        @Override
        public void onPageSelected(int arg0) {


            handler.sendMessage(Message.obtain(handler, MainFragment.ImageHandler.MSG_PAGE_CHANGED, arg0, 0));
            // 遍历数组让当前选中图片下的小圆点设置颜色
            // arg0 = arg0%imagePointViews.length;
            for (int i = 0; i < imagePointViews.length; i++) {

                imagePointViews[arg0 % imagePointViews.length].setBackgroundResource(R.mipmap.page_indicator_focused);


                if (arg0 % imagePointViews.length != i) {

                    imagePointViews[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);

                }

            }

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        //覆写该方法实现轮播效果的暂停和恢复
        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    handler.sendEmptyMessage(MainFragment.ImageHandler.MSG_KEEP_SILENT);
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    handler.sendEmptyMessageDelayed(MainFragment.ImageHandler.MSG_UPDATE_IMAGE, MainFragment.ImageHandler.MSG_DELAY);
                    break;
                default:
                    break;
            }
        }
    };

}
