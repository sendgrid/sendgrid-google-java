package com.sendgrid.google;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Invoked when a warning is returned from the server that
 * isn't critical
 */
interface WarningListener {
    void warning(String serverResponse, Throwable t);
}

/**
 * Send emails from Google App Engine through SendGrid using Java
 */
public class Sendgrid {

    private static final Logger LOGGER = Logger.getLogger(Sendgrid.class.getCanonicalName());

    private static final String UTF8 = StandardCharsets.UTF_8.toString();

    private static final String DOMAIN = "https://sendgrid.com/";
    private static final String ENDPOINT = "api/mail.send.json";

    private static final String CATEGORY = "google_sendgrid_java_lib";

    private boolean useHeaders = true;

    private String username;

    private String password;
    private String from;
    private String fromName;
    private String replyTo;
    private String subject;
    private String text;
    private String html;
    private String serverResponse = "";
    private ArrayList<String> toList = new ArrayList<>();
    private ArrayList<String> toNames = new ArrayList<>();
    private ArrayList<String> bccs = new ArrayList<>();
    private JSONObject headers = new JSONObject();

    public Sendgrid(String username, String password) {
        this.username = username;
        this.password = password;
        try {
            this.setCategory(CATEGORY);
        } catch (JSONException e) {
            LOGGER.info(e.getMessage());
        }
    }

    public void setUseHeaders(boolean useHeaders) {
        this.useHeaders = useHeaders;
    }

    /**
     * getTos - Return the list of recipients
     *
     * @return List of recipients
     */
    public ArrayList<String> getTos() {
        return this.toList;
    }

    /**
     * setTo - Initialize a single email for the recipient 'to' field
     * Destroy previous recipient 'to' data.
     *
     * @param email A list of email addresses
     * @return The SendGrid object.
     */
    public Sendgrid setTo(String email) {
        this.toList = new ArrayList<>();
        this.addTo(email);

        return this;
    }

    /**
     * addTo - Append an email address to the existing list of addresses
     * Preserve previous recipient 'to' data.
     *
     * @param email Recipient email address
     * @param name  Recipient name
     * @return The SendGrid object.
     */
    public Sendgrid addTo(String email, String name) {
        if (this.useHeaders()) {
            String toAddress = (name.length() > 0) ? name + "<" + email + ">" : email;
            this.toList.add(toAddress);
        } else {
            if (!name.trim().isEmpty()) {
                this.addToName(name);
            } else {
                this.addToName("");
            }
            this.toList.add(email);
        }

        return this;
    }

    /**
     * addTo - Make the second parameter("name") of "addTo" method optional
     *
     * @param email A single email address
     * @return The SendGrid object.
     */
    public Sendgrid addTo(String email) {
        return addTo(email, "");
    }

    /**
     * getTos - Return the list of names for recipients
     *
     * @return List of names
     */
    public ArrayList<String> getToNames() {
        return this.toNames;
    }

    /**
     * getFrom - Get the from email address
     *
     * @return The from email address
     */
    public String getFrom() {
        return this.from;
    }

    /**
     * setFrom - Set the from email
     *
     * @param email An email address
     * @return The SendGrid object.
     */
    public Sendgrid setFrom(String email) {
        this.from = email;

        return this;
    }

    /**
     * getFromName - Get the from name
     *
     * @return The from name
     */
    public String getFromName() {
        return this.fromName;
    }

    /**
     * setFromName - Set the from name
     *
     * @param name The name
     * @return The SendGrid object.
     */
    public Sendgrid setFromName(String name) {
        this.fromName = name;

        return this;
    }

    /**
     * getReplyTo - Get reply to address
     *
     * @return the reply to address
     */
    public String getReplyTo() {
        return this.replyTo;
    }

    /**
     * setReplyTo - set the reply-to address
     *
     * @param email the email to reply to
     * @return the SendGrid object.
     */
    public Sendgrid setReplyTo(String email) {
        this.replyTo = email;

        return this;
    }

    /**
     * getBccs - return the list of Blind Carbon Copy recipients
     *
     * @return ArrayList - the list of Blind Carbon Copy recipients
     */
    public ArrayList<String> getBccs() {
        return this.bccs;
    }

    /**
     * setBcc - Initialize the list of Carbon Copy recipients
     * destroy previous recipient Blind Carbon Copy data
     *
     * @param email an email address
     * @return the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid setBcc(String email) throws JSONException {
        this.bccs = new ArrayList<String>();
        this.bccs.add(email);
        if (this.useHeaders()) {
            this.addFilterSetting("bcc", "enable", "1");
            this.addFilterSetting("bcc", "email", email);
        }

        return this;
    }

    /**
     * getSubject - Get the email subject
     *
     * @return The email subject
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * setSubject - Set the email subject
     *
     * @param subject The email subject
     * @return The SendGrid object
     */
    public Sendgrid setSubject(String subject) {
        this.subject = subject;

        return this;
    }

    /**
     * getText - Get the plain text part of the email
     *
     * @return the plain text part of the email
     */
    public String getText() {
        return this.text;
    }

    /**
     * setText - Set the plain text part of the email
     *
     * @param text The plain text of the email
     * @return The SendGrid object.
     */
    public Sendgrid setText(String text) {
        this.text = text;

        return this;
    }

    /**
     * getHtml - Get the HTML part of the email
     *
     * @return The HTML part of the email.
     */
    public String getHtml() {
        return this.html;
    }

    /**
     * setHTML - Set the HTML part of the email
     *
     * @param html The HTML part of the email
     * @return The SendGrid object.
     */
    public Sendgrid setHtml(String html) {
        this.html = html;

        return this;
    }

    /**
     * setCategories - Set the list of category headers
     * destroys previous category header data
     *
     * @param category_list the list of category values
     * @return the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid setCategories(String[] category_list) throws JSONException {
        JSONArray categories_json = new JSONArray(category_list);
        this.headers.put("category", categories_json);
        this.addCategory(CATEGORY);

        return this;
    }

    /**
     * setCategory - Clears the category list and adds the given category
     *
     * @param category the new category to append
     * @return the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid setCategory(String category) throws JSONException {
        JSONArray json_category = new JSONArray(new String[]{category});
        this.headers.put("category", json_category);
        this.addCategory(CATEGORY);

        return this;
    }

    /**
     * addCategory - Append a category to the list of categories
     *
     * @param category the new category to append
     * @return the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid addCategory(String category) throws JSONException {
        if (this.headers.has("category")) {
            ((JSONArray) this.headers.get("category")).put(category);
        } else {
            this.setCategory(category);
        }

        return this;
    }

    /**
     * setSubstitutions - Substitute a value for list of values, where each value corresponds
     * to the list emails in a one to one relationship. (IE, value[0] = email[0],
     * value[1] = email[1])
     *
     * @param key_value_pairs key/value pairs where the value is an array of values
     * @return the SendGrid object.
     */
    public Sendgrid setSubstitutions(JSONObject key_value_pairs) {
        try {
            this.headers.put("sub", key_value_pairs);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return this;
    }

    /**
     * addSubstitution - Substitute a value for list of values, where each value corresponds
     * to the list emails in a one to one relationship. (IE, value[0] = email[0],
     * value[1] = email[1])
     *
     * @param from_value  the value to be replaced
     * @param to_values an array of values to replace the from_value
     * @return the SendGrid object.
     * @throws JSONException
     * @throws JSONException
     */
    public Sendgrid addSubstitution(String from_value, String[] to_values) throws JSONException {
        if (!this.headers.has("sub")) {
            this.headers.put("sub", new JSONObject());
        }
        JSONArray json_values = new JSONArray(to_values);
        ((JSONObject) this.headers.get("sub")).put(from_value, json_values);

        return this;
    }

    /**
     * setSection - Set a list of section values
     *
     * @param key_value_pairs key/value pairs
     * @return the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid setSections(JSONObject key_value_pairs) throws JSONException {
        this.headers.put("section", key_value_pairs);

        return this;
    }

    /**
     * addSection - append a section value to the list of section values
     *
     * @param from_value the value to be replaced
     * @param to_value   the value to replace
     * @return the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid addSection(String from_value, String to_value) throws JSONException {
        if (!this.headers.has("section")) {
            this.headers.put("section", new JSONObject());
        }
        ((JSONObject) this.headers.get("section")).put(from_value, to_value);

        return this;
    }

    /**
     * setUniqueArguments - Set a list of unique arguments, to be used for tracking purposes
     *
     * @param key_value_pairs - list of unique arguments
     */
    public Sendgrid setUniqueArguments(JSONObject key_value_pairs) {
        try {
            this.headers.put("unique_args", key_value_pairs);
        } catch (JSONException e) {
            LOGGER.info(e.getMessage());
        }

        return this;
    }

    /**
     * addUniqueArgument - Set a key/value pair of unique arguments, to be used for tracking purposes
     *
     * @param key   the key
     * @param value the value
     */
    public Sendgrid addUniqueArgument(String key, String value) {
        if (!this.headers.has("unique_args")) {
            try {
                this.headers.put("unique_args", new JSONObject());
            } catch (JSONException e) {
                LOGGER.info(e.getMessage());
            }
        }
        try {
            ((JSONObject) this.headers.get("unique_args")).put(key, value);
        } catch (JSONException e) {
            LOGGER.info(e.getMessage());
        }

        return this;
    }

    /**
     * setFilterSettings - Set filter/app settings
     *
     * @param filter_settings - JSONObject of fiter settings
     */
    public Sendgrid setFilterSettings(JSONObject filter_settings) {
        try {
            this.headers.put("filters", filter_settings);
        } catch (JSONException e) {
            LOGGER.info(e.getMessage());
        }

        return this;
    }

    /**
     * addFilterSetting - Append a filter setting to the list of filter settings
     *
     * @param filter_name     filter name
     * @param parameter_name  parameter name
     * @param parameter_value setting value
     * @throws JSONException
     */
    public Sendgrid addFilterSetting(String filter_name, String parameter_name, String parameter_value) throws JSONException {
        if (!this.headers.has("filters")) {
            this.headers.put("filters", new JSONObject());
        }
        if (!((JSONObject) this.headers.get("filters")).has(filter_name)) {
            ((JSONObject) this.headers.get("filters")).put(filter_name, new JSONObject());
        }
        if (!((JSONObject) ((JSONObject) this.headers.get("filters")).get(filter_name)).has("settings")) {
            ((JSONObject) ((JSONObject) this.headers.get("filters")).get(filter_name)).put("settings", new JSONObject());
        }
        ((JSONObject) ((JSONObject) ((JSONObject) this.headers.get("filters")).get(filter_name)).get("settings"))
                .put(parameter_name, parameter_value);

        return this;
    }

    /**
     * getHeaders - return the list of headers
     *
     * @return JSONObject with headers
     */
    public JSONObject getHeaders() {
        return this.headers;
    }

    /**
     * setHeaders - Sets the list headers
     * destroys previous header data
     *
     * @param key_value_pairs the list of header data
     * @return the SendGrid object.
     */
    public Sendgrid setHeaders(JSONObject key_value_pairs) {
        this.headers = key_value_pairs;

        return this;
    }

    /**
     * getServerResponse - Get the server response message
     *
     * @return The server response message
     */
    public String getServerResponse() {
        return this.serverResponse;
    }

    /**
     * arrayToUrlPart - Converts an ArrayList to a url friendly string
     *
     * @param array the array to convert
     * @param token the name of parameter
     * @return a url part that can be concatenated to a url request
     */
    protected String arrayToUrlPart(ArrayList<String> array, String token) {
        String string = "";
        for (String anArray : array) {
            try {
                string += "&" + token + "[]=" + URLEncoder.encode(anArray, UTF8);
            } catch (UnsupportedEncodingException e) {
                LOGGER.info(e.getMessage());
            }
        }

        return string;
    }

    /**
     * prepMessageData - Takes the mail message and returns a url friendly querystring
     *
     * @return the data query string to be posted
     * @throws JSONException
     */
    protected Map<String, String> prepMessageData() throws JSONException {
        Map<String, String> params = new HashMap<>();

        params.put("api_user", this.username);
        params.put("api_key", this.password);
        params.put("subject", this.getSubject());
        if (this.getHtml() != null) {
            params.put("html", this.getHtml());
        }
        if (this.getFromName() != null) {
            params.put("fromname", this.getFromName());
        }
        params.put("text", this.getText());
        params.put("from", this.getFrom());

        if (this.getReplyTo() != null) {
            params.put("replyto", this.getReplyTo());
        }

        if (this.useHeaders()) {
            JSONObject headers = this.getHeaders();
            params.put("to", this.getFrom());
            JSONArray tos_json = new JSONArray(this.getTos());
            headers.put("to", tos_json);
            this.setHeaders(headers);
            params.put("x-smtpapi", escapeUnicode(this.getHeaders().toString()));
        } else {
            params.put("to", this.getTos().toString());
            if (this.getToNames().size() > 0) {
                params.put("toname", this.getToNames().toString());
            }
        }

        return params;
    }

    /**
     * send - Send an email
     *
     * @throws JSONException
     */
    public void send() throws JSONException {
        send((w, t) -> serverResponse = w);
    }

    /**
     * send - Send an email
     *
     * @param listener callback that will receive warnings
     * @throws JSONException
     */
    public void send(WarningListener listener) throws JSONException {
        Map<String, String> data = this.prepMessageData();
        StringBuilder requestParams = new StringBuilder();
        for (String key : data.keySet()) {
            final String value = data.get(key);
            if (key.equals("to") && this.getTos().size() > 0) {
                if (this.useHeaders()) {
                    try {
                        requestParams.append("to=").append(URLEncoder.encode(value, UTF8)).append("&");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    requestParams.append(this.arrayToUrlPart(this.getTos(), "to")).append("&");
                }
            } else {
                if (key.equals("toname") && this.getToNames().size() > 0) {
                    requestParams.append(this.arrayToUrlPart(this.getToNames(), "toname").substring(1)).append("&");
                } else {
                    try {
                        requestParams.append(URLEncoder.encode(key, UTF8));
                    } catch (UnsupportedEncodingException e) {
                        listener.warning("Unsupported Encoding Exception", e);
                    }
                    requestParams.append("=");
                    try {
                        requestParams.append(URLEncoder.encode(value, UTF8));
                    } catch (UnsupportedEncodingException e) {
                        listener.warning("Unsupported Encoding Exception", e);
                    }
                    requestParams.append("&");
                }
            }
        }
        String request = DOMAIN + ENDPOINT;

        if (this.getBccs().size() > 0) {
            request += "?" + this.arrayToUrlPart(this.getBccs(), "bcc").substring(1);
        }
        try {
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(requestParams.toString());
            // Get the response
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line, response = "";

            while ((line = reader.readLine()) != null) {
                response += line;
            }
            reader.close();
            writer.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // OK
                serverResponse = "success";
            } else {
                // Server returned HTTP error code.
                JSONObject apiResponse = new JSONObject(response);
                JSONArray errorsObj = (JSONArray) apiResponse.get("errors");
                for (int i = 0; i < errorsObj.length(); i++) {
                    if (i != 0) {
                        serverResponse += ", ";
                    }
                    serverResponse += errorsObj.get(i);
                }
                listener.warning(serverResponse, null);
            }
        } catch (MalformedURLException e) {
            listener.warning("Malformed URL Exception", e);
        } catch (IOException e) {
            listener.warning("IO Exception", e);
        }
    }

    /**
     * addToName - Append an recipient name to the existing list of names
     *
     * @param name  Recipient name
     * @return The SendGrid object.
     */
    private Sendgrid addToName(String name) {
        this.toNames.add(name);

        return this;
    }

    /**
     * useHeaders - Checks to see whether or not we can or should you headers. In most cases,
     * we prefer to send our recipients through the headers, but in some cases,
     * we actually don't want to. However, there are certain circumstances in
     * which we have to.
     */
    private boolean useHeaders() {
        return (!(this.preferNotToUseHeaders() && !this.isHeadersRequired()));
    }

    /**
     * preferNotToUseHeaders - There are certain cases in which headers are not a preferred choice
     * to send email, as it limits some basic email functionality. Here, we
     * check for any of those rules, and add them in to decide whether or
     * not to use headers
     *
     * @return if true we don't
     */
    private Boolean preferNotToUseHeaders() {
        return (this.getBccs().isEmpty() || !this.useHeaders);
    }

    /**
     * isHeaderRequired - determines whether or not we need to force recipients through the smtpapi headers
     *
     * @return if true headers are required
     */
    private boolean isHeadersRequired() {
        return this.useHeaders;
    }

    private String escapeUnicode(String input) {
        StringBuilder sb = new StringBuilder();
        int len = input.length();
        for (int i = 0; i < len; i++) {
            int code = Character.codePointAt(input, i);
            if (code > 127) {
                sb.append(String.format("\\u%x", code));
            } else {
                sb.append(String.format("%c", code));
            }
        }
        return sb.toString();
    }

}
