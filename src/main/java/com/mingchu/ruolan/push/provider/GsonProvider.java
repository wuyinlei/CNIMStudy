package com.mingchu.ruolan.push.provider;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

/**
 * 该工具类完成了  把Http请求中的请求数据转换为Model实体
 * 同时也实现了把返回的Model实体转换为Json字符串  并输出到Http的返回体重
 *
 * @param <T> 任意泛型
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonProvider<T> implements MessageBodyReader<T>, MessageBodyWriter<T> {
    private static final Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder()
                .serializeNulls()  //序列化为null的字段
                .excludeFieldsWithoutExposeAnnotation()  //仅仅处理带有@Expose注解的变量
                .enableComplexMapKeySerialization(); //支持Map
        //添加对Java8 LocalDateTime时间类型的支持
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter());
        gson = builder.create();
    }

    public GsonProvider() {
    }

    public static Gson getGson() {
        return gson;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations,
                      MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                      InputStream entityStream) throws IOException, WebApplicationException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(entityStream, "UTF-8"))) {
            return gson.fromJson(reader, genericType);
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(T t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        //TypeAdapter<T> adapter = gson.getAdapter((TypeToken<T>) TypeToken.get(genericType));
        try (JsonWriter jsonWriter = gson.newJsonWriter(new OutputStreamWriter(entityStream, Charset.forName("UTF-8")))) {
            gson.toJson(t, genericType, jsonWriter);
            jsonWriter.close();
        }
    }
}