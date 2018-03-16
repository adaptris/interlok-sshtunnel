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

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SshTunnelComponentTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testSetClassLoader() {
    SshTunnelComponent comp = new SshTunnelComponent();
    comp.setClassLoader(Thread.currentThread().getContextClassLoader());
  }

  @Test
  public void testLifecycle() throws Exception {
    SshTunnelComponent comp = new SshTunnelComponent();
    comp.init(new Properties());
    comp.start();
    comp.stop();
    comp.destroy();
  }


}
