package cn.com.argorse.demo.activity;

import cn.com.argorse.demo.R;
import cn.com.argorse.demo.fragment.MainFragment;
import cn.com.argorse.demo.fragment.MessageFragment;
import cn.com.argorse.demo.fragment.OtherFragment;
import cn.com.argorse.demo.fragment.PersonalFragment;
import cn.com.argorse.demo.fragment.TradeRecordFragment;

public enum MainTabs {

    TAB_MAIN(0, R.string.home_tab_main, R.drawable.selector_main_tab, MainFragment.class),
    TAB_MESSAGE(1, R.string.home_tab_message, R.drawable.selector_message_tab, MessageFragment.class),
    TAB_TRADE(2, R.string.home_tab_trade, R.drawable.selector_trade_tab, TradeRecordFragment.class),
    TAB_PERSONAL(3,R.string.home_tab_personal, R.drawable.selector_personal_tab, PersonalFragment.class);

    private int index;
    private int nameRes;
    private int iconRes;
    private Class<?> clazz;

    MainTabs(int index, int nameRes, int iconRes, Class<?> clazz) {
        this.index = index;
        this.nameRes = nameRes;
        this.iconRes = iconRes;
        this.clazz = clazz;
    }


    public int getIndex() {
        return index;
    }

    public int getNameRes() {
        return nameRes;
    }

    public int getIconRes() {
        return iconRes;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
