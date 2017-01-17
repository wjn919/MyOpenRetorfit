package cn.com.argorse.demo.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import cn.com.argorse.common.exception.ApiErrorHelper;
import cn.com.argorse.common.exception.ApiException;
import cn.com.argorse.common.http.ClientFactory;
import cn.com.argorse.common.http.HttpUtils;
import cn.com.argorse.common.http.JsonConverterFactory;
import cn.com.argorse.common.utils.AlertUtils;
import cn.com.argorse.demo.BaseApplication;
import cn.com.argorse.demo.Constants;
import cn.com.argorse.demo.Entity.BaseEntity;
import cn.com.argorse.demo.Entity.BaseObserver;
import cn.com.argorse.demo.Entity.WXTokenEntity;
import cn.com.argorse.demo.Entity.WXUserInfoEntity;
import cn.com.argorse.demo.R;
import cn.com.argorse.demo.api.weixinloginApi;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by wjn on 2017/1/11.
 */

public class WXEntryActivity  extends AppCompatActivity implements IWXAPIEventHandler {
    private final String APP_ID= Constants.APP_ID_WX;
    private IWXAPI api;


    private Bundle bundle;
    //这个实体类是我自定义的实体类，用来保存第三方的数据的实体类
    private Retrofit.Builder mRetrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, APP_ID, false);
        api.registerApp(APP_ID);
        api.handleIntent(getIntent(), this);

    }

    //微信发送消息给app，app接受并处理的回调函数
    @Override
    public void onReq(BaseReq req) {

    }

    //app发送消息给微信，微信返回的消息回调函数,根据不同的返回码来判断操作是否成功
    @Override
    public void onResp(BaseResp resp) {
        if(BaseApplication. isWxLogin){//微信登录
            bundle=getIntent().getExtras();
            SendAuth.Resp response = new SendAuth.Resp(bundle);
            //获取到code之后，需要调用接口获取到access_token
            if (response. errCode == BaseResp.ErrCode. ERR_OK) {
                String code = response.code;
                getToken(code);
            }
        }else if(BaseApplication.isWxShare) {
            int result = 0;
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    result = R.string.errcode_success;
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    result = R.string.errcode_cancel;
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    result = R.string.errcode_deny;
                    break;
                default:
                    result = R.string.errcode_unknown;
                    break;
            }

            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
            finish();
        }else {
            WXEntryActivity. this.finish();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        api.handleIntent(intent, WXEntryActivity.this);//必须调用此句话
    }


    //这个方法会取得accesstoken  和openID
    private void getToken(String code){
        AlertUtils.showLoadingDialog(WXEntryActivity.this, "登陆", "正在获取用户信息" );
       weixinloginApi api = HttpUtils.getInstance("https://api.weixin.qq.com/sns/").create(weixinloginApi.class);
        api.accessToken(Constants.APP_ID_WX,Constants.APP_SECRET_WX,code,"authorization_code")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WXTokenEntity>(){

                    @Override
                    public void onStart() {
                        AlertUtils.showLoadingDialog(WXEntryActivity.this);
                    }
                    @Override
                    public void onCompleted() {

                        AlertUtils.dismissLoadingDialog();
                    }


                    @Override
                    public void onError(Throwable e) {
                        ApiErrorHelper.handleCommonError(WXEntryActivity.this, e);

                    }

                    @Override
                    public void onNext(WXTokenEntity wxTokenEntity) {
                        getUserInfo(wxTokenEntity.getAccess_token(),wxTokenEntity.getOpenid());

                    }
                });

    }

    //获取到token和openID之后，调用此接口得到身份信息
    private void getUserInfo(String token,String openID){

        weixinloginApi api = HttpUtils.getInstance("https://api.weixin.qq.com/sns/").create(weixinloginApi.class);
        api.userInfo(token,openID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WXUserInfoEntity>(){

                    @Override
                    public void onStart() {
                        AlertUtils.showLoadingDialog(WXEntryActivity.this);
                    }
                    @Override
                    public void onCompleted() {

                        AlertUtils.dismissLoadingDialog();
                    }


                    @Override
                    public void onError(Throwable e) {
                        ApiErrorHelper.handleCommonError(WXEntryActivity.this, e);

                    }

                    @Override
                    public void onNext(WXUserInfoEntity wxUserInfoEntity) {
                      AlertUtils.showToast(WXEntryActivity.this,wxUserInfoEntity.getNickname());
                    }
                });




    }
}