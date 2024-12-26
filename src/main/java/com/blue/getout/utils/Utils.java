package com.blue.getout.utils;

import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Utils {

    public String getRandomAvatarUrl(){
        String AVATAR_URL = "https://getoutimages.blob.core.windows.net/avatars/";
        int AVATAR_COUNT = 36;
        int index = (int) (Math.floor(Math.random() * AVATAR_COUNT)+1);
        return AVATAR_URL +index+".png";
    }

    public Object convertStringToZonedDateTime(Class<?> fieldType, Object value) {
        if (value == null) {
            return null; // Null values can be directly assigned
        }

        if (fieldType.equals(ZonedDateTime.class) && value instanceof String) {
            return ZonedDateTime.parse((String) value, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        }
        return value;
    }
}
