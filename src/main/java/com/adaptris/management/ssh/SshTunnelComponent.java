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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.core.management.ManagementComponent;
import com.adaptris.core.util.PropertyHelper;

public class SshTunnelComponent implements ManagementComponent {
  private transient Logger log = LoggerFactory.getLogger(this.getClass().getName());

  private transient ClassLoader classLoader;

  private transient Map<String, String> config;
  private transient List<TunnelConfig> tunnelConfigs = new ArrayList<>();;
  private transient List<Tunnel> tunnels = new ArrayList<>();

  @Override
  public void setClassLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  @Override
  public void init(Properties config) throws Exception {
    this.config = PropertyHelper.asMap(config);
    tunnelConfigs = new TunnelConfigBuilder(config).build();
  }

  @Override
  public void start() throws Exception {
    tunnels = new ArrayList<>();
    // If you're using the tunnel, then we want to make sure everything
    // is started before actually starting the adapter...
    for (TunnelConfig cfg : tunnelConfigs) {
      tunnels.add(new Tunnel(cfg).connect().start());
    }
  }

  @Override
  public void stop() throws Exception {
    for (Tunnel t : tunnels) {
      t.stop();
    }
  }

  @Override
  public void destroy() throws Exception {
    tunnels.clear();
  }
}
