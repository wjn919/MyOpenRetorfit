package cn.com.argorse.demo;

import java.io.File;

/**
 * 类名：Constants 类描述：用于存储系统常量
 * Created by jiaxiangdong on 2015/9/22.
 */
public final class Constants {

    /**
     * 开发环境
     **/
    public static final String SERVER_DEV_URL = "http://gank.io/api/";


    //分享
    public static final java.lang.String APP_KEY_WEIBO = "3995302498";
    public static final String REDIRECT_URL_WEIBO ="http://www.sina.com" ;
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";// 应用申请的 高级权限


    //微信
    public static final String APP_ID_WX ="wxb998e4a615cdbf81" ;
    public static final String APP_SECRET_WX ="009b47521d4b816b96f0f6b28fa8a17b";
    public static final String APP_ID_QQ ="1105944680" ;


    //缓存
    public static final String DATA_PATH = BaseApplication.context().getCacheDir().getAbsolutePath() + File.separator + "data";
    public static final String NET_DATA_PATH = DATA_PATH + File.separator + "net_cache";

    public static final String PARAM_ACCESS_TOKEN = "access_token";

    public static final String PARAM_EXPIRES_IN ="expires_in" ;

    public static final String PARAM_OPEN_ID = "openid";
}
