package com.moglix.wms.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

/**
 * @author pankaj on 9/5/19
 */
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static Gson gson = new Gson();
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    public static <T> T readObject(String json, Class<T> classType) {
        T obj;
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            obj = objectMapper.readValue(json, classType);
        } catch (IOException e) {
            logger.error("Failed to parse jsonData", e);
            return null;
        }
        return obj;
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }
}
