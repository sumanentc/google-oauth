package com.fractal.oauth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by suman.das on 11/2/16.
 */
@Controller
public class Oauth2callback  {
    private static final long serialVersionUID = 1L;

    /**
     * Please provide a value for the CLIENT_ID constant before proceeding
     */
    private static final String CLIENT_ID = "48143850777-mmi2o4rlcll2qlbl1e3uu18gd3hktlij.apps.googleusercontent.com";
    /**
     * Please provide a value for the CLIENT_SECRET constant before proceeding
     */
    private static final String CLIENT_SECRET = "JL0ab5v6bFnoisqva4z98zc9";

    /**
     * Callback URI that google will redirect to after successful authentication
     */
    private static final String CALLBACK_URI = "http://localhost:8080/oauth2callback";

    // start google authentication constants
    private static final Collection<String> SCOPE = Arrays.asList("https://www.googleapis.com/auth/userinfo.profile;https://www.googleapis.com/auth/userinfo.email".split(";"));
    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private  String SUCCESS_URL="/welcome";
    private  String FAILURE_URL;
    // end google authentication constants

    private String stateToken;

    private final GoogleAuthorizationCodeFlow flow;

    public Oauth2callback() {
        super();
        flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPE).build();
        generateStateToken();

    }

    /**
     * Generates a secure state token
     */
    private void generateStateToken(){

        SecureRandom sr1 = new SecureRandom();

        stateToken = "google;"+sr1.nextInt();

    }


    @RequestMapping(value="/oauth2callback",method = RequestMethod.GET)
    protected String doGet(HttpServletRequest request,
                         HttpServletResponse response,RedirectAttributes redirectAttributes) throws ServletException, IOException {

        System.out.println("entering doGet");
        try {
            // get code
            String code = request.getParameter("code");
            FAILURE_URL = request.getParameter("state");
            if(code == null){
                buildLoginUrl();
                response.sendRedirect(FAILURE_URL);
                return "forward:"+FAILURE_URL;
            }else {
                String error = request.getParameter("error");
                if (error != null) {
                    buildLoginUrl();
                    response.sendRedirect(FAILURE_URL);
                    return "forward:"+FAILURE_URL;
                }
            }

            //String access_token=getToken(code);
            //GooglePojo data = getUserInfo(access_token);
            GooglePojo data = getUserInfoJson(code);
            populateAttributes(data,redirectAttributes,SUCCESS_URL);

        } catch (MalformedURLException e) {
            response.sendRedirect(FAILURE_URL);
            e.printStackTrace();
        } catch (ProtocolException e) {
            response.sendRedirect(FAILURE_URL);
            e.printStackTrace();
        } catch (IOException e) {
            response.sendRedirect(FAILURE_URL);
            e.printStackTrace();
        }
        System.out.println("leaving doGet");
        return "redirect:/response";
    }

    /**
     * Expects an Authentication Code, and makes an authenticated request for the user's profile information
     * @return GooglePojo formatted user profile information
     * @param authCode authentication code provided by google
     */
    private GooglePojo getUserInfoJson(final String authCode) throws IOException {


        System.out.println("Code : " + authCode);
        final GoogleTokenResponse response = flow.newTokenRequest(authCode).setRedirectUri(CALLBACK_URI).execute();
        final Credential credential = flow.createAndStoreCredential(response, null);
        final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
        // Make an authenticated request
        final GenericUrl url = new GenericUrl(USER_INFO_URL);
        final HttpRequest request = requestFactory.buildGetRequest(url);
        request.getHeaders().setContentType("application/json");
        final String jsonIdentity = request.execute().parseAsString();

        System.out.println(jsonIdentity);

        GooglePojo data = new Gson().fromJson(jsonIdentity, GooglePojo.class);
        System.out.println(data);

        return data;

    }

    /**
     * Builds a login URL based on client ID, secret, callback URI, and scope
     */
    private String buildLoginUrl() {

        final GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();

        return url.setRedirectUri(CALLBACK_URI).setState(stateToken).build();
    }

    private void populateAttributes(GooglePojo data,RedirectAttributes redirectAttributes,String url){
        if(data != null){
            redirectAttributes.addFlashAttribute("user",data.getName());
        }
        redirectAttributes.addFlashAttribute("url",url);

    }
    /*
    private String getToken(String code) throws IOException{
        // format parameters to post
        String urlParameters = "code="
                + code
                + "&client_id=48143850777-mmi2o4rlcll2qlbl1e3uu18gd3hktlij.apps.googleusercontent.com"
                + "&client_secret=JL0ab5v6bFnoisqva4z98zc9"
                + "&redirect_uri=http://localhost:8080/oauth2callback"
                + "&grant_type=authorization_code";

        //post parameters
        URL url = new URL("https://accounts.google.com/o/oauth2/token");
        URLConnection urlConn = url.openConnection();
        urlConn.setDoOutput(true);
        OutputStreamWriter writer=null;
        BufferedReader reader=null;
        String access_token=null;
        try {
             writer = new OutputStreamWriter(urlConn.getOutputStream());
             writer.write(urlParameters);
             writer.flush();

            //get output in outputString
            String line, outputString = "";
            reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                outputString += line;
            }
            System.out.println(outputString);

            //get Access Token
            JsonObject json = (JsonObject) new JsonParser().parse(outputString);
            access_token = json.get("access_token").getAsString();
            System.out.println(access_token);
        }finally {
            if(writer!=null){
                writer.close();
            }
            if(reader!=null){
                reader.close();
            }
        }

        return access_token;

    }

    private GooglePojo getUserInfo(String access_token) throws IOException {

        //get User Info
        URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token="+ access_token);
        URLConnection urlConn = url.openConnection();
        String line,outputString = "";
        BufferedReader reader=null;
        GooglePojo data=null;
        try {
            reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                outputString += line;
            }
            System.out.println(outputString);

            // Convert JSON response into Pojo class
            data = new Gson().fromJson(outputString, GooglePojo.class);
            System.out.println(data);
        }finally{
            if(reader!=null){
              reader.close();
            }
        }

        return data;

    }
    */


}
