package cn.com.argorse.demo.Entity.realm;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wjn on 2016/12/22.
 */

public class HistorySearch extends RealmObject {
    @PrimaryKey
    @Ignore
    private int id;
    private String historyName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHistoryName() {
        return historyName;
    }

    public void setHistoryName(String historyName) {
        this.historyName = historyName;
    }
}
