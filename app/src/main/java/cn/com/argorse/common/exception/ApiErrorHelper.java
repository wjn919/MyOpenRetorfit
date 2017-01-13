package cn.com.argorse.common.exception;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import cn.com.argorse.common.utils.AlertUtils;
import cn.com.argorse.demo.activity.UserLoginActivity;
import retrofit2.adapter.rxjava.HttpException;

public class ApiErrorHelper {

    private static final java.lang.String TAG = "ApiErrorHelper";

    public static void handleCommonError(final Context context, Throwable e) {
        if (e instanceof HttpException || e instanceof IOException) {
            AlertUtils.showWarnDialog(context,"网络异常", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    System.exit(0);


                }
            });
        } else if (e instanceof ApiException) {
           handleApiError(context, e);
        } else {
           Log.e(TAG, e.getMessage());
        }


    }

    private static void handleApiError(final Context context, Throwable e) {
        ApiException exception = (ApiException) e;
        switch (exception.getErrorCode()) {
            case ApiCode.ERROR_OTHER:
                AlertUtils.showWarnDialog(context, exception.getExtendErrorMsg(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, UserLoginActivity.class);//跳转到登录页面
                        context.startActivity(intent);


                    }
                });
                break;
            default:
                AlertUtils.showWarnDialog(context, exception.getExtendErrorMsg(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();


                    }
                });
                break;

        }


    }

}
