package cn.com.argorse.demo.api;
import java.util.List;

import cn.com.argorse.demo.Entity.BaseEntity;
import cn.com.argorse.demo.Entity.ResultsEntity;
import cn.com.argorse.demo.Entity.WXTokenEntity;
import cn.com.argorse.demo.Entity.WXUserInfoEntity;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by wjn on 2016/11/3.
 */

public interface weixinloginApi {
    @GET("oauth2/access_token")
    Observable<WXTokenEntity> accessToken(@Query("appid") String appid,
                                          @Query("secret") String secret,
                                          @Query("code") String code,
                                          @Query("grant_type") String grant_type
                                          );


    @GET("userinfo")
    Observable<WXUserInfoEntity>  userInfo(@Query("access_token") String access_token,
                                           @Query("openid") String openid
                  );

}
