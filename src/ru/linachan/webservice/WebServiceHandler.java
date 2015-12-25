package ru.linachan.webservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.linachan.yggdrasil.YggdrasilCore;

import java.io.IOException;
import java.net.Socket;

public abstract class WebServiceHandler implements Runnable {

    protected YggdrasilCore core;
    private Thread handlerThread;
    private Socket clientSocket;

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

    protected abstract WebServiceResponse handleRequest(WebServiceRequest request);

    public boolean isAlive() {
        return handlerThread.isAlive();
    }

    public void start() {
        handlerThread.start();
    }

    public void setUp(YggdrasilCore yggdrasilCore, Socket sock) {
        core = yggdrasilCore;
        clientSocket = sock;
        handlerThread = new Thread(this);
    }

    public void join() throws InterruptedException {
        handlerThread.join();
    }
}
