package cn.com.argorse.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


/**
 * Description:	  实现数据本地存储
 * @author        roc.hu
 * @version       1.0
 *
 * Modification History:
 * DateTime			-*#*-[Author]-*-[Version]	###	Description
 * ------------------------------------------------------------------
 * 2014-5-08 上午10:25:14	-*#*-[roc.hu]-*-[1.0]		###	des
 */
public class SharedPreUtils {



	private final String SHAREDPRE_FILE_NAME = "System_Configue"; // 配置文件名
	private Editor mSharedEditor; // 存储器编辑器
	private SharedPreferences mSharedPre; // 文件存储器

	//需要保存的
	public static final String PUBLIC_KEY_VERSION = "public_key_version";//服务器公钥版本号
	public static final String USER_TOKEN ="user_token" ;
	public static final String SESSION_ID = "session_id";
	public static final String PUBLIC_KEY = "public_key";//公钥
	/**
	 * 单例对象实例
	 */
	private static SharedPreUtils instance = null;

	public static SharedPreUtils getInstance(Context mContext) {
		if (instance == null) {
			synchronized (SharedPreUtils.class) {
				if (instance == null) {
					instance = new SharedPreUtils(mContext);
				}
			}
		}
		return instance;
	}
	/**
	 * 构造函数
	 * @param mContext：上下文环境
	 */
	public SharedPreUtils(Context mContext) {
		mSharedPre = mContext.getSharedPreferences(SHAREDPRE_FILE_NAME, Context.MODE_PRIVATE);
		mSharedEditor = mSharedPre.edit();
	}
	/**
	 * 清除本地保存的所有数据
	 */
	public void clear(){
		mSharedEditor.clear();
		mSharedEditor.commit();
	}

	/**
	 * 保存数据到本地
	 * @param key 键
	 * @param value 值 Object:目前只支持：String/Boolean/Float/Integer/Long
	 */
	public void saveKeyObjValue(String key, Object value){
		if(value instanceof String){
			mSharedEditor.putString(key, (String)value);
		}else if(value instanceof Boolean){
			mSharedEditor.putBoolean(key, (Boolean) value);
		}else if(value instanceof Float){
			mSharedEditor.putFloat(key, (Float) value);
		}else if(value instanceof Integer){
			mSharedEditor.putInt(key, (Integer) value);
		}else if(value instanceof Long){
			mSharedEditor.putLong(key, (Long) value);
		}
		mSharedEditor.commit();
	}
	
	/**
	 * 获取保存在本地的数据 未加密的
	 * @param key
	 * @param defValue
	 * @return String
	 */
	public String getString(String key, String defValue){
		return mSharedPre.getString(key, defValue);
	}
	/**
	 * 获取保存在本地的数据 未加密的
	 * @param key
	 * @param defValue
	 * @return Boolean
	 */
	public boolean getBoolean(String key, boolean defValue){
		return mSharedPre.getBoolean(key, defValue);
	}
	/**
	 * 获取保存在本地的数据 未加密的
	 * @param key
	 * @param defValue
	 * @return Float
	 */
	public float getFloat(String key, float defValue){
		return mSharedPre.getFloat(key, defValue);
	}/**
	 * 获取保存在本地的数据 未加密的
	 * @param key
	 * @param defValue
	 * @return Integer
	 */
	public int getInteger(String key, int defValue){
		return mSharedPre.getInt(key, defValue);
	}
	/**
	 * 获取保存在本地的数据 未加密的
	 * @param key
	 * @param defValue
	 * @return Long
	 */
	public long getLong(String key, long defValue){
		return mSharedPre.getLong(key, defValue);
	}
}
