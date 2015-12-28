package ru.linachan.webservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.linachan.webservice.utils.NotFoundRoute;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class WebServiceRouter {

    protected Map<Pattern, Class<? extends WebServiceRoute>> routes = new HashMap<>();
    private static Logger logger = LoggerFactory.getLogger(WebServiceRouter.class);

    public WebServiceRoute route(String uri) {
        try {
            for (Pattern pattern : routes.keySet()) {
                if (pattern.matcher(uri).matches()) {
                    WebServiceRoute route =  routes.get(pattern).newInstance();
                    route.setPattern(pattern);
                    return route;
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Unable to instantiate router", e);
        }

        return new NotFoundRoute();
    }

    public abstract void setUpRoutes();

    protected void addRoute(String uriRegEx, Class<? extends WebServiceRoute> route) {
        routes.put(Pattern.compile(uriRegEx), route);
    }
}
