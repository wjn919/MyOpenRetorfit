package cn.com.argorse.common.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;

/**
 * Created by wjn on 2016/6/27.
 */
public class JsonUtils {

    static Gson gson = new Gson();

    public static <T> T resolveEntity(String response, Type typeOfT) {
        JsonParser parser = new JsonParser();
        JsonObject rootNode = parser.parse(response).getAsJsonObject();
        T object = (T) gson.fromJson(rootNode, typeOfT);
        return object;
    }
}
