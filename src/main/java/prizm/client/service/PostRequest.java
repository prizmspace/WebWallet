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

public abstract class PostRequest {
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
    
    public PostRequest(String URL) {
        requestBuilder = new RequestBuilder(RequestBuilder.POST, URL);
        requestBuilder.setHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        requestBuilder.setHeader("Accept","application/json, text/javascript, */*; q=0.01");
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
        Request response = requestBuilder.sendRequest( sb.toString(), new RequestCallback() {

            public void onError(Request request, Throwable exception) {
                PostRequest.this.onError();
            }

            public void onResponseReceived(Request request, Response response) {
                PostRequest.this.onSuccess(response.getText());
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
