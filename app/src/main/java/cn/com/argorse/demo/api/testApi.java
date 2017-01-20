package cn.com.argorse.demo.api;
import java.util.List;

import cn.com.argorse.demo.Entity.BaseEntity;
import cn.com.argorse.demo.Entity.ResultsEntity;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by wjn on 2016/11/3.
 */

public interface testApi {
    @GET("data/福利/7/{num}")
    Observable<BaseEntity<List<ResultsEntity>>> getMessage(@Path("num") int num);


}
