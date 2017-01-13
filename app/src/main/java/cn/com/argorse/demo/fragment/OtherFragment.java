package cn.com.argorse.demo.fragment;

import android.content.res.Resources;
import android.support.v4.view.ViewPager;

import cn.com.argorse.common.view.tab.SlidingTabLayout;
import cn.com.argorse.demo.BaseFragment;
import cn.com.argorse.demo.R;
import cn.com.argorse.demo.adapter.MainTabPagerAdapter;

/**
 * Created by wjn on 2016/11/8.
 */
public class OtherFragment extends BaseFragment {

    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private MainTabPagerAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_other;
    }

    @Override
    protected void initViews() {

        initTabs();
    }

    private void initTabs() {
        mViewPager = (ViewPager) findViewById(R.id.pager_container);
        mSlidingTabLayout = (SlidingTabLayout)findViewById(R.id.stl_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.view_tab_indicator, R.id.tv_media_name);
        Resources res = getResources();
        int color = res.getColor(R.color.font_red);
        mSlidingTabLayout.setSelectedIndicatorColors(color);
        mSlidingTabLayout.setDistributeEvenly(true);//是否填充满屏幕的宽度
        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.view_pager_margin));
        if (mAdapter == null) {
            mAdapter = new MainTabPagerAdapter(getFragmentManager());


        }
        mViewPager.setOffscreenPageLimit(mAdapter.getCount());
        mViewPager.setAdapter(mAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);

    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void loadData() {

    }
}
