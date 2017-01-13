package cn.com.argorse.demo.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import cn.com.argorse.demo.BaseViewPagerAdapter;
import cn.com.argorse.demo.fragment.ArticleFragment;
import cn.com.argorse.demo.fragment.OtherMainTab;


public class MainTabPagerAdapter extends BaseViewPagerAdapter {


    public MainTabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        OtherMainTab tab = OtherMainTab.getTab(position);
        return !TextUtils.isEmpty(tab.getTitle()) ? tab.getTitle() : "";
    }

    @Override
    protected Fragment createItem(int position) {
        final int pat = position % getCount();
        OtherMainTab[] values = OtherMainTab.values();
        Fragment fragment = null;
        try {
            fragment = (Fragment) values[pat].getClazz().newInstance();
            Bundle args = new Bundle();
            args.putInt(ArticleFragment.BUNDLE_KEY_CATALOG, values[pat].getCatalog());
            fragment.setArguments(args);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return OtherMainTab.values().length;
    }

}
