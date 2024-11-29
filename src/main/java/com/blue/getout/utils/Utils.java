package com.blue.getout.utils;

import org.springframework.stereotype.Component;

@Component
public class Utils {

    public String getRandomAvatarUrl(){
        String AVATAR_URL = "https://getoutimages.blob.core.windows.net/avatars/";
        int AVATAR_COUNT = 36;
        int index = (int) (Math.floor(Math.random() * AVATAR_COUNT)+1);
        return AVATAR_URL +index+".png";
    }
}
