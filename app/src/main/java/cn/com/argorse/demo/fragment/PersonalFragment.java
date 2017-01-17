package cn.com.argorse.demo.fragment;

import android.view.View;
import android.widget.LinearLayout;

import cn.com.argorse.demo.BaseActivity;
import cn.com.argorse.demo.BaseFragment;
import cn.com.argorse.demo.R;
import cn.com.argorse.demo.activity.MainActivity;
import cn.com.argorse.demo.activity.UserLoginActivity;

/**
 * Created by wjn on 2016/12/20.
 */
public class PersonalFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout mLogin;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_personal;
    }

    @Override
    protected void initViews() {
        mLogin = (LinearLayout) findViewById(R.id.ll_login);

    }

    @Override
    protected void initEvents() {
        mLogin.setOnClickListener(this);

    }

    @Override
    protected void initData() {



    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity)getActivity()).hideTitle();
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onClick(View view) {
        if(view == mLogin){
            startActivity(UserLoginActivity.class);
        }
    }
}
