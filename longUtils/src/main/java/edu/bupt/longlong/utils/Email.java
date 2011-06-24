/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bupt.longlong.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author oulong
 */
public class Email {

    static String mailReceiver = null;

    static {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(Email.class.getClassLoader().getResourceAsStream("mailreceiver.txt")));
            mailReceiver = reader.readLine();
        } catch (Exception e) {
            mailReceiver = "";
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     *
     * @param emails 用分号隔开的邮件地址
     * @param title 标题
     * @param content 正文
     * @return
     */
    public static boolean send(String title, String content) {
        String[] mails = mailReceiver.split(";");

        for (int i = 0; i < mails.length; i++) {
            try {
                Properties mailProps = new Properties();
                mailProps.put("mail.smtp.host", "mail.qunar.com");
                Session mailSession = Session.getDefaultInstance(mailProps);
                MimeMessage message = new MimeMessage(mailSession);
                message.setFrom(new InternetAddress("alert@qunar.com"));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(mails[i]));
                message.setSubject(title);
                MimeMultipart multi = new MimeMultipart();
                BodyPart textBodyPart = new MimeBodyPart();
                textBodyPart.setText(content);
                multi.addBodyPart(textBodyPart);
                message.setContent(multi);
                message.saveChanges();
                Transport.send(message);
            } catch (Exception exc) {
            }
        }

        return true;
    }

    public static void main(String[] argv) {
        Email.send("标题测试", "内容测试");
    }
}
