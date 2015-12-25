package ru.linachan.webservice;

import ru.linachan.yggdrasil.YggdrasilCore;

public abstract class WebServiceRoute {

    protected YggdrasilCore core;

    public void setUp(YggdrasilCore yggdrasilCore) {
        core = yggdrasilCore;
    }

    public WebServiceResponse handle(WebServiceRequest request) {
        WebServiceResponse response = null;

        switch (request.getMethod()) {
            case "OPTIONS":
                response = OPTIONS(request);
                break;
            case "HEAD":
                response = HEAD(request);
                break;
            case "GET":
                response = GET(request);
                break;
            case "POST":
                response = POST(request);
                break;
            case "PUT":
                response = PUT(request);
                break;
            case "PATCH":
                response = PATCH(request);
                break;
            case "DELETE":
                response = DELETE(request);
                break;
        }

        return (response != null) ? response : new WebServiceResponse(WebServiceHTTPCode.METHOD_NOT_ALLOWED);
    }

    protected abstract WebServiceResponse HEAD(WebServiceRequest request);

    protected abstract WebServiceResponse OPTIONS(WebServiceRequest request);

    protected abstract WebServiceResponse GET(WebServiceRequest request);

    protected abstract WebServiceResponse POST(WebServiceRequest request);

    protected abstract WebServiceResponse PUT(WebServiceRequest request);

    protected abstract WebServiceResponse PATCH(WebServiceRequest request);

    protected abstract WebServiceResponse DELETE(WebServiceRequest request);

}
