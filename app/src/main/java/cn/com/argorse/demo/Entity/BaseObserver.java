package cn.com.argorse.demo.Entity;

import android.content.Context;

import cn.com.argorse.common.exception.ApiErrorHelper;
import cn.com.argorse.common.exception.ApiException;
import cn.com.argorse.common.utils.AlertUtils;
import rx.Subscriber;

/**
 * Created by wjn on 2017/1/13.
 */

public abstract class BaseObserver<T> extends Subscriber<BaseEntity<T>> {
    private Context mContext;
    private final int SUCCESS_CODE = 0;

    public BaseObserver(Context context) {
        mContext = context;
    }


    @Override
    public void onStart() {
        AlertUtils.showLoadingDialog(mContext);
    }
    @Override
    public void onCompleted() {

        AlertUtils.dismissLoadingDialog();
    }


    @Override
    public void onError(Throwable e) {
        ApiErrorHelper.handleCommonError(mContext, e);

    }

    @Override
    public void onNext(BaseEntity<T> entity) {
        if(!entity.isError()){//成功
            onSuccess(entity.getResults());
        }else {//失败
            onError(new ApiException(1, "转换失败"));


        }
    }


    public  void onSuccess(T response){};


}
