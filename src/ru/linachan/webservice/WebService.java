package ru.linachan.webservice;

import ru.linachan.yggdrasil.service.YggdrasilService;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.List;

public abstract class WebService extends YggdrasilService {

    private ServerSocket webServiceSocket;
    private SocketAddress webServiceBindAddress;

    private List<WebServiceHandler> webServiceThreads = new LinkedList<>();
    private Class<? extends WebServiceRouter> webServiceRouter;

    @Override
    protected void onInit() {
        setUpWebService();
        if (webServiceBindAddress != null) {
            try {
                webServiceSocket = new ServerSocket();
                webServiceSocket.bind(webServiceBindAddress);
                webServiceSocket.setSoTimeout(1000);
            } catch (IOException e) {
                logger.error("Unable to create socket", e);
            }
        } else {
            logger.warn("WebService instance not configured");
        }
    }

    @Override
    protected void onShutdown() {
        try {
            if (webServiceSocket != null) {
                webServiceSocket.close();
            }
        } catch (IOException e) {
            logger.error("Unable to close socket", e);
        }
    }

    @Override
    public void run() {
        while (isRunning()) {
            List<WebServiceHandler> finishedServiceThreads = new LinkedList<>();
            for (WebServiceHandler serviceThread : webServiceThreads) {
                if (!serviceThread.isAlive()) {
                    try {
                        serviceThread.join();
                    } catch (InterruptedException e) {
                        logger.error("Unable to finish service thread correctly", e);
                    }
                    finishedServiceThreads.add(serviceThread);
                }
            }

            for (WebServiceHandler finishedServiceThread : finishedServiceThreads) {
                webServiceThreads.remove(finishedServiceThread);
            }

            try {
                Socket sock = webServiceSocket.accept();

                WebServiceHandler clientHandler = new WebServiceHandler();

                clientHandler.setUp(core, webServiceRouter, sock);
                clientHandler.start();

                webServiceThreads.add(clientHandler);
            } catch (SocketException | SocketTimeoutException ignored) {
                // Do nothing
            } catch (IOException e) {
                logger.error("Unable to handle client request", e);
            }
        }
    }

    protected void setBindAddress(SocketAddress bindAddress) {
        webServiceBindAddress = bindAddress;
    }

    protected abstract void setUpWebService();

    public void setRouter(Class<? extends WebServiceRouter> routerClass) {
        webServiceRouter = routerClass;
    }
}
