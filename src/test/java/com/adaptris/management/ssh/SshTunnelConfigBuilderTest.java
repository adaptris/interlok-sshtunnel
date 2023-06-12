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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.Test;

public class SshTunnelConfigBuilderTest {

  @Test
  public void testBuild() throws Exception {
    TunnelConfigBuilder builder = new TunnelConfigBuilder(createProperties());
    List<TunnelConfig> cfgs = builder.build();
    assertEquals(2, cfgs.size());
    TunnelConfig first = cfgs.get(0);
    assertEquals("192.168.1.1:22", first.getHost());
    assertEquals("myIdentifier", first.getUser());
    assertEquals(2, first.getTunnels().size());
    assertEquals("/path/to/privatekey", first.getPrivateKeyFile());
    assertEquals("MyPrivateKeyPassword", first.getPrivateKeyPassword());
    assertNull(first.getPassword());
    assertNotNull(first.buildProxy());
    assertEquals(90, first.getKeepAliveSeconds());
    assertEquals(12, first.getConnectTimeoutSeconds());

    TunnelConfig second = cfgs.get(1);
    assertEquals("192.168.1.2:22", second.getHost());
    assertEquals(1, second.getTunnels().size());
    assertEquals("myOtherIdentifier", second.getUser());
    assertNull(second.getPrivateKeyFile());
    assertNull(second.getPrivateKeyPassword());
    assertEquals("MyUserPassword", second.getPassword());
    assertEquals(60, second.getKeepAliveSeconds());
    assertEquals(12, second.getConnectTimeoutSeconds());
    assertNull(second.buildProxy());

  }

  private Properties createProperties() {
    Properties config = new Properties();
    config.setProperty("sshtunnel.tunnel.myIdentifier.host", "192.168.1.1:22");
    config.setProperty("sshtunnel.tunnel.myIdentifier.tunnel.1", "3306:3306");
    config.setProperty("sshtunnel.tunnel.myIdentifier.tunnel.2", "2506:2506");
    config.setProperty("sshtunnel.tunnel.myIdentifier.user", "myIdentifier");
    config.setProperty("sshtunnel.tunnel.myIdentifier.privateKey", "/path/to/privatekey");
    config.setProperty("sshtunnel.tunnel.myIdentifier.privateKeyPassword", "MyPrivateKeyPassword");
    config.setProperty("sshtunnel.tunnel.myIdentifier.keepAlive.seconds", "90");

    config.setProperty("sshtunnel.tunnel.myOtherIdentifier.host", "192.168.1.2:22");
    config.setProperty("sshtunnel.tunnel.myOtherIdentifier.tunnel", "3306:3306");
    config.setProperty("sshtunnel.tunnel.myOtherIdentifier.user", "myOtherIdentifier");
    config.setProperty("sshtunnel.tunnel.myOtherIdentifier.password", "MyUserPassword");

    config.setProperty("sshtunnel.tunnel.myIdentifier.proxy", "192.168.1.1:3128");
    config.setProperty("sshtunnel.tunnel.myIdentifier.proxy.user", "proxyUser");
    config.setProperty("sshtunnel.tunnel.myIdentifier.proxy.password", "proxyPassword");
    config.setProperty("sshtunnel.connect.timeout.seconds", "12");
    return config;
  }

}
