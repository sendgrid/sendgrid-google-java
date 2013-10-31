# Sendgrid-google-java #

This library allows you to quickly and easily send emails from Google App Engine through SendGrid using Java.
 
## License ##
Licensed under the MIT License.

## Downloading ##

Installing the SendGrid package is as simple as adding it to your project's include path.  If you're using git, you can just clone down the repo like this:

```
git clone git@github.com:sendgrid/sendgrid-google-java.git
```

__Note__: If you don't have git or would rather install by unpacking a Zip or Tarball, you can always grab the latest version of the package from [the downloads page](https://github.com/sendgrid/sendgrid-google-java/archive/master.zip). 


## SendGrid API ##
SendGrid provides SendGrid library for sending email.


## Mail Pre-Usage ##

Before we begin using the library, its important to understand a few things about the library architecture...

* The SendGrid object is the means of setting mail data. In general, data can be set in three ways for most elements:
  1. set - reset the data, and initialize it to the given element. This will destroy previous data
  2. set (List) - for array based elements, we provide a way of passing the entire array in at once. This will also destroy previous data.
  3. add - append data to the list of elements.

* Sending an email is as simple as :
  1. Creating a SendGrid object, and setting its data
  2. Sending the mail.

## Mail Usage ##

To begin using this library, you must first include it

```java
import packageName.Sendgrid;
```

Then, initialize the SendGrid object with your SendGrid credentials

```java
Sendgrid mail = new Sendgrid("<sendgrid_username>","<sendgrid_password>");
```

Headers are enabled by default. If you do not want to use headers, the use_headers variable must be set to false

```java
mail.use_headers = false;
```

Create a new SendGrid object and add your message details

```java
mail.setTo("foo@bar.com")
    .setFrom("me@bar.com")
    .setSubject("Subject goes here")
    .setText("Hello World!")
    .setHtml("<strong>Hello World!</strong>");
```

Send the email

```java
mail.send();
```

### Using Categories ###

You can mark messages with optional categories to give better visibility to email statistics (opens, clicks, etc.). You can add up to 10 categories per email message. You can read more about Categories here: http://docs.sendgrid.com/documentation/delivery-metrics/categories/

To add categories to your message, use the mail.addCategory() method and pass a category as parameter or mail.setCategories() and pass a list of category names. SendGrid will begin tracking statistics with these category names if the category name is new, or aggregate statistics for existing category names.

```java
mail.addTo("foo@bar.com")
    ...
    .addCategory("Category 1")
    .addCategory("Category 2");
```

```java
mail.addTo("foo@bar.com")
    ...
    .setCategories(new String[]{"Category 1","Category 2","Category 3"});
```

### Using Substitutions ###

SendGrid also allows you to send multi-recipient messages with unique information per recipient. This is commonly used for sending unique URLs or codes to a list of recipients in a single batch. You can read more about Substitutions here: http://docs.sendgrid.com/documentation/api/smtp-api/developers-guide/substitution-tags/

```java
mail.addTo("john@somewhere.com")
    .addTo("harry@somewhere.com")
    .addTo("Bob@somewhere.com")
    ...
    .setHtml("Hey %name%, we've seen that you've been gone for a while")
    .addSubstitution("%name%", new String[]{"John", "Harry", "Bob"});
```

### Using Sections ###

Used in conjunction with Substitutions, Sections can be used to further customize messages for the end users, and acts like a second tier of substitution data. You can use mail.addSection() to add a single section, or mail.setSections() method to add several sections. You can read more about using Sections here: http://docs.sendgrid.com/documentation/api/smtp-api/developers-guide/section-tags/

```java
mail.addTo("john@somewhere.com")
    .addTo("harry@somewhere.com")
    .addTo("Bob@somewhere.com")
    ...
    .setHtml("Hey %name%, you work at %place%")
    .addSubstitution("%name%", new String[]{"John", "Harry", "Bob"})
    .addSubstitution("%place%", new String[]{"%office%", "%office%", "%home%"})
    .addSection("%office%", "an office")
    .addSection("%home%", "your house");
```

### Using Unique Arguments ###

Unique Arguments are used for tracking purposes on the message, and can be seen in the Email Activity screen on your account dashboard or through the Event API. Use the mail.addUniqueArgument() method, which takes two parameters, a key and a value. To pass multiple keys/values, use mail.setUniqueArguments() and pass a dictionary of key/value pairs. More information can be found here: http://docs.sendgrid.com/documentation/api/smtp-api/developers-guide/unique-arguments/

```java
mail.addTo("foo@bar.com")
    ...
    .addUniqueArgument("Customer", "Someone")
    .addUniqueArgument("location", "Somewhere");
```

### Using Filter Settings ###

Filter Settings are used to enable and disable apps, and to pass parameters to those apps. You can read more here: http://docs.sendgrid.com/documentation/api/smtp-api/filter-settings/
Here's an example of passing content to the 'footer' app:

```java
mail.addTo("foo@bar.com")
    ...
    .addFilterSetting("footer", "enable", "1")
    .addFilterSetting("footer", "text/plain", "Here is a plain text footer")
    .addFilterSetting("footer", "text/html", "<p style='color:red;'>Here is an HTML footer</p>");
```

### Using Bcc ###

Bcc is used to send a blind carbon copy to an address. Standard setBcc will hide who the email is addressed to. This is by design. Additionally, it is a good idea to use multiple addTos instead addBcc(this is currently not supported) and each user will receive a personalized email showing only their email.

```java
mail.setBcc("foo@bar.com")
```

Notes:
- addBcc() was removed because is currently not supported.
