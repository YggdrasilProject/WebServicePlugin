package ru.linachan.webservice;

import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class WebServiceResponse {

    private WebServiceHTTPCode httpCode;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();
    private byte[] body = null;

    public WebServiceResponse(WebServiceHTTPCode statusCode) {
        httpCode = statusCode;

        headers.put("Connection", "close");
    }

    public static void writeToSocket(WebServiceResponse response, Socket clientSocket) throws IOException {
        if (response == null) {
            response = new WebServiceResponse(WebServiceHTTPCode.BAD_REQUEST);
        }

        BufferedWriter responseWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        responseWriter.write(String.format("HTTP/1.0 %s\r\n", response.getCode())); responseWriter.flush();

        for (Map.Entry<String, String> header: response.getHeaders().entrySet()) {
            responseWriter.write(String.format("%s: %s\r\n", header.getKey(), header.getValue()));
            responseWriter.flush();
        }

        if ((response.getBody() != null)&&(response.getBody().length > 0)) {
            responseWriter.write(String.format("Content-Length: %s\r\n", response.getBody().length));
            responseWriter.flush();
        }

        for (Map.Entry<String, String> cookie: response.getCookies().entrySet()) {
            responseWriter.write(String.format("Set-Cookie: %s=%s\r\n", cookie.getKey(), cookie.getValue()));
            responseWriter.flush();
        }

        responseWriter.newLine(); responseWriter.flush();

        if ((response.getBody() != null)&&(response.getBody().length > 0)) {
            clientSocket.getOutputStream().write(response.getBody());
            clientSocket.getOutputStream().flush();
        }

        clientSocket.getOutputStream().flush();
        clientSocket.getOutputStream().close();
    }

    private String getCode() {
        return httpCode.getCode();
    }

    private Map<String, String> getHeaders() {
        return headers;
    }

    private Map<String, String> getCookies() {
        return cookies;
    }

    private byte[] getBody() {
        return body;
    }

    public void setBinaryBody(byte[] binaryData) {
        body = binaryData;
    }

    public void setHTMLBody(String htmlData) {
        setContentType("text/html");
        body = htmlData != null ? htmlData.getBytes() : new byte[0];
    }

    public void setJSONBody(JSONObject jsonData) {
        setContentType("application/json");
        JSONObject jsonObject = (jsonData != null) ? jsonData : new JSONObject();
        body = jsonObject.toJSONString().getBytes();
    }

    public void setHeader(String header, String value) {
        headers.put(header, value);
    }

    public void setCookie(String cookie, String value) {
        cookies.put(cookie, value);
    }

    public void setContentType(String contentType) {
        headers.put("Content-Type", contentType);
    }
}
