package org.wisebrains.utils.rest.client;

import org.apache.commons.lang3.StringUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private static final Logger LOG = LoggerFactory.getLogger("org.wisebrains.utils.rest.client.IdentityRestInjector");
  private final int USERS_TO_INJECT_PER_CYCLE = 100;
  private final int TOTAL_USERS_TO_INJECT = 10000;
  private final String HOST = "localhost";
  private final String PORT = "8080";
  private String host;
  private String port;
  private int usersToInjectPerCycle;
  private int totalUsersToInject;

  public IdentityRestInjector(String host, String port, int usersToInjectPerCycle, int totalUsersToInject) {
    this.host = (!StringUtils.isBlank(host)) ? host : HOST;
    this.port = (!StringUtils.isBlank(port)) ? port : PORT;
    this.usersToInjectPerCycle = (usersToInjectPerCycle != 0) ? usersToInjectPerCycle : USERS_TO_INJECT_PER_CYCLE;
    this.totalUsersToInject = (totalUsersToInject != 0) ? totalUsersToInject : TOTAL_USERS_TO_INJECT;
  }

  public static void main(String[] args) throws IOException {

    DefaultHttpClient httpClient = new DefaultHttpClient();

    IdentityRestInjector identityRestInjector = new IdentityRestInjector(args[0], args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));

    String loginURL = String.format("http://%s:%s/portal/login",identityRestInjector.host,identityRestInjector.port);

    String injectionURI = String.format("http://%s:%s/portal/private/rest/bench/inject/identity?number=",identityRestInjector.host,identityRestInjector.port);

    try {
      LOG.info("Trying to perform a login...");
      HttpPost httpPost = new HttpPost(loginURL); //URL to login page
      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
      nameValuePairs.add(new BasicNameValuePair("username", "root"));
      nameValuePairs.add(new BasicNameValuePair("password", "gtn"));
      httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
      HttpResponse response = httpClient.execute(httpPost);
      HttpEntity entity = response.getEntity();

      LOG.info("LOGIN STATUS: {}", response.getStatusLine());
      EntityUtils.consume(entity);
      //Checking the sessionID in cookies
      List<Cookie> cookies = httpClient.getCookieStore().getCookies();
      if (cookies.isEmpty()) {
        LOG.info("Failed login");
      } else {
        Iterator iterator = cookies.iterator();
        while (iterator.hasNext()) {
          Cookie cookie = (Cookie) iterator.next();
          LOG.info("Name: {} / Value: {}", cookie.getName(), cookie.getValue());
        }
      }

      int iterations = identityRestInjector.totalUsersToInject / identityRestInjector.usersToInjectPerCycle;
      LOG.info("{} users will be injected in {} cycles...", identityRestInjector.totalUsersToInject, iterations);

      String injectionURL = injectionURI.concat(String.valueOf(identityRestInjector.usersToInjectPerCycle));

      for (int i = 0; i<iterations; i++){
        LOG.info("Injecting Lot n°: {}", i++);
        HttpGet httpGet = new HttpGet(injectionURL);
        response = httpClient.execute(httpGet);
        entity = response.getEntity();
        LOG.info("User creation status: {}", response.getStatusLine());
        String responseString = EntityUtils.toString(entity, "UTF-8");
        LOG.info(responseString);
      }

      if ((identityRestInjector.totalUsersToInject % identityRestInjector.usersToInjectPerCycle) != 0){
        injectionURL = injectionURI.concat(String.valueOf(identityRestInjector.totalUsersToInject % identityRestInjector.usersToInjectPerCycle));
        HttpGet httpGet = new HttpGet(injectionURL);
        response = httpClient.execute(httpGet);
        entity = response.getEntity();
        LOG.info("User creation status: {}", response.getStatusLine());
        String responseString = EntityUtils.toString(entity, "UTF-8");
        LOG.info(responseString);
      }

    } finally {
      //Close the HTTP connection
      LOG.info("** User injection done, the connection will be closed **");
      httpClient.getConnectionManager().shutdown();
    }

  }

}
