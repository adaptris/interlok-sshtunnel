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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TunnelTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testHostPortPair() {
    Tunnel.HostPortPair pair = new Tunnel.HostPortPair("localhost");
    assertEquals("localhost", pair.getHost());
    assertEquals(22, pair.getPort());
    pair = new Tunnel.HostPortPair("localhost:4444");
    assertEquals("localhost", pair.getHost());
    assertEquals(4444, pair.getPort());
  }

  @Test(expected = NumberFormatException.class)
  public void testLocalRemotePortPair() {
    Tunnel.LocalRemotePortPair pair = new Tunnel.LocalRemotePortPair("4444:5555");
    assertEquals(4444, pair.getLocalPort());
    assertEquals(5555, pair.getRemotePort());
    pair = new Tunnel.LocalRemotePortPair("localhost");
    try {
      pair.getLocalPort();
      fail();
    } catch (IllegalArgumentException expected) {

    }
    try {
      pair.getRemotePort();
      fail();
    } catch (IllegalArgumentException expected) {

    }
    new Tunnel.LocalRemotePortPair("localhost:localhost");
  }

  @Test
  public void testStaticPassword() {
    Tunnel.StaticPassword pwd = new Tunnel.StaticPassword("myPassword");
    assertEquals("myPassword", pwd.getPassword());
    assertTrue(pwd.promptPassphrase(""));
    assertTrue(pwd.promptPassword(""));
    assertTrue(pwd.promptYesNo(""));
    assertNull(pwd.getPassphrase());
    pwd.showMessage("");
  }
}
