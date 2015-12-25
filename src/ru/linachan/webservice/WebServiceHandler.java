package ru.linachan.webservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.linachan.yggdrasil.YggdrasilCore;

import java.io.IOException;
import java.net.Socket;

public class WebServiceHandler implements Runnable {

    protected YggdrasilCore core;
    private Thread handlerThread;
    private Socket clientSocket;
    private WebServiceRouter router;

    private static Logger logger = LoggerFactory.getLogger(WebServiceHandler.class);

    @Override
    public void run() {
        try {
            WebServiceRequest request = WebServiceRequest.readFromSocket(clientSocket);
            WebServiceResponse response = (request != null) ? handleRequest(request) : null;
            WebServiceResponse.writeToSocket(response, clientSocket);

            clientSocket.close();
        } catch (IOException e) {
            logger.error("Unable to process client request", e);
        }
    }

    private WebServiceResponse handleRequest(WebServiceRequest request) {
        WebServiceRoute route = router.route(request.getUri());
        route.setUp(core);
        return route.handle(request);
    }

    public boolean isAlive() {
        return handlerThread.isAlive();
    }

    public void start() {
        handlerThread.start();
    }

    public void setUp(YggdrasilCore yggdrasilCore, Class<? extends WebServiceRouter> routerClass, Socket sock) {
        core = yggdrasilCore;
        clientSocket = sock;
        handlerThread = new Thread(this);
        try {
            router = routerClass.newInstance();
            router.setUpRoutes();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Unable to instantiate router", e);
        }
    }

    public void join() throws InterruptedException {
        handlerThread.join();
    }
}
