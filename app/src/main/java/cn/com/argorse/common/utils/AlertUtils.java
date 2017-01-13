package cn.com.argorse.common.utils;/*
 * ProjectName:   AND-Pocket
 * FileName:      AlertUtils.java
 * Description:
 * Create at:	2014-3-17 上午11:24:13
 * @author:		roc.hu(roc.hu@msn.com)
 * @version:	V1.0.0
 * Copyright:   Copyright (c) 2013 Beijing Argorse Technology Development Co., LTD
 * Website：	http://www.argorse.com.cn 
 */


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.com.argorse.demo.R;


/**
 * Description:   提示信息
 * @author        roc.hu
 * @version       1.0
 *
 * Modification History:
 * DateTime			-*#*-[Author]-*-[Version]	###	Description
 * ------------------------------------------------------------------
 * 2014-3-17 上午11:24:13	-*#*-[roc.hu]-*-[1.0]		###	des
 */
public class AlertUtils {
	/** 网络请求 **/
	private static ProgressDialog mProgDialog = null;
	
	/**
	 * 展示loading框
	 */
	public static void showLoadingDialog(Context context){
		if (mProgDialog == null){
			mProgDialog = new ProgressDialog(context);
			mProgDialog.setMessage(context.getString(R.string.sys_warn_plase_wait));
			mProgDialog.setCancelable(false);
			mProgDialog.setCanceledOnTouchOutside(false);
			mProgDialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if(keyCode== KeyEvent.KEYCODE_BACK){
		                return true;
		            }
					return false;
				}
			});
		}
		mProgDialog.show();
	}
	/**
	 * 销毁loading框
	 */
	public static void dismissLoadingDialog(){
		if (mProgDialog != null&& mProgDialog.isShowing())
			mProgDialog.dismiss();
		mProgDialog=null;
	}
	/**
	 * Toast Toast.LENGTH_SHORT
	 * 
	 * @param msg
	 */
	public static void showToast(Context cxt, int msg) {
		Toast.makeText(cxt, msg, Toast.LENGTH_SHORT).show();
	}
	/**
	 * Toast Toast.LENGTH_SHORT
	 * 
	 * @param msg
	 */
	public static void showToast(Context cxt, String msg) {
		Toast.makeText(cxt, msg, Toast.LENGTH_SHORT).show();
	}
	/**
	 * 带图片的提示信息
	 * @param  msgResId msg
	 */
	public static void showPicToast(Context cxt, int msgResId){
		//实例化一个Toast对象
		Toast toast= Toast.makeText(cxt,msgResId, 2*1000);
		toast.setGravity(Gravity.CENTER, 0, 0);
		LinearLayout toastView = (LinearLayout) toast.getView();
		ImageView imageView = new ImageView(cxt);
		imageView.setImageResource(R.mipmap.ic_launcher);
//		TextView textView=new TextView(this);
//		textView.setText(msgResId);
//		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
		toastView.addView(imageView,0);
//		toastView.addView(textView,1);
		toast.show();
	}
	/**
	 * 弹出提示框
	 * 
	 * @param msg 提示消息，
	 * @return 无
	 */
	public static void showWarnDialog(Context cxt, String msg) {
		AlertDialog.Builder erroBuilder = new AlertDialog.Builder(cxt);
		erroBuilder.setMessage(msg);
		erroBuilder.setPositiveButton(R.string.sys_confirm_text,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		erroBuilder.show();
	}
	/**
	 * 弹出提示框
	 * 
	 * @param msg 提示消息，
	 * @param listener ShowWarnDialogListener listener
	 * 
	 * @return 无
	 */
	public static void showWarnDialog(Context cxt, String msg, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder erroBuilder = new AlertDialog.Builder(cxt);
		erroBuilder.setMessage(msg);
		erroBuilder.setCancelable(false);
		erroBuilder.setPositiveButton(R.string.sys_confirm_text,listener);
		erroBuilder.show();
	}
	/**
	 * 弹出提示框
	 * 
	 * @param msg 提示消息
	 * @return 无
	 */
	public static void showWarnDialog(Context cxt, int msg) {
		AlertDialog.Builder erroBuilder = new AlertDialog.Builder(cxt);
		erroBuilder.setMessage(msg);
		erroBuilder.setPositiveButton(R.string.sys_confirm_text,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		erroBuilder.show();
	}
	/**
	 * 弹出提示框
	 * 
	 * @param msg msg 提示消息
	 * @param listener ShowWarnDialogListener listener
	 * 
	 * @return 无
	 */
	public static void showWarnDialog(Context cxt, int msg, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder erroBuilder = new AlertDialog.Builder(cxt);
		erroBuilder.setMessage(msg);
		erroBuilder.setCancelable(false);
		erroBuilder.setPositiveButton(R.string.sys_confirm_text,listener);
		erroBuilder.show();
	}
}
