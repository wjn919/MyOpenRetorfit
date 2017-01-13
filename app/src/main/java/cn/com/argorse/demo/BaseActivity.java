package cn.com.argorse.demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.transition.Explode;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import cn.com.argorse.demo.activity.SearchActivity;
import io.realm.Realm;


public abstract class BaseActivity extends FragmentActivity implements OnClickListener {
    private final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 1111;//权限请求
    /**
     * setResult 退出登录
     **/
    protected static final int RESULT_EXIT_LOGIN = 3;
    /**
     * setResult 退出系统APP
     **/
    protected static final int RESULT_EXIT_APP = 2;
    protected static final int REQUEST_ACTIVITY = 1;//请求下一个activity
    protected BaseApplication mApplication;
    protected Activity mActivity;
    // ====与网络交互失败;没有数据 显示的View================
    private LinearLayout mPromptLl;//提示信息__rootView
    private LinearLayout mLoadingLl;//Loading框 rootView
    // ====================End=============================

    // #start 页面顶部
    protected RelativeLayout mHeaderLl;
    protected TextView mHeaderTv;// 标题信息
    protected LinearLayout pageleftll;//返回按钮
    /**
     * 屏幕的宽度、高度、密度
     */
    protected int mScreenWidth;
    protected int mScreenHeight;
    protected float mDensity;
    protected LayoutInflater mInflater;
    protected LinearLayout mHeaderSearchLl;
    protected ImageView mHeaderSearchIv;
    protected EditText mHeaderSearchEt;
    protected TextView mHeaderSearchTv;
    protected Realm realm;
    protected ImageView mHeaderShareIv;


    // #end
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_base);
        if (Build.VERSION.SDK_INT >= 21) {//透明导航栏
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        realm = BaseApplication.getRealm();
        mApplication = (BaseApplication) getApplication();
        mActivity = this;
        mApplication.addActivity(mActivity);
        mInflater = getLayoutInflater();
        //android:windowSoftInputMode=" stateAlwaysHidden | adjustPan "
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//软键盘弹出设置
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }*/
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏显示

        getIntentBundle();//上一个Activity传递过来的Bundle

        //找到资源文件的XML
        if (getLayoutId() != 0) {
            View vContent = LayoutInflater.from(mActivity).inflate(getLayoutId(), null);
            ((FrameLayout) findViewById(R.id.frame_content)).addView(vContent);
        }
        //加载头部内容
        mHeaderLl = (RelativeLayout) findViewById(R.id.main_title_rl);
        mHeaderSearchLl = (LinearLayout)findViewById(R.id.main_search_ll);
        mHeaderSearchIv = (ImageView)findViewById(R.id.main_search_iv);
        mHeaderSearchEt  = (EditText)findViewById(R.id.main_search_et);
        mHeaderSearchTv  = (TextView)findViewById(R.id.main_search_tv);
        mHeaderShareIv = (ImageView)findViewById(R.id.iv_main_share);
        mHeaderLl.setVisibility(View.VISIBLE);//标题布局
        mHeaderTv = (TextView) findViewById(R.id.main_title_tv);// 标题信息
        pageleftll = (LinearLayout) findViewById(R.id.page_left_ll);//返回按钮
        pageleftll.setVisibility(View.VISIBLE);
        pageleftll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(v.getWindowToken(), 0);
                onBackPressed();
            }
        });



        /**
         * 获取屏幕宽度、高度、密度
         */
        DisplayMetrics metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        mDensity = metric.density;

        initViews();// 加载正文内容 默认显示头部
        initEvents();
        initData();//加载页面内容
    }
    protected View inflateView(int resId) {
        return mInflater.inflate(resId, null);
    }



    public void hideTitle() {
        mHeaderLl.setVisibility(View.GONE);
    }
    public void showTitle() {
        mHeaderLl.setVisibility(View.VISIBLE);
    }

    //显示search
    public void showHeaderSearch() {
        mHeaderSearchLl.setVisibility(View.VISIBLE);
        mHeaderSearchIv.setVisibility(View.VISIBLE);
        mHeaderTv.setVisibility(View.GONE);
    }

    public void showHeaderTv() {
        mHeaderSearchLl.setVisibility(View.GONE);
        mHeaderSearchIv.setVisibility(View.GONE);
        mHeaderTv.setVisibility(View.VISIBLE);
    }

    public LinearLayout getHeaderView() {

        return mHeaderSearchLl;
    }

    public void setHeaderTv(String  text){

        mHeaderTv.setText(text);

    }

    //权限监听
    protected interface PermissionResultListener {
        void onResponseResult(boolean grantResult);
    }

    private PermissionResultListener permissionResultListener;
    //check权限的方法，6.0之后可以用的上，目前这里没有check
    protected void checkPermission(String permission, PermissionResultListener permissionResultListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int selfPermission = checkSelfPermission(permission);

            List<String> permissions = new ArrayList<String>();
            if (selfPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
            }

            this.permissionResultListener = permissionResultListener;
        }
    }

    //权限成功后的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_SOME_FEATURES_PERMISSIONS: {
                for (int i = 0; i < permissions.length; i++) {
                    if (null != permissionResultListener)
                        permissionResultListener.onResponseResult(grantResults[i] == PackageManager.PERMISSION_GRANTED ? true : false);
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permissions --> " + "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        System.out.println("Permissions --> " + "Permission Denied: " + permissions[i]);
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    /**
     * 初始化Activity的常用变量 举例:
     * <b>mLayoutResID=页面XML资源ID 必须存在的</b>
     */
    protected abstract int getLayoutId();

    /**
     * 上一个Activity传递过来的Bundle
     */
    protected void getIntentBundle() {
    }

    /**
     * 加载数据中
     */
    @SuppressLint("InflateParams")
    public void showLoadingView() {
        if (mLoadingLl == null) {
            mLoadingLl = (LinearLayout) LayoutInflater.from(mActivity).inflate(R.layout.view_loading, null);
            ((FrameLayout) findViewById(R.id.frame_main)).addView(mLoadingLl);//主布局LinearLayout
        }
        if (mPromptLl != null) {
            mPromptLl.setVisibility(View.GONE);
        }
        mLoadingLl.setVisibility(View.VISIBLE);
        mLoadingLl.setOnClickListener(null);
    }

    /**
     * 数据加载完成
     */
    public void dismissLoadingView() {
        if (mLoadingLl != null) {
            mLoadingLl.setVisibility(View.GONE);
        }
    }

    /**
     * 加载失败展示的View
     * (必须有FrameLayout布局并且android:id="@+id/frame_main")
     * <p/>
     * 如果  promptRefreshListener !=null 则带刷新按钮
     * <p/>
     * 如果  promptRefreshListener ==null 提示信息
     *
     * @param promptTip             值：字符串 或者 R.string.xx
     * @param promptRefreshListener
     */
    @SuppressLint("InflateParams")
    public void showPromptView(Object promptTip, OnClickListener promptRefreshListener) {
        if (mPromptLl == null) {
            mPromptLl = (LinearLayout) LayoutInflater.from(mActivity).inflate(R.layout.view_prompt, null);
//			((FrameLayout)findViewById(R.id.frame_main)).addView(mPromptLl);//主布局LinearLayout
        }
        mPromptLl.setVisibility(View.VISIBLE);
        mPromptLl.setOnClickListener(null);

        (mPromptLl.findViewById(R.id.prompt_iv_icon)).setVisibility(View.VISIBLE);// 提示信息图片
        (mPromptLl.findViewById(R.id.prompt_tv_tips)).setVisibility(View.VISIBLE);// 提示信息__内容
        (mPromptLl.findViewById(R.id.prompt_btn_refresh)).setVisibility(View.VISIBLE);// 提示信息__刷新按钮

        TextView mPromptTvTip = (TextView) mPromptLl.findViewById(R.id.prompt_tv_tips);// 提示信息__内容
        // 提示信息__刷新按钮
        if (promptRefreshListener == null) {
            if (promptTip != null && (promptTip instanceof String)) {
                mPromptTvTip.setText(promptTip.toString());
            } else if (promptTip != null && (promptTip instanceof Integer)) {
                mPromptTvTip.setText(Integer.parseInt(promptTip.toString()));
            } else {
                mPromptTvTip.setText(R.string.view_prompt_pictxt_text);
            }
            (mPromptLl.findViewById(R.id.prompt_btn_refresh)).setVisibility(View.GONE);
        } else {
            mPromptTvTip.setText(R.string.view_prompt_clickretry_text);
            (mPromptLl.findViewById(R.id.prompt_btn_refresh)).setVisibility(View.VISIBLE);
            (mPromptLl.findViewById(R.id.prompt_btn_refresh)).setOnClickListener(promptRefreshListener);
        }
    }

    /**
     * 初始化视图
     **/
    protected abstract void initViews();

    /**
     * 初始化事件
     **/
    protected abstract void initEvents();

    /**
     * 初始化内容
     **/
    protected abstract void initData();

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_EXIT_APP://退出APP
                setResult(resultCode, data);
                finish();
                break;
            case RESULT_EXIT_LOGIN://退出登录
                setResult(resultCode);
                finish();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * 通过Class跳转界面
     *
     * @param cls
     */
    protected void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 通过Class跳转界面
     *
     * @param cls
     * @param bundle
     */
    protected void startActivity(Class<?> cls, Bundle bundle) {
        startActivity(cls, bundle, REQUEST_ACTIVITY);
    }

    /**
     * 通过Class跳转界面
     *
     * @param cls
     * @param bundle
     * @param requestCode
     */
    protected void startActivity(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(mActivity, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 通过Action跳转界面
     *
     * @param action
     */
    protected void startActivity(String action) {
        startActivity(action, null);
    }

    /**
     * 通过Action跳转界面
     *
     * @param action
     * @param bundle
     */
    protected void startActivity(String action, Bundle bundle) {
        startActivity(action, bundle, REQUEST_ACTIVITY);
    }

    /**
     * 通过Action跳转界面
     *
     * @param action
     * @param bundle
     * @param requestCode
     */
    protected void startActivity(String action, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onBackPressed() {
        mApplication.removeActivity(mActivity);
        super.onBackPressed();
    }


}
