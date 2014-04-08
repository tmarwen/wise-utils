package org.wisebrains.utils.rest.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by eXo Platform MEA on 08/04/14.
 *
 * @author <a href="mailto:mtrabelsi@exoplatform.com">Marwen Trabelsi</a>
 */
public class IdentityRestInjector {

  private static final Log LOG = LogFactory.getLog("Manage Document RESTful Client");

  public static void main(String[] args) throws IOException {

    DefaultHttpClient httpClient = new DefaultHttpClient();

    String loginURL = "http://localhost:8080/portal/login";
    String injectionURI = "http://localhost:8080/portal/private/rest/bench/inject/identity?number=";

    try {
      LOG.info("Trying to perform a login...");
      HttpPost httpost = new HttpPost(loginURL); //URL to login page
      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
      nameValuePairs.add(new BasicNameValuePair("username", "root"));
      nameValuePairs.add(new BasicNameValuePair("password", "gtn"));
      httpost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
      HttpResponse response = httpClient.execute(httpost);
      HttpEntity entity = response.getEntity();

      LOG.info(String.format("LOGIN STATUS: %s", response.getStatusLine()));
      EntityUtils.consume(entity);
      //Checking the sessionID in cookies
      List<Cookie> cookies = httpClient.getCookieStore().getCookies();
      if (cookies.isEmpty()) {
        LOG.info("Failed login");
      } else {
        Iterator iterator = cookies.iterator();
        while (iterator.hasNext()) {
          Cookie cookie = (Cookie) iterator.next();
          LOG.info(String.format("Name: %s / Value: %s", cookie.getName(), cookie.getValue()));
        }
      }

      int usersToInjectPerCycle = 100;
      int totalUsersToInject = 10000;
      int iterations = totalUsersToInject / usersToInjectPerCycle;

      String injectionURL = injectionURI.concat(String.valueOf(usersToInjectPerCycle));

      for (int i = 0; i<iterations; i++){
        HttpGet httpGet = new HttpGet(injectionURL);
        response = httpClient.execute(httpGet);
        entity = response.getEntity();
        LOG.info(String.format("User creation status: %s", response.getStatusLine()));
        String responseString = EntityUtils.toString(entity, "UTF-8");
        LOG.info(responseString);
      }

      if ((totalUsersToInject % usersToInjectPerCycle) != 0){
        injectionURL = injectionURI.concat(String.valueOf(totalUsersToInject % usersToInjectPerCycle));
        HttpGet httpGet = new HttpGet(injectionURL);
        response = httpClient.execute(httpGet);
        entity = response.getEntity();
        LOG.info(String.format("User creation status: %s", response.getStatusLine()));
        String responseString = EntityUtils.toString(entity, "UTF-8");
        LOG.info(responseString);
      }

    } finally {
      //Close the HTTP connection
      httpClient.getConnectionManager().shutdown();
    }

  }

}
