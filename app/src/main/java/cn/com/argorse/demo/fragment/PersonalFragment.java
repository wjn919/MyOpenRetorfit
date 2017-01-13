package cn.com.argorse.demo.fragment;

import cn.com.argorse.demo.BaseActivity;
import cn.com.argorse.demo.BaseFragment;
import cn.com.argorse.demo.R;
import cn.com.argorse.demo.activity.MainActivity;

/**
 * Created by wjn on 2016/12/20.
 */
public class PersonalFragment extends BaseFragment{
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_personal;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {

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
}
