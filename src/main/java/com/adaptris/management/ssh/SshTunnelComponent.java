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
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.adaptris.core.management.ManagementComponent;
import com.adaptris.core.util.ManagedThreadFactory;
import com.adaptris.core.util.PropertyHelper;
import com.adaptris.util.TimeInterval;

public class SshTunnelComponent implements ManagementComponent {

  private static final TimeInterval SHUTDOWN_TIMEOUT_MS = new TimeInterval(60L, TimeUnit.SECONDS);

  private transient List<TunnelConfig> tunnelConfigs = new ArrayList<>();
  private transient Set<Tunnel> tunnels = new HashSet<>();
  private transient ScheduledExecutorService executor;

  @Override
  public void setClassLoader(ClassLoader classLoader) {
    // no-op
  }

  @Override
  public void init(Properties config) throws Exception {
    executor = Executors.newScheduledThreadPool(1, new ManagedThreadFactory(getClass().getSimpleName()));
    tunnelConfigs = new TunnelConfigBuilder(PropertyHelper.asMap(config)).build();
  }

  @Override
  public void start() throws Exception {
    tunnels = new HashSet<>();
    // If you're using the tunnel, then we want to make sure everything
    // is started before actually starting the adapter...
    for (TunnelConfig cfg : tunnelConfigs) {
      tunnels.add(new Tunnel(cfg, executor).connect().start());
    }
  }

  @Override
  public void stop() throws Exception {
    for (Tunnel t : tunnels) {
      t.stop();
    }
    ManagedThreadFactory.shutdownQuietly(executor, SHUTDOWN_TIMEOUT_MS);
  }

  @Override
  public void destroy() throws Exception {
    tunnels.clear();
  }

}
