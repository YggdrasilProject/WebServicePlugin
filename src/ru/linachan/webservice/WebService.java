package ru.linachan.webservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.linachan.tcpserver.TCPService;
import ru.linachan.yggdrasil.YggdrasilCore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WebService implements TCPService {

    protected YggdrasilCore core;
    private WebServiceRouter router;

    private static Logger logger = LoggerFactory.getLogger(WebService.class);

    public WebService(WebServiceRouter router) {
        this.router = router;
        this.router.setUpRoutes();
    }

    public WebService(Class<? extends WebServiceRouter> routerClass) throws IllegalAccessException, InstantiationException {
        this.router = routerClass.newInstance();
        this.router.setUpRoutes();
    }

    @Override
    public void handleConnection(YggdrasilCore core, InputStream in, OutputStream out) {
        try {
            WebServiceRequest request = WebServiceRequest.readFromSocket(in);
            WebServiceResponse response = (request != null) ? handleRequest(request) : null;
            WebServiceResponse.writeToSocket(response, out);
        } catch (IOException e) {
            logger.error("Unable to process client request", e);
        }
    }

    private WebServiceResponse handleRequest(WebServiceRequest request) {
        WebServiceRoute route = router.route(request.getUri());
        route.setUp(core);
        return route.handle(request);
    }
}
