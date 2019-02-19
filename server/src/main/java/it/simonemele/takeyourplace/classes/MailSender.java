/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.simonemele.takeyourplace.classes;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 *
 * @author Simone
 */
public final class MailSender { 
    
    public static final short SIGN_UP = 0;
    public static final short CHANGE_MAIL = 1;
    public static final short CHANGE_PASSWORD = 2;
    public static final short CONFIRM_EMAIL = 3;
    public static final short DELETE_USER = 4;
    
    private final String baseUri;
    
    private String host;
    private int port;
    private String username;
    private String password;
    private String from;
    
    public MailSender(URI baseUri){
        this.baseUri = baseUri.toString();
    }

    public MailSender(String baseUri) {
        this.baseUri = baseUri;
    }    
    

    public final String getHost() {
        return host;
    }
    
    private MimeMessage configure() throws IOException{
        this.readConfigurationProperties();
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        
        Authenticator auth = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                     }
                };
        //Get session
        Session session = Session.getInstance(
                properties,
                auth
        );
                
        
        return new MimeMessage(session);
    }
    
    private void readConfigurationProperties() throws IOException{
        Properties prop = new Properties();
        
        InputStream fileStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("smtp.conf");    
        
        prop.load(fileStream);
        host = prop.getProperty("host");
        port = Integer.valueOf(prop.getProperty("port"));
        username = prop.getProperty("username");
        password = prop.getProperty("password");
        from = prop.getProperty("from");
    }
    
    public void send( 
            String to, 
            String subject, 
            String messageText) throws MessagingException, IOException
    {  
        
        MimeMessage message = configure();
        
        
            InternetAddress fromIA = new InternetAddress(from);
            InternetAddress toIA = new InternetAddress(to);
            
            message.setFrom(fromIA);
            message.addRecipient(Message.RecipientType.TO, toIA);
            
            message.setSubject(subject);
            message.setContent(messageText, "text/html");
            
            Transport.send(message);
            System.out.println("Message successfully sent...");
    }
    
    public void send( 
            String to,
            long activationId,
            String activationCode,
            short messageType) throws MessagingException, IOException{
        
        switch(messageType){
            case SIGN_UP : 
                sendSignUpEmail(to, activationId, activationCode);
                break;
            
            case CHANGE_MAIL : 
                sendMailChangingEmail(to, activationId, activationCode);
                break;
            
            case CHANGE_PASSWORD : 
                sendPasswordChangingEmail(to, activationId, activationCode);
                break;
            
            case CONFIRM_EMAIL :
                sendMailConfirmEmail(to, activationId, activationCode);
                break;
            case DELETE_USER :
                sendDeleteUserEmail(to, activationId, activationCode);
                break;
        }
    }
    
    public void sendSignUpEmail(
            String to,
            long activationId,
            String activationCode) throws MessagingException, IOException
    {
        String activationUrl = this.baseUri + "auth/confirm/"
                + activationId + "/" + activationCode;
        
        String subject = "Take Your Place - Confirmation Email";
        String message = "<h1>Take Your Place</h1>"
                + "<p>You have signed up on Take Your Place, "
                + "click on the link below to activate your account</p>"
                + "<a href='" + activationUrl + "'>"
                + activationUrl + "</a>";
                
        send(to, subject, message);
    }
    
    public void sendMailChangingEmail(
            String to,
            long activationId,
            String activationCode ) throws MessagingException, IOException {
        
        String activationUrl = this.baseUri + "auth/changeEmail/"
                + activationId + "/" + activationCode;
        
        String subject = "Take Your Place - Confirm email changing";
        String message = "<h1>Take Your Place</h1>"
                + "<p>There is a request to change your email, "
                + "click on the link below to confirm the operation</p>"
                + "<a href='" + activationUrl + "'>"
                + activationUrl + "</a>"
                + "<p><h3>If you did not make the request, "
                + "ignore this email</h3></p>";
                
        send(to, subject, message);
    }
    
    public void sendPasswordChangingEmail(
            String to,
            long activationId,
            String activationCode) throws MessagingException, IOException
    {
        String activationUrl = this.baseUri + "auth/changePsw/"
                + activationId + "/" + activationCode;
        
        String subject = "Take Your Place - Confirm password changing";
        String message = "<h1>Take Your Place</h1>"
                + "<p>There is a request to change your password, "
                + "click on the link below to confirm the operation</p>"
                + "<a href='" + activationUrl + "'>"
                + activationUrl + "</a>"
                + "<p><h3>If you did not make the request, "
                + "ignore this email</h3></p>";
                
        send(to, subject, message);
    }
    
    public void sendMailConfirmEmail(
            String to,
            long activationId,
            String activationCode) throws MessagingException, IOException
    {
        String activationUrl = this.baseUri + "auth/confirm/"
                + activationId + "/" + activationCode;
        
        String subject = "Take Your Place - Confirmation Email";
        String message = "<h1>Take Your Place</h1>"
                + "<p>You request to change the email is being processed, "
                + "click on the link below to complete the operation</p>"
                + "<a href='" + activationUrl + "'>"
                + activationUrl + "</a>";
                
        send(to, subject, message);
    }
    
    public void sendDeleteUserEmail(
            String to,
            long activationId,
            String activationCode) throws MessagingException, IOException
    {
        String activationUrl = this.baseUri + "auth/deleteUser/"
                + activationId + "/" + activationCode;
        
        String subject = "Take Your Place - Unsubscribe request ";
        String message = "<h1>Take Your Place</h1>"
                + "<p>There is a request to delete permanently your account, "
                + "click on the link below to allow the operation</p>"
                + "<a href='" + activationUrl + "'>"
                + activationUrl + "</a>"
                + "<p><h3>If you did not make the request, "
                + "ignore this email</h3></p>";
                
        send(to, subject, message);
    }
    
    public void sendResetPasswordEmail(
            String to,
            String password) throws MessagingException, IOException
    {
        String subject = "Take Your Place - Password resetted";
        String message = "<h1>Take Your Place</h1>"
                + "<p>Password is successfully resetted, your new password is"
                + "<br/>" + password + "<p>After your first login you can "
                + "change your password.<p><strong>N.B.Delete this email after "
                + "you have taken note</strong></p>";                
        send(to, subject, message);
    }
}
