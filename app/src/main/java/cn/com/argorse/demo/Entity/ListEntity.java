package cn.com.argorse.demo.Entity;

import java.io.Serializable;
import java.util.List;

public interface ListEntity<T> extends Serializable {

    List<T> getList();
}
