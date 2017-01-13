package cn.com.argorse.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.trello.rxlifecycle.components.support.RxFragment;

import cn.com.argorse.common.http.HttpUtils;
import cn.com.argorse.demo.BaseApplication;
import cn.com.argorse.demo.R;


public abstract class BaseFragment extends RxFragment {

	/** 跳转到下一个activity **/
	protected static final int REQUEST_ACTIVITY=1;
	protected BaseApplication mApplication;
	protected Activity mActivity;
	protected View mView;

	// ====与网络交互失败;没有数据 显示的View================
	private LinearLayout mPromptLl;//提示信息__rootView
	private LinearLayout mLoadingLl;//Loading框 rootView
	// ====================End=============================
	/**
	 * 屏幕的宽度、高度、密度
	 */
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected float mDensity;
	//控件是否已经初始化
	private boolean isCreateView = false;
	//是否已经加载过数据
	private boolean isLoadData = false;

	@Override
	public void onAttach(Activity activity) {
		mApplication=(BaseApplication)activity.getApplication();
		mActivity = activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(getLayoutId(), null);
			DisplayMetrics metric = new DisplayMetrics();
			mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
			mScreenWidth = metric.widthPixels;
			mScreenHeight = metric.heightPixels;
			mDensity = metric.density;
			initViews();
			isCreateView = true;
			initEvents();
			initData();
		}
		// 缓存的viewiew需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个view已经有parent的错误。
		ViewGroup parent = (ViewGroup) mView.getParent();
		if (parent != null) {
			parent.removeView(mView);
		}
		/**
		 * 获取屏幕宽度、高度、密度
		 */

		return mView;
	}

	protected abstract int getLayoutId();
	//此方法在控件初始化前调用，所以不能在此方法中直接操作控件会出现空指针
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && isCreateView) {
			lazyLoad();
		}
	}

	private void lazyLoad() {
		//如果没有加载过就加载，否则就不再加载了
		if(!isLoadData){
			//加载数据操作
			loadData();
			isLoadData=true;
		}
	}
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//第一个fragment会调用
		if (getUserVisibleHint())
			lazyLoad();
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	protected abstract void initViews();
	
	protected abstract void initEvents();

	protected abstract void initData();
	protected abstract void loadData();
	public View findViewById(int id) {
		return mView.findViewById(id);
	}
	/**
	 * 通过Class跳转界面
	 * @param cls
	 */
	protected void startActivity(Class<?> cls) {
		startActivity(cls, null);
	}
	/**
	 * 通过Class跳转界面
	 * @param cls
	 * @param bundle
	 */
	protected void startActivity(Class<?> cls, Bundle bundle) {
		startActivity(cls, bundle,REQUEST_ACTIVITY);
	}
	/**
	 * 通过Class跳转界面
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
		//安卓5.0以后的切换activity动画
		/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			startActivityForResult(intent,requestCode, ActivityOptions.makeSceneTransitionAnimation(mActivity).toBundle());


		}else{*/
			startActivityForResult(intent,requestCode);
		//}
	}/**
	 * 通过Class跳转界面
	 * @param cls
	 * @param bundle
	 * @param requestCode
	 */
	protected void startActivity(Class<?> cls, Bundle bundle, int requestCode,View view) {
		Intent intent = new Intent();
		intent.setClass(mActivity, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			//startActivityForResult(intent,requestCode, ActivityOptions.makeSceneTransitionAnimation(mActivity).toBundle());
			startActivityForResult(intent,requestCode, ActivityOptions.makeSceneTransitionAnimation(mActivity,view,"share").toBundle());



		}else{
			startActivityForResult(intent,requestCode);
		}
	}

	/**
	 * 通过Action跳转界面
	 * @param action
	 */
	protected void startActivity(String action) {
		startActivity(action, null);
	}
	/**
	 * 通过Action跳转界面
	 * @param action
	 * @param bundle
	 */
	protected void startActivity(String action, Bundle bundle) {
		startActivity(action,bundle,REQUEST_ACTIVITY);
	}
	/**
	 * 通过Action跳转界面
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
		startActivityForResult(intent,requestCode);
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
	public void showPromptView(Object promptTip, View.OnClickListener promptRefreshListener) {
		if (mPromptLl == null) {
			mPromptLl = (LinearLayout) findViewById(R.id.prompt_ll);
		}
		mPromptLl.setVisibility(View.VISIBLE);
		mPromptLl.setOnClickListener(null);

		(mPromptLl.findViewById(R.id.prompt_iv_icon)).setVisibility(View.VISIBLE);// 提示信息图片
		(mPromptLl.findViewById(R.id.prompt_tv_tips)).setVisibility(View.VISIBLE);// 提示信息__内容
		//(mPromptLl.findViewById(R.id.prompt_btn_refresh)).setVisibility(View.VISIBLE);// 提示信息__刷新按钮

		TextView mPromptTvTip = (TextView) mPromptLl.findViewById(R.id.prompt_tv_tips);// 提示信息__内容
		// 提示信息__刷新按钮
		if (promptRefreshListener == null) {
			(mPromptLl.findViewById(R.id.prompt_iv_icon)).setBackground(getResources().getDrawable(R.mipmap.no_message_icon));// 提示信息图片
			if (promptTip != null && (promptTip instanceof String)) {
				mPromptTvTip.setText(promptTip.toString());
			} else if (promptTip != null && (promptTip instanceof Integer)) {
				mPromptTvTip.setText(Integer.parseInt(promptTip.toString()));
			} else {
				mPromptTvTip.setText(R.string.view_prompt_pictxt_text);
			}
		} else {
			mPromptTvTip.setText(R.string.view_prompt_clickretry_text);
			(mPromptLl.findViewById(R.id.prompt_iv_icon)).setBackground(getResources().getDrawable(R.mipmap.no_network_icon));// 提示信息图片
			mPromptLl.setVisibility(View.VISIBLE);
			mPromptLl.setOnClickListener(promptRefreshListener);
		}
	}
}
