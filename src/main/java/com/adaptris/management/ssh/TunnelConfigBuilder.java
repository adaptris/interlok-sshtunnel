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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.adaptris.core.util.PropertyHelper;

public class TunnelConfigBuilder {

  public static final String SSHTUNNEL_HOST = "sshtunnel.tunnel.%s.host";
  public static final String SSHTUNNEL_TUNNELS = "sshtunnel.tunnel.%s.tunnel";
  public static final String SSHTUNNEL_USER = "sshtunnel.tunnel.%s.user";
  public static final String SSHTUNNEL_PRIVATE_KEY = "sshtunnel.tunnel.%s.privateKey";
  public static final String SSHTUNNEL_PRIVATE_KEY_PASSWORD = "sshtunnel.tunnel.%s.privateKeyPassword";
  public static final String SSHTUNNEL_PASSWORD = "sshtunnel.tunnel.%s.password";
  public static final String SSHTUNNEL_KEEP_ALIVE = "sshtunnel.tunnel.%s.keepAlive.seconds";
  public static final String SSHTUNNEL_PROXY = "sshtunnel.tunnel.%s.proxy";
  public static final String SSHTUNNEL_PROXY_USER = "sshtunnel.tunnel.%s.proxy.user";
  public static final String SSHTUNNEL_PROXY_PASSWORD = "sshtunnel.tunnel.%s.proxy.password";
  public static final String SSHTUNNEL_CONNECT_TIMEOUT = "sshtunnel.connect.timeout.seconds";
  
  private static final String SSHTUNNEL_IDENTIFIER_REGEX = "^sshtunnel\\.tunnel\\.(.*)\\.host$";

  private transient Pattern pattern = Pattern.compile(SSHTUNNEL_IDENTIFIER_REGEX);

  private transient Map<String, String> config;
  private transient Set<String> identifiers;

  private enum ConfigItem {

    Host {

      @Override
      void add(String identifier, Map<String, String> config, TunnelConfig existing) {
        existing.setHost(config.get(String.format(SSHTUNNEL_HOST, identifier)));
      }
      
    },
    Tunnels {
      @Override
      void add(String identifier, Map<String, String> config, TunnelConfig existing) {
        existing.setTunnels(getSubset(config, String.format(SSHTUNNEL_TUNNELS, identifier)));
      }
    },
    User {
      @Override
      void add(String identifier, Map<String, String> config, TunnelConfig existing) {
        existing.setUser(config.get(String.format(SSHTUNNEL_USER, identifier)));
      }

    },
    PrivateKey {
      @Override
      void add(String identifier, Map<String, String> config, TunnelConfig existing) {
        existing.setPrivateKeyFile(config.get(String.format(SSHTUNNEL_PRIVATE_KEY, identifier)));
      }
    },
    PrivateKeyPassword {
      @Override
      void add(String identifier, Map<String, String> config, TunnelConfig existing) {
        existing.setPrivateKeyPassword(config.get(String.format(SSHTUNNEL_PRIVATE_KEY_PASSWORD, identifier)));
      }
    },
    Password {
      @Override
      void add(String identifier, Map<String, String> config, TunnelConfig existing) {
        existing.setPassword(config.get(String.format(SSHTUNNEL_PASSWORD, identifier)));
      }
    },
    KeepAlive {
      @Override
      void add(String identifier, Map<String, String> config, TunnelConfig existing) {
        existing.setKeepAliveSeconds(config.get(String.format(SSHTUNNEL_KEEP_ALIVE, identifier)));
      }
    },
    ConnectTimeout {
      @Override
      void add(String identifier, Map<String, String> config, TunnelConfig existing) {
        existing.setConnectTimeoutSeconds(config.get(SSHTUNNEL_CONNECT_TIMEOUT));
      }
    },
    Proxy {
      @Override
      void add(String identifier, Map<String, String> config, TunnelConfig existing) {
        existing.setProxy(config.get(String.format(SSHTUNNEL_PROXY, identifier)),
            config.get(String.format(SSHTUNNEL_PROXY_USER, identifier)),
            config.get(String.format(SSHTUNNEL_PROXY_PASSWORD, identifier)));
      }
    };
    abstract void add(String identifier, Map<String, String> config, TunnelConfig existing);

  }

  public TunnelConfigBuilder(Properties config) {
    this(PropertyHelper.asMap(config));
  }

  public TunnelConfigBuilder(Map<String, String> config) {
    this.config = config;
    buildIdentifiers();
  }

  private void buildIdentifiers() {
    identifiers = new TreeSet<String>();
    for (String s : config.keySet()) {
      Matcher m = pattern.matcher(s);
      if (m.matches()) {
        identifiers.add(m.group(1));
      }
    }
  }

  public List<TunnelConfig> build() {
    List<TunnelConfig> result = new ArrayList<>();
    for (String id : identifiers) {
      TunnelConfig cfg = new TunnelConfig();
      for (ConfigItem item : ConfigItem.values()) {
        item.add(id, config, cfg);
      }
      result.add(cfg);
    }
    return result;
  }

  private static List<String> getSubset(Map<String, String> p, String prefix) {
    List<String> result = new ArrayList<>();
    for (String key : p.keySet()) {
      if (key.startsWith(prefix)) {
        result.add(p.get(key));
      }
    }
    return result;
  }

}
