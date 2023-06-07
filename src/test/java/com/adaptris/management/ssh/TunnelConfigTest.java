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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.adaptris.security.exc.PasswordException;

public class TunnelConfigTest {

  @BeforeEach
  public void setUp() throws Exception {
  }

  @AfterEach
  public void tearDown() throws Exception {
  }

  @Test
  public void testBuildProxy() throws PasswordException {
    assertNull(new TunnelConfig().buildProxy());
    assertNotNull(new TunnelConfig().withProxy("localhost:3128", "user", "password").buildProxy());
    assertNotNull(new TunnelConfig().withProxy("localhost:3128", null, null).buildProxy());
    assertNotNull(new TunnelConfig().withProxy("localhost:3128", "user", null).buildProxy());
    assertNotNull(new TunnelConfig().withProxy("localhost:3128", null, "password").buildProxy());
  }

  @Test
  public void testWithHost() {
    assertNull(new TunnelConfig().getHost());
    assertEquals("host", new TunnelConfig().withHost("host").getHost());
  }

  @Test
  public void testWithTunnels() {
    assertEquals(0, new TunnelConfig().getTunnels().size());
    assertEquals(2, new TunnelConfig().withTunnels("1234:1234", "2345:2345").getTunnels().size());
  }

  @Test
  public void testWithUser() {
    assertNull(new TunnelConfig().getUser());
    assertEquals("user", new TunnelConfig().withUser("user").getUser());
  }

  @Test
  public void testWithPrivateKeyFile() {
    assertNull(new TunnelConfig().getPrivateKeyFile());
    assertEquals("myFile", new TunnelConfig().withPrivateKeyFile("myFile").getPrivateKeyFile());
  }

  @Test
  public void testWithPrivateKeyPassword() {
    assertNull(new TunnelConfig().getPrivateKeyPassword());
    assertEquals("myPassword", new TunnelConfig().withPrivateKeyPassword("myPassword").getPrivateKeyPassword());
  }

  @Test
  public void testWithPassword() {
    assertNull(new TunnelConfig().getPassword());
    assertEquals("myPassword", new TunnelConfig().withPassword("myPassword").getPassword());
  }

  @Test
  public void testWithKeepAliveSeconds() {
    assertEquals(60, new TunnelConfig().getKeepAliveSeconds());
    assertEquals(90, new TunnelConfig().withKeepAliveSeconds(90).getKeepAliveSeconds());
  }

  @Test
  public void testWithConnectTimeout() {
    assertEquals(60, new TunnelConfig().getConnectTimeoutSeconds());
    assertEquals(90, new TunnelConfig().withConnectTimeout(90).getConnectTimeoutSeconds());
    TunnelConfig cfg = new TunnelConfig();
    cfg.setConnectTimeoutSeconds("90");
    assertEquals(90, cfg.getConnectTimeoutSeconds());
  }
}
