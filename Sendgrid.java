package googleSendgridJava;

import java.net.HttpURLConnection;
import java.util.*;
import java.io.IOException;
import java.util.Iterator;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.labs.repackaged.org.json.JSONArray;


public class Sendgrid {
    
    private String from,
                   reply_to,
                   subject,
                   text,
                   html;
    protected Boolean use_headers = true;
    public String message = "";
    private ArrayList<String> to_list  = new ArrayList<String>();
    private ArrayList<String> bcc_list = new ArrayList<String>();
    private JSONObject header_list = new JSONObject();

    protected String domain = "http://sendgrid.com/",
                     endpoint= "api/mail.send.json",
                     username,
                     password;

    Sendgrid(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * getTos - Return the list of recipients
     *
     * @return  List of recipients
     */
    public ArrayList<String> getTos() {
        return this.to_list;
    }

    /**
     * setTo - Initialize a single email for the recipient 'to' field
     * Destroy previous recipient 'to' data.
     *
     * @param    email   A list of email addresses
     * @return           The SendGrid object.
     */
    public Sendgrid setTo(String email) {
        this.to_list = new ArrayList<String>();
        this.to_list.add(email);

        return this;
    }

    /**
     * addTo - Append an email address to the existing list of addresses
     * Preserve previous recipient 'to' data.
     *
     * @param    email   Recipient email address
     * @param    name    Recipient name
     * @return           The SendGrid object.
     */
    public Sendgrid addTo(String email, String name) {
        String toAddress = (name.length() > 0) ? name + "<" + email + ">" : email;
        this.to_list.add(toAddress);

        return this;
    }

    /**
     * addTo - Make the second parameter("name") of "addTo" method optional
     *
     * @param   email   A single email address 
     * @return          The SendGrid object.  
     */
    public Sendgrid addTo(String email) {
        return addTo(email, "");
    }

    /**
     * getFrom - Get the from email address
     *
     * @return  The from email address
     */
    public String getFrom() {
        return this.from;
    }

    /**
     * setFrom - Set the from email
     *
     * @param    email   An email address
     * @return           The SendGrid object.
     */
    public Sendgrid setFrom(String email) {
        this.from = email;

        return this;
    }

    /**
     * getReplyTo - Get reply to address
     *
     * @return the reply to address
     */
    public String getReplyTo() {
        return this.reply_to;
    }

    /**
     * setReplyTo - set the reply-to address
     *
     * @param  email   the email to reply to
     * @return         the SendGrid object.
     */
    public Sendgrid setReplyTo(String email) {
      this.reply_to = email;

      return this;
    }

    /**
     * getBccs - return the list of Blind Carbon Copy recipients
     *
     * @return ArrayList - the list of Blind Carbon Copy recipients
     */
    public ArrayList<String> getBccs() {
        return this.bcc_list;
    }

    /**
     * setBcc - Initialize the list of Carbon Copy recipients
     * destroy previous recipient Blind Carbon Copy data
     *
     * @param  email   an email address
     * @return         the SendGrid object.
     */
    public Sendgrid setBcc(String email) {
      this.bcc_list = new ArrayList<String>();
      this.bcc_list.add(email);

      return this;
    }

    /**
     * addBcc - Append an email address to the list of Blind Carbon Copy
     * recipients
     *
     * @param email - an email address
     */
    public Sendgrid addBcc(String email) {
        if (this.bcc_list.size() > 0) {
            this.bcc_list.add(email);
        } else {
            this.setBcc(email);
        }

        return this;
    }

    /** 
     * getSubject - Get the email subject
     * 
     * @return  The email subject
     */
    public String getSubject() {
        return this.subject;
    }

    /** 
     * setSubject - Set the email subject
     * 
     * @param    subject   The email subject
     * @return             The SendGrid object
     */
    public Sendgrid setSubject(String subject) {
        this.subject = subject;
      
        return this;
    }

    /** 
     * getText - Get the plain text part of the email
     * 
     * @return   the plain text part of the email
     */
    public String getText() {
        return this.text;
    }

    /** 
     * setText - Set the plain text part of the email
     * 
     * @param   text   The plain text of the email
     * @return         The SendGrid object.
     */
    public Sendgrid setText(String text) {
        this.text = text;

        return this;
    }
    
    /** 
     * getHtml - Get the HTML part of the email
     * 
     * @return   The HTML part of the email.
     */
    public String getHtml() {
        return this.html;
    }

    /** 
     * setHTML - Set the HTML part of the email
     * 
     * @param   html   The HTML part of the email
     * @return         The SendGrid object.
     */
    public Sendgrid setHtml(String html) {
        this.html = html;

        return this;
    }

    /**
     * setCategories - Set the list of category headers
     * destroys previous category header data
     *
     * @param  category_list   the list of category values
     * @return                 the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid setCategories(String[] category_list) throws JSONException {
        JSONArray categories_json = new JSONArray(category_list);
        this.header_list.put("category", categories_json);

        return this;
    }

    /**
     * setCategory - Clears the category list and adds the given category
     *
     * @param  category   the new category to append
     * @return            the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid setCategory(String category) throws JSONException {
        JSONArray json_category = new JSONArray(new String[]{category});
        this.header_list.put("category", json_category);

        return this;
    }

    /**
     * addCategory - Append a category to the list of categories
     *
     * @param  category   the new category to append
     * @return            the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid addCategory(String category) throws JSONException {
        if (true == this.header_list.has("category")) {
            ((JSONArray) this.header_list.get("category")).put(category);
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
     * @param  key_value_pairs   key/value pairs where the value is an array of values
     * @return                   the SendGrid object.
     */
    public Sendgrid setSubstitutions(JSONObject key_value_pairs) {
        try {
            this.header_list.put("sub", key_value_pairs);
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
     * @param  from_key    the value to be replaced
     * @param  to_values   an array of values to replace the from_value
     * @return             the SendGrid object.
     * @throws JSONException
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public Sendgrid addSubstitution(String from_value, String[] to_values) throws JSONException {
      if (false == this.header_list.has("sub")) {
        this.header_list.put("sub", new JSONObject());
      }
      JSONArray json_values = new JSONArray(to_values);
      ((JSONObject) this.header_list.get("sub")).put(from_value, json_values);

      return this;
    }

    /**
     * setSection - Set a list of section values
     *
     * @param  key_value_pairs   key/value pairs
     * @return                   the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid setSections(JSONObject key_value_pairs) throws JSONException {
        this.header_list.put("section", key_value_pairs);

        return this;
    }

    /**
     * addSection - append a section value to the list of section values
     *
     * @param  from_value  the value to be replaced
     * @param  to_value    the value to replace
     * @return             the SendGrid object.
     * @throws JSONException
     */
    public Sendgrid addSection(String from_value, String to_value) throws JSONException {
        if (false == this.header_list.has("section")) {
          this.header_list.put("section", new JSONObject() );
        }
        ((JSONObject) this.header_list.get("section")).put(from_value, to_value);

        return this;
    }

    /**
     * setUniqueArguments - Set a list of unique arguments, to be used for tracking purposes
     *
     * @param key_value_pairs - list of unique arguments
     */
    public Sendgrid setUniqueArguments(JSONObject key_value_pairs) {
        try {
            this.header_list.put("unique_args", key_value_pairs);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return this;
    }

    /**
     * addUniqueArgument - Set a key/value pair of unique arguments, to be used for tracking purposes
     *
     * @param key     the key
     * @param value   the value
     */
    public Sendgrid addUniqueArgument(String key, String value) {
        if (false == this.header_list.has("unique_args")) {
          try {
              this.header_list.put("unique_args", new JSONObject());
          } catch (JSONException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          }
        }
        try {
            ((JSONObject) this.header_list.get("unique_args")).put(key, value);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
            this.header_list.put("filters", filter_settings);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return this;
    }

    /** 
     * addFilterSetting - Append a filter setting to the list of filter settings
     *
     * @param  filter_name       filter name
     * @param  parameter_name    parameter name
     * @param  parameter_value   setting value
     * @throws JSONException
     */
    public Sendgrid addFilterSetting(String filter_name, String parameter_name, String parameter_value) throws JSONException {
        if (false == this.header_list.has("filters")) {
            this.header_list.put("filters", new JSONObject());
        }
        if (false == ((JSONObject) this.header_list.get("filters")).has(filter_name)) {
            ((JSONObject) this.header_list.get("filters")).put(filter_name, new JSONObject());
        }
        if (false == ((JSONObject) ((JSONObject) this.header_list.get("filters")).get(filter_name)).has("settings")) {
            ((JSONObject) ((JSONObject) this.header_list.get("filters")).get(filter_name)).put("settings", new JSONObject());
        }
        ((JSONObject) ((JSONObject) ((JSONObject) this.header_list.get("filters")).get(filter_name)).get("settings"))
            .put(parameter_name, parameter_value);

        return this;
    }

    /**
     * getHeaders - return the list of headers
     *
     * @return JSONObject with headers
     */
    public JSONObject getHeaders() {
        return this.header_list;
    }

    /**
     * setHeaders - Sets the list headers
     * destroys previous header data
     *
     * @param  key_value_pairs   the list of header data
     * @return                   the SendGrid object.
     */
    public Sendgrid setHeaders(JSONObject key_value_pairs) {
        this.header_list = key_value_pairs;

        return this;
    }

    /**
     * _arrayToUrlPart - Converts an ArrayList to a url friendly string
     *
     * @param  array   the array to convert
     * @param  token   the name of parameter
     * @return         a url part that can be concatenated to a url request
     */
    protected String _arrayToUrlPart(ArrayList<String> array, String token) {
        String string = "";

        for(int i = 0;i < array.size();i++)
        {
            try {
                string += "&" + token + "[]=" + URLEncoder.encode(array.get(i), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return string;
    }

    /**
     * _prepMessageData - Takes the mail message and returns a url friendly querystring
     *
     * @return the data query string to be posted
     * @throws JSONException 
     */
    protected Map<String, String> _prepMessageData() throws JSONException {
        Map<String,String> params = new HashMap<String, String>();

        params.put("api_user", this.username);
        params.put("api_key", this.password);
        params.put("subject", this.getSubject());
        params.put("html", this.getHtml());
        params.put("text",this.getText());
        params.put("from", this.getFrom());

        if (this.getReplyTo() != null) {
            params.put("replyto", this.getReplyTo());
        }

        if (this._useHeaders() == true) {
            JSONObject headers = this.getHeaders();
            params.put("to", this.getFrom());
            headers.put("to", this.getTos());
            this.setHeaders(headers);
            params.put("x-smtpapi", this.getHeaders().toString());
        } else {
            params.put("to", this.getTos().toString());
        }

        return params;
    }

    /**
     * send - Send an email
     *
     * @throws IOException 
     * @throws JSONException 
     */
    public void send() throws IOException, JSONException {
        Map<String,String> data = new HashMap<String, String>();

        data = this._prepMessageData();
        StringBuffer requestParams = new StringBuffer();
        Iterator<String> paramIterator = data.keySet().iterator();
        while (paramIterator.hasNext()) {
            String key = paramIterator.next();
            String value = data.get(key);
            if (key == "to" && this.getTos().size() > 0) {
                requestParams.append(this._arrayToUrlPart(this.getTos(), "to")+"&");
            } else {
                requestParams.append(URLEncoder.encode(key, "UTF-8"));
                requestParams.append("=");
                requestParams.append(URLEncoder.encode(value, "UTF-8"));
                requestParams.append("&");
            }
        }
        String request = this.domain + this.endpoint;

        if (this.getBccs().size() > 0){
            request += "?" +this._arrayToUrlPart(this.getBccs(), "bcc").substring(1);
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
                // Process line...
                response += line;
            }
            reader.close();
            writer.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // OK
                message = "success";
            } else {
                // Server returned HTTP error code.
                JSONObject apiResponse = new JSONObject(response);
                JSONArray errorsObj = (JSONArray) apiResponse.get("errors");
                for (int i = 0; i < errorsObj.length(); i++) {
                    if (i != 0) {
                        message += ", ";
                    }
                    message += errorsObj.get(i);
                }
            }
        } catch (MalformedURLException e) {
            message = "MalformedURLException - " + e.getMessage();
        } catch (IOException e) {
            message = "IOException - " + e.getMessage();
        }
    }

    /**
     * useHeaders - Checks to see whether or not we can or should you headers. In most cases,
     * we prefer to send our recipients through the headers, but in some cases,
     * we actually don't want to. However, there are certain circumstances in
     * which we have to.
     */
    private Boolean _useHeaders() {
        if ((this._preferNotToUseHeaders() == true) && (this._isHeadersRequired() == false)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * _preferNotToUseHeaders - There are certain cases in which headers are not a preferred choice
     * to send email, as it limits some basic email functionality. Here, we
     * check for any of those rules, and add them in to decide whether or
     * not to use headers
     *
     * @return if true we don't
     */
    private Boolean _preferNotToUseHeaders() {
        if (this.getBccs().size() == 0)
        {
            return true;
        }
        if (this.use_headers != null && this.use_headers == false)
        {
            return true;
        }

        return false;
    }

    /**
     * isHeaderRequired - determines whether or not we need to force recipients through the smtpapi headers
     *
     * @return if true headers are required
     */
    private Boolean _isHeadersRequired() {
        if (this.use_headers == true)
        {
            return true;
        }

        return false;
    }
}
