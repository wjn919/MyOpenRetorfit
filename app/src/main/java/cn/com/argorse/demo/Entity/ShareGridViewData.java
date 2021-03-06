package cn.com.argorse.demo.Entity;


import cn.com.argorse.demo.R;

/**
 * Created by wjn on 2015/10/27.
 */
public class ShareGridViewData {
    private final String[] displayname = {"QQ", "QQ空间", "微信", "微信朋友圈", "新浪", "复制信息", "复制链接"};
    private final int[] icon = {  R.mipmap.sipay_personal_qq_icon,R.mipmap.sipay_personal_space_icon,R.mipmap.sipay_personal_wechat_icon,R.mipmap.sipay_personal_ciccle_of_friends_icon,R.mipmap.sipay_personal_weibo_icon,R.mipmap.umeng_socialize_copy,R.mipmap.umeng_socialize_copy_url};
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
