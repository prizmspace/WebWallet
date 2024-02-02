package prizm.shared;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import prizm.client.pojo.Balance;

public class Request {
    
    private final static String REQUEST_URI = "http://localhost:9976/prizm";
    private final static String USER_AGENT = "Mozilla/5.0";
    
    private  static String sendGet(String url) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		return response.toString();

	}
    
    public static Balance get() throws Exception {

        String resString = sendGet(REQUEST_URI + "?requestType=getBalance&account=8562459348922351959");
        Gson gson = new Gson();
        Balance balance = gson.fromJson(resString, Balance.class);
        
        return balance;
    }
}
