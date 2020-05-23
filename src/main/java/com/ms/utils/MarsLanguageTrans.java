package com.ms.utils;

import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

@Component
public class MarsLanguageTrans {
    public static String trans(String words) throws Exception {
        String marsLanguage = null;
        HttpPost httpPost = new HttpPost("http://www.fzlft.com/?#result");
        StringEntity reqEntity = new StringEntity("q=" + URLEncoder.encode(words, "utf-8"));
        reqEntity.setContentType("application/x-www-form-urlencoded");
        httpPost.setEntity(reqEntity);
        HttpEntity entity = HttpClients.createDefault().execute(httpPost).getEntity();
        String responseCode = EntityUtils.toString(entity, "utf-8");
        marsLanguage = responseCode.substring(responseCode.indexOf("class=\"input result\" name=\"q\">") + 30, responseCode.indexOf("</textarea><div class=\"control\">"));
        return marsLanguage;
    }
}
