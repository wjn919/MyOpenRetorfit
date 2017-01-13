package cn.com.argorse.demo.Entity;

/**
 * Created by wjn on 2017/1/5.
 */
public class GoodsType {
    private String goodsType;
    private String goodsName;
    private boolean ischose;

    public boolean ischose() {
        return ischose;
    }

    public boolean isIschose() {
        return ischose;
    }

    public void setIschose(boolean ischose) {
        this.ischose = ischose;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
}
