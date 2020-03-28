package com.ecnu2020.achieveit.util;

import com.ecnu2020.achieveit.entity.Staff;
import com.ecnu2020.achieveit.mapper.StaffMapper;
import com.sun.mail.util.MailSSLSocketFactory;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import tk.mybatis.mapper.entity.Example;

/**
 * JavaMail发送邮件:前提是QQ邮箱里帐号设置要开启POP3/SMTP协议
 * @author yan on 2020-03-07
 */
@Component
@Slf4j
public class SendMail {

    @Value("${mail.user}")
    private String user;

    @Value("${mail.password}")
    private String password;

    @Value("${mail.internetAddress}")
    private String internetAddress;

    @Autowired
    private StaffMapper staffMapper;


    /**
     *
     * @param targetMail 目标邮箱
     * @param subject 邮件主题
     * @param mailMessage 邮件内容
     * @throws Exception
     */
    public void sendMail(String targetMail,String subject, String mailMessage) throws Exception {

        Properties prop = new Properties();
        prop.setProperty("mail.debug", "true");
        prop.setProperty("mail.host", "smtp.qq.com");
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.transport.protocol", "smtp");

        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.socketFactory", sf);

        Session session = Session.getInstance(prop);
        Transport ts = session.getTransport();
        ts.connect("smtp.qq.com",user, password);
        Message message = createSimpleMail(session,targetMail,subject,mailMessage);
        ts.sendMessage(message, message.getAllRecipients());
        ts.close();
    }

    /**
     * 创建一封只包含文本的邮件
     */
    private MimeMessage createSimpleMail(Session session,String targetMail,String subject,String mailMessage)
            throws Exception {
// 创建邮件对象
        MimeMessage message = new MimeMessage(session);
// 指明邮件的发件人
        message.setFrom(new InternetAddress(internetAddress));
// 指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(targetMail));
// 邮件的标题
        message.setSubject(subject);
// 邮件的文本内容
        message.setContent(mailMessage, "text/html;charset=UTF-8");
// 返回创建好的邮件对象
        return message;
    }


    public void sendStaffEmail(List<String> staffIdList,String subject, String mailMessage){
        if(staffIdList.isEmpty()){
            return;
        }
        Example example = new Example(Staff.class);
        example.createCriteria().andIn("id",staffIdList);

        List<String> emailList = staffMapper.selectByExample(example)
            .stream().map(staff -> staff.getEmail()).collect(Collectors.toList());

        emailList.stream().forEach(email->{
            try {
                sendMail(email, subject, mailMessage);
            }catch(Exception e){
                log.warn(e.getMessage());
            }
        });
    }


}