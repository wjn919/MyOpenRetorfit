package cn.com.argorse.demo.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.argorse.common.utils.AlertUtils;
import cn.com.argorse.demo.BaseActivity;
import cn.com.argorse.demo.R;

public class MainActivity extends BaseActivity implements  TabHost.OnTabChangeListener{


    NavigationView nvmainnavigation;
    DrawerLayout drawerLayout;
    private FragmentTabHost mTabHost;//TabHost
    private long back_pressed;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        nvmainnavigation = (NavigationView) findViewById(R.id.nv_main_navigation);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setShowDividers(0);
        initTabs();
        mTabHost.setCurrentTab(0);
        mTabHost.setOnTabChangedListener(this);
    }


    //初始化下面的tab
    private void initTabs() {
       MainTabs[] tabs = MainTabs.values();
        for (MainTabs tab : tabs) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(getString(tab.getNameRes()));

            View indicator = inflateView(R.layout.view_home_tab);
            ImageView icon = (ImageView) indicator.findViewById(R.id.tab_iv_image);
            icon.setImageResource(tab.getIconRes());
            TextView title = (TextView) indicator.findViewById(R.id.tab_tv_text);
            title.setText(getString(tab.getNameRes()));

            tabSpec.setIndicator(indicator);
            tabSpec.setContent(new TabHost.TabContentFactory() {
                @Override
                public View createTabContent(String tag) {
                    return new View(MainActivity.this);
                }
            });

            mTabHost.addTab(tabSpec, tab.getClazz(), null);
        }
    }
    @Override
    public void onTabChanged(String tabId) {
        final int tabCount = mTabHost.getTabWidget().getTabCount();
        for (int i = 0; i < tabCount; i++) {
            View tab = mTabHost.getTabWidget().getChildAt(i);
            if (i == mTabHost.getCurrentTab()) {
                tab.findViewById(R.id.tab_iv_image).setSelected(true);
                tab.findViewById(R.id.tab_tv_text).setSelected(true);
            } else {
                tab.findViewById(R.id.tab_iv_image).setSelected(false);
                tab.findViewById(R.id.tab_tv_text).setSelected(false);


            }
        }

        supportInvalidateOptionsMenu();
    }
    @Override
    protected void initEvents() {
        pageleftll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawerLayout.isDrawerOpen(nvmainnavigation)){
                    drawerLayout.closeDrawers();
                }else{
                    drawerLayout.openDrawer(nvmainnavigation);
                }

            }
        });


        nvmainnavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setCheckable(true);
                Toast.makeText(mActivity,item.getTitle(),Toast.LENGTH_SHORT).show();
                //可以通过item.getTitle()获取侧滑菜单上的文字，该处做一些事件
                if(item.getTitle().equals("Android")){
                    mTabHost.setCurrentTab(0);
                    drawerLayout.closeDrawers();

                }else if(item.getTitle().equals("iOS")){
                    mTabHost.setCurrentTab(1);
                    drawerLayout.closeDrawers();

                }else if(item.getTitle().equals("PC")){
                    mTabHost.setCurrentTab(2);
                    drawerLayout.closeDrawers();

                }else if(item.getTitle().equals("web")){
                    mTabHost.setCurrentTab(3);
                    drawerLayout.closeDrawers();

                }else if(item.getTitle().equals("other")){
                    mTabHost.setCurrentTab(0);
                    drawerLayout.closeDrawers();

                }
                return true;
            }
        });

    }




    @Override
    protected void initData() {
        pageleftll.setVisibility(View.GONE);

    }


    @Override
    public void onClick(View view) {

    }

    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            mApplication.closeAllActivity();
        } else {
            AlertUtils.showToast(this, "再按一次退出"+getResources().getString(R.string.app_name));
            back_pressed = System.currentTimeMillis();

        }
    }



}
