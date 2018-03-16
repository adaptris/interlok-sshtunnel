/*
 * Copyright 2018 Adaptris Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.adaptris.management.ssh;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.math.NumberUtils;

import com.adaptris.interlok.resolver.ExternalResolver;
import com.adaptris.security.exc.PasswordException;
import com.adaptris.security.password.Password;
import com.jcraft.jsch.Proxy;
import com.jcraft.jsch.ProxyHTTP;

public class TunnelConfig {

  private static final long DEFAULT_KEEPALIVE = TimeUnit.MINUTES.toSeconds(1L);
  private static final long DEFAULT_CONNECT_TIMEOUT = TimeUnit.MINUTES.toSeconds(1L);

  private String host;
  private List<String> tunnels;
  private String user;
  private String privateKeyFile;
  private String privateKeyPassword;
  private String password;
  private long keepAliveSeconds = DEFAULT_KEEPALIVE;
  private String proxy;
  private String proxyUser;
  private String proxyPassword;
  private long connectTimeoutSeconds = DEFAULT_CONNECT_TIMEOUT;

  public TunnelConfig() {
    tunnels = new ArrayList<>();
  }

  public Proxy buildProxy() throws PasswordException {
    String resolvedProxyHost = ExternalResolver.resolve(proxy);
    String resolvedProxyUser = ExternalResolver.resolve(proxyUser);
    String resulvedProxyPassword = ExternalResolver.resolve(proxyPassword);
    ProxyHTTP proxy = null;
    if (!isBlank(resolvedProxyHost)) {
      proxy = new ProxyHTTP(resolvedProxyHost);
      if (isBlank(resolvedProxyUser)) {
        proxy.setUserPasswd(resolvedProxyUser, Password.decode(resulvedProxyPassword));
      }
    }
    return proxy;
  }

  public TunnelConfig withHost(String h) {
    setHost(h);
    return this;
  }

  public TunnelConfig withTunnels(String... tunnels) {
    setTunnels(Arrays.asList(tunnels));
    return this;
  }

  public TunnelConfig withUser(String s) {
    setUser(s);
    return this;
  }

  public TunnelConfig withPrivateKeyFile(String s) {
    setPrivateKeyFile(s);
    return this;
  }

  public TunnelConfig withPrivateKeyPassword(String s) {
    setPrivateKeyPassword(s);
    return this;
  }

  public TunnelConfig withPassword(String s) {
    setPassword(s);
    return this;
  }

  public TunnelConfig withKeepAliveSeconds(long s) {
    keepAliveSeconds = s;
    return this;
  }

  public TunnelConfig withProxy(String proxy, String user, String password) {
    setProxy(proxy, user, password);
    return this;
  }

  public TunnelConfig withConnectTimeout(long s) {
    connectTimeoutSeconds = s;
    return this;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public List<String> getTunnels() {
    return tunnels;
  }

  public void setTunnels(List<String> tunnels) {
    for (String t : tunnels) {
      this.tunnels.add(t);
    }
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPrivateKeyFile() {
    return privateKeyFile;
  }

  public void setPrivateKeyFile(String key) {
    this.privateKeyFile = key;
  }

  public String getPrivateKeyPassword() {
    return privateKeyPassword;
  }

  public void setPrivateKeyPassword(String privateKeyPassword) {
    this.privateKeyPassword = privateKeyPassword;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public long getKeepAliveSeconds() {
    return keepAliveSeconds;
  }

  public void setKeepAliveSeconds(String keepAlive) {
    this.keepAliveSeconds = NumberUtils.toLong(keepAlive, DEFAULT_KEEPALIVE);
  }

  public long getConnectTimeoutSeconds() {
    return connectTimeoutSeconds;
  }

  public void setConnectTimeoutSeconds(String timeout) {
    this.connectTimeoutSeconds = NumberUtils.toLong(timeout, DEFAULT_CONNECT_TIMEOUT);
  }

  public void setProxy(String proxy, String user, String password) {
    this.proxy = proxy;
    this.proxyUser = user;
    this.proxyPassword = password;
  }

}
