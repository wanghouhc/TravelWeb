package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import java.util.Properties;

/**
 * 发送邮件工具类
 */
public final class MailUtils {
	private MailUtils(){}
	/**
	 * 发送邮件
	 * 参数一:发送邮件给谁
	 * 参数二:发送邮件的内容
	 */
	public static void sendMail(String toEmail, String emailMsg) throws Exception {
		// 1.创建一个程序与发件人的 发送邮件服务器会话对象 Session
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "SMTP");//邮件发送协议
		props.setProperty("mail.host", "smtp.163.com");//邮件发送服务器的地址，如果使用的是本地安装的易邮，写成：localhost
		props.setProperty("mail.smtp.auth", "true");//指定验证为true
		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("liuyp_itcast", "heima100");
			}
		};
		Session session = Session.getInstance(props, auth);
		//2_创建一封邮件
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("liuyp_itcast@163.com"));
		message.setRecipient(RecipientType.TO, new InternetAddress(toEmail));
		message.setSubject("黑马旅游网帐号激活");
		message.setContent(emailMsg, "text/html;charset=UTF-8");
		//3_发送邮件
		Transport.send(message);
	}
}








