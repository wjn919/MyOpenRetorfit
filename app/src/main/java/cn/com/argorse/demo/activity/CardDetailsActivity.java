package cn.com.argorse.demo.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.argorse.common.http.HttpUtils;
import cn.com.argorse.common.utils.AlertUtils;
import cn.com.argorse.common.utils.CommonListAdapter;
import cn.com.argorse.common.utils.FormatStr;
import cn.com.argorse.common.utils.ImageLoaderUtil;
import cn.com.argorse.common.utils.ListViewHolder;
import cn.com.argorse.common.view.VerticalSwipeRefreshLayout;
import cn.com.argorse.demo.BaseActivity;
import cn.com.argorse.demo.BaseApplication;
import cn.com.argorse.demo.Constants;
import cn.com.argorse.demo.Entity.BaseObserver;
import cn.com.argorse.demo.Entity.ResultsEntity;
import cn.com.argorse.demo.Entity.ShareGridViewData;
import cn.com.argorse.demo.R;
import cn.com.argorse.demo.adapter.MyShareGridAdapter;
import cn.com.argorse.demo.adapter.MyTypeListAdapter;
import cn.com.argorse.demo.api.testApi;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wjn on 2017/1/19.
 * 帖子详情
 */
public class CardDetailsActivity extends BaseActivity{
    private VerticalSwipeRefreshLayout swipeContainer;//下拉刷新
    private ListView mListLv;//ListView
    private ArrayList<ResultsEntity> mDataList;
    /**
     * 刷新 数据加载完毕
     **/
    private final int LOAD_REFRESH = 0;
    private int rowsDefault = 2;
    int beginRow = 0, rows = rowsDefault;

    private int loadType;
    private View mLvHeaderView;
    private MyTypeListAdapter mAdapter;
    private EditText mComment;
    private TextView btnConfirm;
    private int subnum;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_card_details;
    }

    @Override
    protected void initViews() {
        //下拉刷新
        swipeContainer = (VerticalSwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new LvSLOnRefreshListener());
        swipeContainer.setColorSchemeResources(R.color.swipeLayout_scheme_1, R.color.swipeLayout_scheme_2, R.color.swipeLayout_scheme_3, R.color.swipeLayout_scheme_4);

        mListLv = (ListView) findViewById(R.id.lv_list_card_details);
        mListLv.addHeaderView(initHeaderView());
        mListLv.addFooterView(initFooterView());

        mComment = (EditText)findViewById(R.id.et_card_details_comment);
        btnConfirm = (TextView)findViewById(R.id.tv_card_details_confirm);


    }

    /**
     * 更多样式
     *
     * @return
     */
    private View initFooterView() {

        View mLvFooterMoreV = View.inflate(mActivity, R.layout.inc_footer, null);
        return mLvFooterMoreV;
    }


    @Override
    protected void initEvents() {
        btnConfirm.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        mDataList = new ArrayList<>();
        mAdapter = new MyTypeListAdapter(mActivity, mDataList);
        mListLv.setAdapter(mAdapter);

        loadData(LOAD_REFRESH);
        mHeaderTv.setText(R.string.card_details);


    }





    /**
     * 获取交易记录列表
     */
    private void loadData(int mLoadType) {
        loadType = mLoadType;
        if (loadType == LOAD_REFRESH) {
            beginRow = 0;// 开始条数
            rows = rowsDefault;// 加载条数
        }
        getData1();

    }

       private void getData1() {
        testApi msgApi = HttpUtils.getInstance(BaseApplication.Server_Url).create(testApi.class);
        msgApi.getMessage(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<ResultsEntity>>(this) {

                    @Override
                    public void onStart() {
                        if (loadType == LOAD_REFRESH) {// 刷新
                            if (mDataList.size() < 1) {
                                showLoadingView();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
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
                        subnum = mDataList.size()-2;
                        mAdapter.setSubnum(subnum);
                        if (loadType == LOAD_REFRESH)
                            mListLv.setSelection(0);// 切换收到送出后滑动到顶部


                        if (mDataList.size() < 1)
                            showPromptView(getResources().getString(R.string.no_message), null);
                        mAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCompleted() {
                        if (loadType == LOAD_REFRESH) {// 刷新
                            swipeContainer.setRefreshing(false);
                            // 更新完后调用该方法结束刷新
                            swipeContainer.setEnabled(true);
                            dismissLoadingView();
                        }


                    }
                });
    }



    @Override
    public void onClick(View view) {

        if(view == btnConfirm){

            String comment = mComment.getText().toString().trim();
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
            mComment.setText("");
            mComment.clearFocus();
            if(!TextUtils.isEmpty(comment)){
               if(mDataList.size()>2){//到了评论，加载了数据的话
                   ResultsEntity entity = new ResultsEntity();
                   entity.setCreatedAt(FormatStr.getTime(new Date()));
                   entity.setUrl("http://7xi8d6.com1.z0.glb.clouddn.com/16124351_1863111260639981_4361246625721483264_n.jpg");
                   entity.setDesc(comment);
                   entity.setWho("www");
                   mDataList.add(2,entity);
                   subnum++;
                   mAdapter.setSubnum(subnum);
                   mAdapter.notifyDataSetChanged();
               }


            }
        }
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
    private View initHeaderView() {
        if (mLvHeaderView == null)
            mLvHeaderView = View.inflate(mActivity, R.layout.card_details_header_view, null);
        return mLvHeaderView;
    }

}
