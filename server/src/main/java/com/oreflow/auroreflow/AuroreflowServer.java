package com.oreflow.auroreflow;

import com.google.inject.Injector;
import com.oreflow.auroreflow.websocket.LightbulbEndpoint;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Arrays;
import java.util.EventListener;
import java.util.Iterator;

public class AuroreflowServer implements EventListener {
  private static int port = 8080;

  private static void parseArgs(String [] args) {
    Iterator<String> iterator = Arrays.asList(args).iterator();
    while(iterator.hasNext()) {
      String arg = iterator.next();
      switch (arg) {
        case "-port":
          port = Integer.parseInt(iterator.next());
      }
    }
  }

  public static void main(String [] args) throws Exception {
    parseArgs(args);
    AuroreflowServletContextListener listener = new AuroreflowServletContextListener();
    Injector injector = listener.getInjector();

    Server server = new Server(port);
    WebAppContext webAppContext = new WebAppContext("./src/main/webapp","/");
    webAppContext.setServer(server);
    webAppContext.setWelcomeFiles(new String[]{"index.html"});
    webAppContext.addEventListener(listener);

    ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(webAppContext);
    wscontainer.addEndpoint(
        ServerEndpointConfig.Builder
            .create(LightbulbEndpoint.class, "/lightbulbupdates")
            .configurator(new AuroreflowConfigurator(injector))
            .build());
    server.setHandler(webAppContext);

    server.start();
  }
}
