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
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.sina.weibo.sdk.api.share.IWeiboHandler;
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
import java.util.List;

import cn.com.argorse.common.http.HttpUtils;
import cn.com.argorse.common.utils.AlertUtils;
import cn.com.argorse.common.utils.CommonListAdapter;
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
import cn.com.argorse.demo.api.testApi;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by wjn on 2016/12/21.
 * 特惠详情
 */
public class ShoppingDetailsActivity extends BaseActivity implements IWeiboHandler.Response /*implements IWeiboHandler.Response, IWXAPIEventHandler*/ {

    private static final String SHARE_TEXT = "分享文字";
    private static final String SHARE_MESG = "测试分享的文字 http://www.argorse.com";
    private static final String SHARE_MESG_URL = "http://www.argorse.com";
    private VerticalSwipeRefreshLayout swipeContainer;//下拉刷新
    private ListView mListLv;//ListView
    /**
     * 刷新 数据加载完毕
     **/
    private final int LOAD_REFRESH = 0;
    private int rowsDefault = 2;
    int beginRow = 0, rows = rowsDefault;
    private ArrayList<ResultsEntity> mDataList;
    private ShoppingDetailsListAdapter mAdapter;
    private int loadType;
    private View mLvHeaderView;
    private String imageUrl;
    private GridView gridView;
    private ShareGridViewData mData;
    private MyShareGridAdapter<String> mArrayAdapter;
    private ImageView iv;
    private View rootView;
    private PopupWindow window;
    private IWeiboShareAPI mWeiboShareAPI;
    private Oauth2AccessToken mAccessToken;
    private SsoHandler mSsoHandler;
    private AuthInfo mAuthInfo;
    private ClipboardManager mClipboard;
    private IWXAPI api;
    private Tencent mTencent;
    private IUiListener myListener;
    private TextView mLeftDay;


    @Override
    protected void getIntentBundle() {

        imageUrl = getIntent().getStringExtra("imageUrl");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shopping_details;
    }

    @Override
    protected void initViews() {
        //下拉刷新
        swipeContainer = (VerticalSwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new LvSLOnRefreshListener());
        swipeContainer.setColorSchemeResources(R.color.swipeLayout_scheme_1, R.color.swipeLayout_scheme_2, R.color.swipeLayout_scheme_3, R.color.swipeLayout_scheme_4);

        mListLv = (ListView) findViewById(R.id.lv_list_shopping_details);

        mListLv.addHeaderView(initHeaderView());

        mListLv.addFooterView(initFooterView());

        //微博注册
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY_WEIBO);
        mWeiboShareAPI.registerApp(); // 将应用注册到微博客户端

        //微信注册
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID_WX, true);
        api.registerApp(Constants.APP_ID_WX);

        //qq注册
        mTencent = Tencent.createInstance(Constants.APP_ID_QQ, getApplicationContext());
        mListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertUtils.showToast(mActivity, i + "");
                if (i > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString("imageUrl", imageUrl);
                    startActivity(ShoppingDetailsActivity.class, bundle, REQUEST_ACTIVITY);

                }
            }
        });

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

    }

    @Override
    protected void initData() {

        mDataList = new ArrayList<>();
        mAdapter = new ShoppingDetailsListAdapter(mActivity, mDataList, R.layout.adapter_search_item);
        mListLv.setAdapter(mAdapter);
        initHeaderTab();
        loadData(LOAD_REFRESH);
        mHeaderTv.setText(R.string.message_details);
        mHeaderShareIv.setVisibility(View.VISIBLE);
        mHeaderShareIv.setOnClickListener(this);
        handler.postDelayed(runnable, 1000);


    }


    private void initHeaderTab() {

        iv = (ImageView) mLvHeaderView.findViewById(R.id.iv_shopping_details_image);
        ImageLoaderUtil.load(this, imageUrl, iv);
        TextView shopName = (TextView) mLvHeaderView.findViewById(R.id.tv_shopping_details_name);

        ImageSpan span = new ImageSpan(this, R.mipmap.ic_goods_tmall);
        SpannableString spanStr = new SpannableString("   ");
        spanStr.setSpan(span, 0, spanStr.length() - 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        shopName.setText(spanStr);
        shopName.append("润虎 台湾高山茶冻顶乌龙茶台湾茶叶 乌龙茶150克");

        mLeftDay = (TextView)mLvHeaderView.findViewById(R.id.tv_left_day);
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
                        mDataList.add(response.get(0));
                        mDataList.add(response.get(1));
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
        if (view == mHeaderShareIv) {
            showPopupWindow();
        } else if (view == rootView) {
            window.dismiss();
        }

    }
    Handler handler = new Handler();
    private long time = 100000;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time--;
            String formatLongToTimeStr = formatLongToTimeStr(time);
            mLeftDay.setText(formatLongToTimeStr);
           /* String[] split = formatLongToTimeStr.split("：");
            for (int i = 0; i < split.length; i++) {
                if(i==0){
                    tvtime1.setText(split[0]+"小时");
                }
                if(i==1){
                    tvtime2.setText(split[1]+"分钟");
                }
                if(i==2){
                    tvtime3.setText(split[2]+"秒");
                }

            }*/
            if(time>0){
                handler.postDelayed(this, 1000);
            }
        }
    };

    public  String formatLongToTimeStr(Long l) {
        int day = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;
        second = l.intValue() ;
        if (second > 60) {
            minute = second / 60;         //取整
            second = second % 60;//取余

        }

        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;

        }
        if(hour>24){
            day = hour / 24;
            hour = hour%24;
        }
        String strtime = "剩下"+day+"天"+hour+":"+minute+":"+second;
        return strtime;

    }
    private void showPopupWindow() {
        final View contentView = LayoutInflater.from(this).inflate(R.layout.share_pop_layout, null);
        gridView = (GridView) contentView.findViewById(R.id.gv_share);
        rootView = contentView.findViewById(R.id.pop_root_ll);
        rootView.setOnClickListener(this);
        initHeaderGridData();
        window = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.showAtLocation(iv, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);


    }

    //头布局的grid布局
    private void initHeaderGridData() {
        //初始化ArrayAdapter所需要的数据
        mData = new ShareGridViewData();
        mArrayAdapter = new MyShareGridAdapter<>(this, R.layout.adapter_main_grid_item_view, mData.getDisplaymainmessage(), mData);

        gridView.setAdapter(mArrayAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                //取出每个view带的参数值，然后做你想做的操作。这里只是弹一个toast。
                String value = (String) (view.getTag());
                String message = mData.getDisplaymainmessage(Integer.parseInt(value));
                share(value);


            }
        });


    }

    private void share(String value) {
        if (value.equals("0")) {
            AlertUtils.showToast(this, "QQ");
            window.dismiss();
            //shareToQQ();
            qq(false);
        } else if (value.equals("1")) {
            AlertUtils.showToast(this, "QQ空间");
            window.dismiss();
            qq(true);
            // shareToQZone();
        } else if (value.equals("2")) {
            AlertUtils.showToast(this, "微信");
            window.dismiss();
            weixinShare(0);
        } else if (value.equals("3")) {
            AlertUtils.showToast(this, "微信朋友圈");
            window.dismiss();
            weixinShare(1);
        } else if (value.equals("4")) {
            AlertUtils.showToast(this, "新浪");
            window.dismiss();
            weiboAuth();


        } else if (value.equals("5")) {
            AlertUtils.showToast(this, "复制信息");
            window.dismiss();
            copy(SHARE_MESG);

        } else if (value.equals("6")) {
            AlertUtils.showToast(this, "复制链接");
            window.dismiss();
            copy(SHARE_MESG_URL);
        }
    }

    private void qq(boolean flag) {
        if (mTencent.isSessionValid() && mTencent.getOpenId() == null) {
            AlertUtils.showToast(mActivity, "您还未安装QQ");
        }

        myListener = new MyIUiListener();
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "要分享的标题");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "要分享的摘要");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://www.qq.com/news/1.html");// 内容地址
        // params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "应用名称");// 应用名称
        params.putString(QQShare.SHARE_TO_QQ_EXT_INT, "其它附加功能");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "测试应用222222");
        if (flag) {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        }

        mTencent.shareToQQ(this, params, myListener);
    }

    private void weixinShare(int shareType) {
        if (!api.isWXAppInstalled()) {
            AlertUtils.showToast(this, "您还未安装微信");
            return;
        }
        BaseApplication.isWxLogin = false;
        BaseApplication.isWxShare = true;
        WXWebpageObject webpage = new WXWebpageObject();
        //分享网页类型
        webpage.webpageUrl = "http://www.argorse.com/";
        //创建一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "测试";
        msg.description = "这是我做的一款电商型app，高端大气上档次，快来看看吧！";

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());//transaction字段用于唯一标识一个请求，这个必须有，否则会出错
        req.message = msg;

        //表示发送给朋友圈  WXSceneTimeline  表示发送给朋友  WXSceneSession
        req.scene = shareType == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;

        api.sendReq(req);
    }


    private void copy(String shareMesg) {
        // Gets a handle to the clipboard service.
        if (null == mClipboard) {
            mClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        }

        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("simple text",
                shareMesg);

        // Set the clipboard's primary clip.
        mClipboard.setPrimaryClip(clip);


    }


    private void weiboAuth() {
        mAuthInfo = new AuthInfo(this, Constants.APP_KEY_WEIBO,
                Constants.REDIRECT_URL_WEIBO, Constants.SCOPE);
        mSsoHandler = new SsoHandler(this, mAuthInfo);
        mSsoHandler.authorize(new AuthListener());
    }

   /* //微信
    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        String result = "";

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result =getResources().getString(R.string.errcode_success) ;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = getResources().getString(R.string.errcode_cancel);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = getResources().getString(R.string.errcode_deny);
                break;
            default:
                result = getResources().getString(R.string.errcode_unknown);
                break;
        }

        AlertUtils.showToast(this,result);
    }*/


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
            mLvHeaderView = View.inflate(mActivity, R.layout.shopping_details_header_view, null);
        return mLvHeaderView;
    }

    /**
     * 账单列表适配器
     */
    private class ShoppingDetailsListAdapter extends CommonListAdapter<ResultsEntity> {

        public ShoppingDetailsListAdapter(Context context, List<ResultsEntity> datas, int layoutId) {
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
     * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
     * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
     * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
     * SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(mActivity,
                        mAccessToken);
                startSinaShare();
            } else {
                // 当您注册的应用程序签名不正确时，就会收到错误Code，请确保签名正确
                String code = values.getString("code", "");
                AlertUtils.showToast(mActivity, code + " 签名不正确");
            }
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(mActivity,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * 新浪微博用户授权
     */
    public void startSinaShare() {
        mSsoHandler = new SsoHandler(this, mAuthInfo);
        // 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 如果Token有效，则直接调用发送微博
        if (mAccessToken.isSessionValid()) {
            sendMessage();
        } else {
            mSsoHandler.authorize(new AuthListener());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this); //当前应用唤起微博分享后，返回当前应用
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_ACTIVITY) {
            // SSO 授权回调
            // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
            if (mSsoHandler != null) {
                mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
                mSsoHandler = null;
            }

            if (myListener != null) {
                Tencent.onActivityResultData(requestCode, resultCode, data, myListener);
                myListener = null;
            }


        }


    }

    /**
     * 接收微客户端博请求的数据。 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     *
     * @param baseResp 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                Toast.makeText(this, R.string.share_success, Toast.LENGTH_LONG)
                        .show();
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Toast.makeText(this, R.string.share_cancel, Toast.LENGTH_LONG)
                        .show();
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Toast.makeText(this, R.string.errcode_deny, Toast.LENGTH_LONG)
                        .show();
                break;
        }

    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     */
    private void sendMessage() {
        if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
            sendMultiMessage(true, true, true, false, false, false);
        } else {
            Toast.makeText(this, R.string.sina_share_hint, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。 注意：当
     * {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     *
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     */

    private void sendMultiMessage(boolean hasText, boolean hasImage, boolean hasWebpage,
                                  boolean hasMusic, boolean hasVideo, boolean hasVoice) {
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (hasText) {
            weiboMessage.textObject = getTextObj();
        }
        if (hasWebpage) {
            // 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
            weiboMessage.mediaObject = getWebpageObj();
        }

        if (hasImage) {
            weiboMessage.imageObject = getImageObj();
        }
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(this, request);


    }

    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();

        Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                R.mipmap.baicai);
        imageObject.setImageObject(bmp);
        return imageObject;
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = getResources().getString(R.string.share_content);
        return textObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.actionUrl = getString(R.string.share_url);
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = getResources().getString(R.string.share_title);
        mediaObject.description = getString(R.string.share_content);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
        mediaObject.setThumbImage(bmp);
        return mediaObject;
    }


    class MyIUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            AlertUtils.showToast(mActivity, "QQ分享成功");
            // 操作成功
        }

        @Override
        public void onError(UiError uiError) {
            AlertUtils.showToast(mActivity, "QQ分享异常");

            // 分享异常
        }

        @Override
        public void onCancel() {
            AlertUtils.showToast(mActivity, "取消QQ分享");

            // 取消分享
        }
    }

}
