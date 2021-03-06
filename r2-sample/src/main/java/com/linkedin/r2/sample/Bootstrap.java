/*
   Copyright (c) 2012 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/* $Id$ */
package com.linkedin.r2.sample;

import java.net.URI;
import java.util.Collections;

import com.linkedin.r2.filter.FilterChain;
import com.linkedin.r2.sample.echo.EchoServiceImpl;
import com.linkedin.r2.sample.echo.OnExceptionEchoService;
import com.linkedin.r2.sample.echo.ThrowingEchoService;
import com.linkedin.r2.sample.echo.rest.RestEchoServer;
import com.linkedin.r2.sample.echo.rpc.RpcEchoServer;
import com.linkedin.r2.transport.common.Client;
import com.linkedin.r2.transport.common.Server;
import com.linkedin.r2.transport.common.bridge.client.TransportClient;
import com.linkedin.r2.transport.common.bridge.client.TransportClientAdapter;
import com.linkedin.r2.transport.common.bridge.server.TransportDispatcher;
import com.linkedin.r2.transport.common.bridge.server.TransportDispatcherBuilder;
import com.linkedin.r2.transport.http.client.HttpClientFactory;
import com.linkedin.r2.transport.http.server.HttpServerFactory;

/**
 * @author Chris Pettitt
 * @version $Revision$
 */
public class Bootstrap
{
  private static final int HTTP_PORT = 8877;

  private static final URI ECHO_URI = URI.create("/echo");
  private static final URI ON_EXCEPTION_ECHO_URI = URI.create("/on-exception-echo");
  private static final URI THROWING_ECHO_URI = URI.create("/throwing-echo");

  public static Server createHttpServer(FilterChain filters)
  {
    return createHttpServer(HTTP_PORT, filters);
  }

  public static Server createHttpServer(int port, FilterChain filters)
  {
    return new HttpServerFactory(filters)
            .createServer(port, createDispatcher());
  }

  public static Client createHttpClient(FilterChain filters)
  {
    final TransportClient client = new HttpClientFactory(filters)
            .getClient(Collections.<String, String>emptyMap());
    return new TransportClientAdapter(client);
  }

  public static URI createHttpURI(URI relativeURI)
  {
    return createHttpURI(HTTP_PORT, relativeURI);
  }

  public static URI createHttpURI(int port, URI relativeURI)
  {
    return URI.create("http://localhost:" + port + relativeURI);
  }

  public static URI getEchoURI()
  {
    return ECHO_URI;
  }

  public static URI getOnExceptionEchoURI()
  {
    return ON_EXCEPTION_ECHO_URI;
  }

  public static URI getThrowingEchoURI()
  {
    return THROWING_ECHO_URI;
  }

  private static TransportDispatcher createDispatcher()
  {
    return new TransportDispatcherBuilder()
            .addRpcHandler(ECHO_URI, new RpcEchoServer(new EchoServiceImpl()))
            .addRpcHandler(ON_EXCEPTION_ECHO_URI, new RpcEchoServer(new OnExceptionEchoService()))
            .addRpcHandler(THROWING_ECHO_URI, new RpcEchoServer(new ThrowingEchoService()))
            .addRestHandler(ECHO_URI, new RestEchoServer(new EchoServiceImpl()))
            .addRestHandler(ON_EXCEPTION_ECHO_URI, new RestEchoServer(new OnExceptionEchoService()))
            .addRestHandler(THROWING_ECHO_URI, new RestEchoServer(new ThrowingEchoService()))
            .build();
  }
}
