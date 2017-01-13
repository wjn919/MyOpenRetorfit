package cn.com.argorse.demo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;


import java.util.LinkedList;
import java.util.List;

import cn.com.argorse.common.utils.SharedPreUtils;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class BaseApplication extends Application {

	private static Realm realm;
	//运用list来保存们每一个activity是关键
    private List<Activity> mActivities = new LinkedList<Activity>();
	//*******环境切换***********************************
	/**服务器地址**/
	public static String Server_Url;
	/**图片地址**/
	public static String Image_Url;

	/**加密公钥**/
	public static String PublickKey;
	/**加密公钥 版本号**/
	public static String PubVersion;
	
	private static BaseApplication instance=null;
	private String sessionId;//sessionid


	//实例化一次
    public static BaseApplication getInstance(){
    	if(instance==null){
    		synchronized (BaseApplication.class) {
    			if (null == instance) {   
    	            instance = new BaseApplication();
    	        }
			}
    	}
        return instance;   
    }

	//实例化一次获取数据库
	public static Realm getRealm(){
		return realm;
	}
	
	//====================系统环境的设置==========================
	public void onCreate() {
		super.onCreate();
		instance = this;
		//创建数据库
		RealmConfiguration config = new RealmConfiguration.Builder(this).name("myrealm.realm").encryptionKey(new byte[64]).schemaVersion(1).build();
		realm = Realm.getInstance(config);
		Server_Url= Constants.SERVER_DEV_URL;//服务器地址
		Image_Url = Constants.SERVER_DEV_URL;

	}



	// add Activity    
    public void addActivity(Activity activity) {
        mActivities.add(activity);   
    }
	// removes Activity    
    public void removeActivity(Activity activity) {
        mActivities.remove(activity);   
    }  
    //关闭每一个list内的activity  
    public void closeAllActivity() {
		try {
			for (Activity activity : mActivities) {
				if (activity != null)
					activity.finish();
			}
			mActivities.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//设置sessionId
	public void setSessionId(String sessionId) {
		SharedPreUtils.getInstance(this).saveKeyObjValue(SharedPreUtils.SESSION_ID,sessionId);
		this.sessionId = sessionId;
	}
	//获取sessionId
	public String getSessionId() {
		if(TextUtils.isEmpty(sessionId))
			sessionId= SharedPreUtils.getInstance(this).getString(SharedPreUtils.SESSION_ID,"");
		return sessionId;

	}

	public static Context context() {
		return getInstance();
	}
}
