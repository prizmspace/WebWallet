package prizm.client.service;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class GetRequest {
    private RequestBuilder requestBuilder;
    private HashMap<String, String> values = new HashMap<String, String>();

    private RequestCallback callback = new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
            onSuccess(response.getText());
        }

        @Override
        public void onError(Request request, Throwable exception) {
            
        }
    };
    
    private String URLString;
    
    public GetRequest(String URL) {
        this.URLString = URL;
    }

    private static Random rnd = new Random();
    
    public void action() {
        if (!values.containsKey("rnd")) {
            values.put("rnd", ""+rnd.nextLong());
        }        
        StringBuilder sb = new StringBuilder();
        for ( Map.Entry<String, String> item : values.entrySet()) {
            String vx = URL.encodeComponent(item.getValue());
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(item.getKey()).append("=").append(vx);
        }
        
    try {
        requestBuilder = new RequestBuilder(RequestBuilder.GET, URLString+"?"+sb.toString());
        requestBuilder.setHeader("X-Content-Type-Options", "nosniff");
        requestBuilder.setHeader("Access-Control-Allow-Origin", "*");
        requestBuilder.setHeader("Access-Control-Allow-Credentials", "true");
        requestBuilder.setHeader("Access-Control-Allow-Headers", "origin, x-requested-with, content-type");
        requestBuilder.setHeader("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
        Request response = requestBuilder.sendRequest(null, new RequestCallback() {

            public void onError(Request request, Throwable exception) {
                GetRequest.this.onError();
            }

            public void onResponseReceived(Request request, Response response) {
                GetRequest.this.onSuccess(response.getText());
            }
        });
    } catch (RequestException e) {}        
    }

    public HashMap<String, String> getValues() {
        return values;
    }
    
    public abstract void onSuccess(String result);
    
    public abstract void onError();
}
