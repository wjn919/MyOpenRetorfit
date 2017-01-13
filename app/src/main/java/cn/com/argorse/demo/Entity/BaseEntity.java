package cn.com.argorse.demo.Entity;

import java.io.Serializable;

/**
 * Created by wjn on 2016/8/8.
 */
public class BaseEntity<E> implements Serializable {
    private boolean error;
    private E results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public E getResults() {
        return results;
    }

    public void setResults(E results) {
        this.results = results;
    }
}

