package cn.com.argorse.demo.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.com.argorse.common.http.HttpUtils;
import cn.com.argorse.common.utils.CommonRecyclerAdapter;
import cn.com.argorse.common.utils.AlertUtils;
import cn.com.argorse.common.utils.CommonListAdapter;
import cn.com.argorse.common.utils.ListViewHolder;
import cn.com.argorse.common.utils.RecyclerViewHolder;
import cn.com.argorse.common.view.VerticalSwipeRefreshLayout;
import cn.com.argorse.demo.BaseActivity;
import cn.com.argorse.demo.BaseApplication;
import cn.com.argorse.demo.BaseFragment;
import cn.com.argorse.demo.Entity.BaseObserver;
import cn.com.argorse.demo.Entity.ImageEntity;
import cn.com.argorse.demo.Entity.ResultsEntity;
import cn.com.argorse.demo.R;
import cn.com.argorse.demo.adapter.MainViewPagerAdapter;
import cn.com.argorse.demo.api.testApi;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;



/**
 * Created by wjn on 2016/11/8.
 */
public class TradeRecordFragment extends BaseFragment implements View.OnClickListener{


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
    private TradeListAdapter mAdapter;
    private View mLvHeaderView;
    private int loadType;
    private ViewPager mViewPager;
    private RelativeLayout ll_vp;
    private LinearLayout mPointGroup;
    private ImageView[] imagePointViews;
    private List<ResultsEntity> urlList ;
    private MainViewPagerAdapter mViewPagerAdapter;
    private TradeRecordFragment.ImageHandler handler = new TradeRecordFragment.ImageHandler(new WeakReference<TradeRecordFragment>(this));
    private RecyclerView mRecyclerView;
    private int[] images = new int[]{R.mipmap.pm_media_icon_1,R.mipmap.pm_media_icon_2,R.mipmap.pm_media_icon_3,R.mipmap.pm_media_icon_4,R.mipmap.pm_media_icon_5,R.mipmap.pm_media_icon_6};
    private ArrayList<ImageEntity>mImageList;
    private MyMainAdapter mImageAdapter;
    private boolean isFirst = true;
    int sum = 2;
    int num = 2;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_trade;
    }

    @Override
    protected void initViews() {
        //下拉刷新
        swipeContainer = (VerticalSwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new LvSLOnRefreshListener());
        swipeContainer.setColorSchemeResources(R.color.swipeLayout_scheme_1, R.color.swipeLayout_scheme_2, R.color.swipeLayout_scheme_3, R.color.swipeLayout_scheme_4);
        mListLv = (ListView) findViewById(R.id.lv_list_trade);


    }


    @Override
    protected void initEvents() {
        mListLv.setOnScrollListener(new LvOnScrollListener());
    }

    @Override
    protected void initData() {

        mDataList = new ArrayList<>();
        urlList = new ArrayList<>();
        mAdapter = new TradeListAdapter(mActivity, mDataList, R.layout.list_item_layout_hospital);
        mListLv.addFooterView(initFooterMoreView());
        mListLv.addHeaderView(initHeaderView());
        //mListLv.setAdapter(mAdapter);
        mListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertUtils.showToast(mActivity,(position-1)+"");

            }
        });

        initViewPager();
        initViewPagerData();
        initRecyclerView();


    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity)getActivity()).setHeaderTv(getResources().getString(R.string.trade_fragment));
        ((BaseActivity)getActivity()).showTitle();
        ((BaseActivity)getActivity()).showHeaderTv();
    }

    //初始化头部的recyclerview
    private void initRecyclerView() {
       mImageList = new ArrayList<>();
        for(int i = 0;i<images.length;i++){
            ImageEntity iv = new ImageEntity();
            iv.setImageId(images[i]);
            mImageList.add(iv);
        }
        mRecyclerView = (RecyclerView) mLvHeaderView.findViewById(R.id.rv_header_trade);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mActivity);
        horizontalLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(horizontalLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mImageAdapter = new MyMainAdapter(mActivity, mImageList, R.layout.item_trade_image);
        mRecyclerView.setAdapter(mImageAdapter);
        mImageAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {
                AlertUtils.showToast(mActivity,pos+"");
            }
        });



    }

    private void initViewPager() {
        mViewPager = (ViewPager) mLvHeaderView.findViewById(R.id.vp_header_trade_guidePages);
        RelativeLayout.LayoutParams vpLp = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();
        vpLp.height = mScreenWidth *(Integer.parseInt(getResources().getString(R.string.viewpager_height) ) )/(Integer.parseInt(getResources().getString(R.string.viewpager_width) ));
        mViewPager.setLayoutParams(vpLp);
        mPointGroup = (LinearLayout) mLvHeaderView.findViewById(R.id.ll_header_main_point_viewGroup);
        ll_vp = (RelativeLayout) mLvHeaderView.findViewById(R.id.ll_header_vp);
        ll_vp.setVisibility(View.GONE);

    }

    private void initViewPagerData() {
        //设置小圆点

        //小圆点的个数和图片个数一致
        imagePointViews = new ImageView[2];
        // 设置每个小圆点距离左边的间距
        LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        margin.setMargins(10, 0, 0, 10);
        for (int i = 0; i < 2; i++) {
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

    @Override
    protected void loadData() {
        initUrl();
        loadData(LOAD_REFRESH);
    }

    private void initUrl() {
        testApi viewPagerApi = HttpUtils.getInstance(BaseApplication.Server_Url).create(testApi.class);
        viewPagerApi.getMessage(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<ResultsEntity>>(getActivity()) {
                        @Override
                        public void onStart() {
                            if (urlList.size() < 1) {
                                showLoadingView();
                            }

                        }

                        @Override
                        public void onSuccess(List<ResultsEntity> response) {
                            if (loadType == LOAD_REFRESH) {
                                urlList.clear();// 刷新
                            }
                                     urlList.addAll(response);

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

                                if (loadType == LOAD_MORE) {
                                    setFooterMoreView(false);
                                }

                                if(loadType==LOAD_REFRESH){
                                    num--;
                                    if (num == 0) {
                                        updateUi();
                                        return;
                                    }
                                }



                            if (mDataList.size() < 1){
                                showPromptView(getResources().getString(R.string.no_message), null);
                            }

                            mAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onCompleted() {
                        if(loadType==LOAD_MORE){
                            updateComplete();
                        }else if(loadType==LOAD_REFRESH){
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

    private void updateComplete() {


        isLoadMoreOK = true;
        isFirst = false;
        if (loadType == LOAD_REFRESH) {// 刷新
            swipeContainer.setRefreshing(false);
            // 更新完后调用该方法结束刷新
            swipeContainer.setEnabled(true);
            dismissLoadingView();
        }
        num = 2;
        sum = 2;

    }

    private void updateUi() {
        if (isFirst) {
            mListLv.setAdapter(mAdapter);
        }
        mViewPagerAdapter = new MainViewPagerAdapter(mActivity, urlList);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnPageChangeListener(paggeListener);
        mViewPager.setCurrentItem(Integer.MAX_VALUE / 2);
        //开始轮播效果
        handler.sendEmptyMessageDelayed(TradeRecordFragment.ImageHandler.MSG_UPDATE_IMAGE_FIRST, TradeRecordFragment.ImageHandler.MSG_DELAY);
        ll_vp.setVisibility(View.VISIBLE);

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
    public void onClick(View view) {

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
            mLvHeaderView = View.inflate(mActivity, R.layout.trade_header_view, null);
        return mLvHeaderView;
    }

    /**
     * 账单列表适配器
     */
    private class TradeListAdapter extends CommonListAdapter<ResultsEntity> {

        public TradeListAdapter(Context context, List<ResultsEntity> datas, int layoutId) {
            super(context, datas, layoutId);
        }


        @Override
        public void convert(ListViewHolder holder, ResultsEntity item, int position) {
            holder.setText(R.id.common_tv, item.getWho()).setImage(R.id.common_iv, item.getUrl());


        }


    }


    //页面转换的listner
    private ViewPager.OnPageChangeListener paggeListener = new ViewPager.OnPageChangeListener() {

        //配合Adapter的currentItem字段进行设置。
        @Override
        public void onPageSelected(int arg0) {

            handler.sendMessage(Message.obtain(handler, TradeRecordFragment.ImageHandler.MSG_PAGE_CHANGED, arg0, 0));
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
                    handler.sendEmptyMessage(TradeRecordFragment.ImageHandler.MSG_KEEP_SILENT);
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    handler.sendEmptyMessageDelayed(TradeRecordFragment.ImageHandler.MSG_UPDATE_IMAGE, TradeRecordFragment.ImageHandler.MSG_DELAY);
                    break;
                default:
                    break;
            }
        }
    };

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
        private WeakReference<TradeRecordFragment> weakReference;
        private int currentItem = 0;

        protected ImageHandler(WeakReference<TradeRecordFragment> wk) {
            weakReference = wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            TradeRecordFragment activity = weakReference.get();
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


    class MyMainAdapter extends CommonRecyclerAdapter<ImageEntity> {


        public MyMainAdapter(Context ctx, List<ImageEntity> list, int LayoutId) {
            super(ctx, list, LayoutId);
        }

        @Override
        public void bindData(RecyclerViewHolder holder, final int position, ImageEntity item) {
            holder.setImageRes(R.id.iv_trade_image,item.getImageId());

        }
    }
}
