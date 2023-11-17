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

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.interlok.resolver.ExternalResolver;
import com.adaptris.security.password.Password;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

class Tunnel {

  private static final String NO_KERBEROS_AUTH = "publickey,keyboard-interactive,password";
  private static final String SSH_PREFERRED_AUTHENTICATIONS = "PreferredAuthentications";

  private transient TunnelConfig config;
  private static final Logger log = LoggerFactory.getLogger(Tunnel.class);

  private transient JSch jsch;
  private transient Session session;
  private transient ScheduledExecutorService executor;
  private transient Set<ScheduledFuture<?>> tunnelMonitors = Collections.newSetFromMap(new WeakHashMap<ScheduledFuture<?>, Boolean>());

  public Tunnel(TunnelConfig config, ScheduledExecutorService exec) throws Exception {
    this.config = config;
    executor = exec;
    jsch = addIdentity(new JSch(), config);
  }

  public Tunnel connect() throws Exception {
    _connect();
    return this;
  }

  public Tunnel start() throws Exception {
    _start();
    return this;
  }

  public Tunnel stop() throws Exception {
    for (ScheduledFuture<?> f : tunnelMonitors) {
      f.cancel(true);
    }
    _stopQuietly();
    return this;
  }

  private void _connect() throws Exception {
    HostPortPair host = new HostPortPair(config.getHost());
    log.trace("Trying to connect to {}:{} as {}", host.getHost(), host.getPort(), config.getUser());
    session = jsch.getSession(config.getUser(), host.getHost(), host.getPort());
    session.setProxy(config.buildProxy());
    session.setDaemonThread(true);
    session.setServerAliveInterval(Long.valueOf(TimeUnit.SECONDS.toMillis(config.getKeepAliveSeconds())).intValue());
    session.setConfig(SSH_PREFERRED_AUTHENTICATIONS, NO_KERBEROS_AUTH);
    session.setUserInfo(new StaticPassword(Password.decode(ExternalResolver.resolve(config.getPassword()))));
    int connectTimeout = Long.valueOf(TimeUnit.SECONDS.toMillis(config.getConnectTimeoutSeconds())).intValue();
    session.connect(connectTimeout);
    log.trace("Connected to {}:{} as {}", host.getHost(), host.getPort(), config.getUser());
  }

  private void _start() throws Exception {
    for (String s : config.getTunnels()) {
      LocalRemotePortPair ports = new LocalRemotePortPair(s);
      log.trace("Creating Tunnel : localPort {} -> remote {}", ports.getLocalPort(), ports.getRemotePort());
      session.setPortForwardingL(ports.getLocalPort(), "localhost", ports.getRemotePort());
    }
    scheduleNextRun(new TunnelMonitor());
  }

  private void _stopQuietly() {
    try {
      if (session != null) {
        session.disconnect();
        log.trace("Disconnected from {}", config.getHost());
        session = null;
      }
    } catch (Exception ignored) {

    }
  }

  private void scheduleNextRun(Runnable r) {
    tunnelMonitors.add(executor.schedule(r, 60, TimeUnit.SECONDS));
  }

  private class TunnelMonitor implements Runnable {

    @Override
    public void run() {
      if (session.isConnected()) {
        scheduleNextRun(this);
      } else {
        log.warn("Tunnel is/was disconnected; attempting restart");
        scheduleNextRun(new TunnelRetry());
      }
    }
  }

  private class TunnelRetry implements Runnable {

    @Override
    public void run() {
      try {
        _connect();
        _start();
      } catch (Exception e) {
        log.trace("Failed to reconnect tunnel, scheduling retry");
        _stopQuietly();
        scheduleNextRun(this);
      }
    }
  }

  private static JSch addIdentity(JSch jsch, TunnelConfig config) throws Exception {
    if (!isBlank(config.getPrivateKeyFile())) {
      String pw = ExternalResolver.resolve(config.getPrivateKeyPassword());
      if (pw != null) {
        jsch.addIdentity(config.getPrivateKeyFile(), Password.decode(pw).getBytes());
      } else {
        jsch.addIdentity(config.getPrivateKeyFile());
      }
    }
    return jsch;
  }

  protected static class HostPortPair {
    private String hostname;
    private int port = 22;

    protected HostPortPair(String combinedHostPort) {
      String[] pair = combinedHostPort.split(":");
      hostname = pair[0];
      if (pair.length > 1) {
        port = NumberUtils.toInt(pair[1], 22);
      }
    }

    protected String getHost() {
      return hostname;
    }

    protected int getPort() {
      return port;
    }
  }

  protected static class LocalRemotePortPair {
    private Integer localPort;
    private Integer remotePort;

    protected LocalRemotePortPair(String portPairs) {
      String[] pair = portPairs.split(":");
      if (pair.length > 1) {
        localPort = Integer.parseInt(pair[0]);
        remotePort = Integer.parseInt(pair[1]);
      }
    }

    protected int getLocalPort() {
      if (localPort != null) {
        return localPort.intValue();
      }
      throw new IllegalArgumentException();
    }

    protected int getRemotePort() {
      if (remotePort != null) {
        return remotePort.intValue();
      }
      throw new IllegalArgumentException();
    }
  }

  protected static class StaticPassword implements UserInfo {

    private String password;

    protected StaticPassword(String pw) {
      password = pw;
    }

    @Override
    public String getPassword() {
      return password;
    }

    @Override
    public boolean promptYesNo(String message) {
      return true;
    }

    @Override
    public String getPassphrase() {
      return null;
    }

    @Override
    public boolean promptPassphrase(String message) {
      return true;
    }

    @Override
    public boolean promptPassword(String message) {
      return true;
    }

    @Override
    public void showMessage(String message) {
      log.trace(message);
    }
  }

}
