package cn.com.argorse.demo.activity;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import cn.com.argorse.common.utils.FormatStr;
import cn.com.argorse.common.utils.ImageLoaderUtil;
import cn.com.argorse.demo.BaseActivity;
import cn.com.argorse.demo.BaseApplication;
import cn.com.argorse.demo.R;

/**
 * Created by wjn on 2016/11/28.
 */
public class ImageActivity extends BaseActivity {
    ImageView ivimagedetails;
    private String url;

    @Override
    protected void getIntentBundle() {

        url = getIntent().getStringExtra("url");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image;
    }

    @Override
    protected void initViews() {
        ivimagedetails = (ImageView) findViewById(R.id.iv_image_details);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivimagedetails.setTransitionName("share");
        }


    }

    @Override
    protected void initEvents() {
        ivimagedetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });
    }

    @Override
    protected void initData() {
        if(FormatStr.isNotNull(url)){
            ImageLoaderUtil.load(this, url,ivimagedetails);
        }


    }

    @Override
    public void onClick(View view) {

    }


}
