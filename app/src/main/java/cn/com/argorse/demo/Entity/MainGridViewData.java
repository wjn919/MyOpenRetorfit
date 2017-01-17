package cn.com.argorse.demo.Entity;


import cn.com.argorse.demo.R;

/**
 * Created by wjn on 2015/10/27.
 */
public class MainGridViewData {
    private final String[] displayname = {"品牌服装", "母婴用品", "美妆护肤", "居家生活", "鞋包配饰", "美食天地", "文体车品", "数码家电"};
    private final int[] icon = { R.mipmap.message_header_1, R.mipmap.message_header_2,R.mipmap.message_header_3,R.mipmap.message_header_4,R.mipmap.message_header_5,R.mipmap.message_header_6,R.mipmap.message_header_7,R.mipmap.message_header_8};
    private final String defaultDisplayName = "未知信息";
    private final int defaultIcon = R.mipmap.ic_launcher;

    public String[] getDisplaymainmessage() {
        return displayname;
    }

    //获取文字信息
    public String getDisplaymainmessage(int id) {
        if (id >= 0 && id < displayname.length) {
            return displayname[id];
        } else {
            return defaultDisplayName;
        }
    }

    //获取图标信息
    public int getMainActivityIcon(int id) {
        if (id >= 0 && id < icon.length) {
            return icon[id];
        } else {
            return defaultIcon;
        }
    }
}
