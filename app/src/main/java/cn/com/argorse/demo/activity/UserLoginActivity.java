package cn.com.argorse.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
import com.tencent.connect.common.Constants;
import com.tencent.connect.UserInfo;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;
import cn.com.argorse.common.utils.AlertUtils;
import cn.com.argorse.demo.BaseActivity;
import cn.com.argorse.demo.BaseApplication;
import cn.com.argorse.demo.R;

/**
 * Created by wjn on 2016/11/7.
 */
public class UserLoginActivity extends BaseActivity {

    ImageView ivpersonshownum;
    ImageView ivpersonisShowpass;
    private TextView mWeixinLogin;
    private TextView mQQLogin;
    private TextView mSina;
    private IWXAPI mIwapi;
    private Tencent mTencent;
    private IUiListener loginListener;
    private IUiListener userInfoListener;
    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;
    private Oauth2AccessToken mAccessToken;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews() {
        ivpersonshownum = (ImageView) findViewById(R.id.iv_person_show_num);
        ivpersonisShowpass = (ImageView) findViewById(R.id.iv_person_isShow_pass);
        mWeixinLogin = (TextView) findViewById(R.id.tv_weixin_login);
        mQQLogin = (TextView) findViewById(R.id.tv_qq_login);
        mSina = (TextView) findViewById(R.id.tv_sina_login);

        //微信注册
        mIwapi = WXAPIFactory.createWXAPI( this, cn.com.argorse.demo.Constants.APP_ID_WX, true );
        mIwapi.registerApp(cn.com.argorse.demo.Constants.APP_ID_WX);

        mTencent = Tencent.createInstance(cn.com.argorse.demo.Constants.APP_ID_QQ,this);


        //微博
        mAuthInfo = new AuthInfo( this,
                cn.com.argorse.demo.Constants.APP_KEY_WEIBO, cn.com.argorse.demo.Constants.REDIRECT_URL_WEIBO,
                cn.com.argorse.demo.Constants. SCOPE);
        mSsoHandler = new SsoHandler(this, mAuthInfo);


    }

    @Override
    protected void initEvents() {
        ivpersonshownum.setOnClickListener(this);
        ivpersonisShowpass.setOnClickListener(this);
        mWeixinLogin.setOnClickListener(this);
        mQQLogin.setOnClickListener(this);
        mSina.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mHeaderTv.setText(R.string.login);
        loginListener = new IUiListener() {

            @Override
            public void onCancel() {
                AlertUtils. showWarnDialog(UserLoginActivity.this, "取消了" );

            }

            @Override
            public void onComplete(Object arg0) {
                //登陆成功的回调，在此处可以获取用户信息
                AlertUtils. showLoadingDialog(UserLoginActivity.this, "登陆", "正在获取用户信息" );
                initOpenidAndToken((JSONObject) arg0);
                updateUserInfo();
            }

            @Override
            public void onError(UiError arg0) {
                AlertUtils. showWarnDialog(UserLoginActivity.this, "错误了" );

            }

        };



        // 返回Bitmap对象。
        userInfoListener = new IUiListener() {

            @Override
            public void onError(UiError e) {
                AlertUtils.dismissLoadingDialog();
            }

            // 用户的信息回调在此处
            @Override
            public void onComplete( final Object response) {
                AlertUtils.dismissLoadingDialog();
                // 返回Bitmap对象。
                try {
                    JSONObject obj = new JSONObject(response.toString());
                    String nickName = obj.optString( "nickname");
                    String figureurl_qq_2 =obj.optString( "figureurl_qq_2");
                    String gender = obj.optString( "gender");
                    Log.e(".....",nickName+","+figureurl_qq_2+","+gender);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                AlertUtils.dismissLoadingDialog();
            }
        };
    }

    @Override
    public void onClick(View view) {
        if(view==ivpersonshownum){
            AlertUtils.showToast(mActivity,"下拉电话号");

        }else if(view == ivpersonisShowpass){
            AlertUtils.showToast(mActivity,"隐藏显示密码");
        } else if(view==mWeixinLogin){
            AlertUtils.showToast(mActivity,"微信登录");
            //现在登录不了，因为没有开通
           // weixinLogin();

        }else if(view==mQQLogin){
            AlertUtils.showToast(mActivity,"QQ登录");
            tecentLogin();

        }else if(view==mSina){
            AlertUtils.showToast(mActivity,"新浪登录");
            weiboLogin();

        }

    }
    /**
     * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
     * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
     * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
     * SharedPreferences 中。
     */
    public class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if ( mAccessToken.isSessionValid()) {
                AlertUtils. showLoadingDialog(UserLoginActivity.this, "登陆",
                        "正在获取用户信息" );
                UsersAPI mUsersAPI = new UsersAPI(UserLoginActivity.this,cn.com.argorse.demo.Constants.APP_KEY_WEIBO , mAccessToken);
                long uid = Long.parseLong(mAccessToken.getUid());
                mUsersAPI.show(uid, mListener); //获取用户基本信息
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                AlertUtils.showToast(UserLoginActivity.this,code);
            }
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onWeiboException(WeiboException e) {

        }
    }



    private void weiboLogin() {
        mSsoHandler.authorize( new AuthListener());
    }

    private void tecentLogin() {
        // QQ发起登陆
        mTencent.login( this, "all",loginListener);

    }

    //获取用户信息的回调
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            AlertUtils.dismissLoadingDialog();
            if (!TextUtils. isEmpty(response)) {
                // 调用 User#parse 将JSON串解析成User对象，所有的用户信息全部在这里面
                User user = User. parse(response);
               String name = user. name; // 昵称
                String hd = user. avatar_hd; // 头像
                String gender = user. gender.equals( "m") ? "男" : "女" ;
                Log.e(".....",name+","+hd+","+gender);

            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
           AlertUtils.dismissLoadingDialog();
            ErrorInfo info = ErrorInfo. parse(e.getMessage());
            AlertUtils. showToast(UserLoginActivity.this, info.toString());
        }
    };



    /**
     * @Title: initOpenidAndToken
     * @Description: 初始化OPENID以及TOKEN身份验证。
     * @param @param jsonObject
     * @return void
     * @throws
     */
    private void initOpenidAndToken (JSONObject jsonObject) {

        try {
            //这里的Constants类，是 com.tencent.connect.common.Constants类，下面的几个参数也是固定的
            String token = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN );
            String expires = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN );
            //OPENID,作为唯一身份标识
            String openId = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID );
            if (!TextUtils. isEmpty(token) && !TextUtils.isEmpty(expires)&& !TextUtils. isEmpty(openId)) {
                //设置身份的token
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);

            }
        } catch (Exception e) {
        }
    }

    /**
     * @Title: updateUserInfo
     * @Description: 在回调里面可以获取用户信息数据了
     * @param
     * @return void
     * @throws
     */
    private void updateUserInfo() {
        if ( mTencent != null && mTencent.isSessionValid()) {
            UserInfo mInfo = new UserInfo(UserLoginActivity. this,  mTencent.getQQToken());
            mInfo.getUserInfo(userInfoListener);
        }
    }

    private void weixinLogin() {

        BaseApplication.isWxLogin = true;
        BaseApplication.isWxShare = false;
        SendAuth.Req req = new SendAuth.Req();
        req. scope = "snsapi_userinfo";
        req. state = "wechat_sdk_demo_test";
        mIwapi.sendReq(req);//执行完毕这句话之后，会在WXEntryActivity回调
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_LOGIN) {
            if (resultCode == Constants.ACTIVITY_OK) {
                Tencent.handleResultData(data, loginListener);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
