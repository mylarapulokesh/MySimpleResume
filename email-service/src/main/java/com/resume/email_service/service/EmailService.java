package com.resume.email_service.service;

import com.resume.email_service.Client.AccountClientService;
import com.resume.email_service.DTO.WelcomeMailDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.servlet.function.ServerResponse.status;

@Service
public class EmailService {
    @Autowired
    JavaMailSender mailSender;
    @Value("${email.sender}")
    private String emailSenderName;
    @Value("${domain.url}")
    private String domainUrl;
    @Value("${customer.care.email}")
    private String customerCareEmail;
    @Autowired
    AccountClientService accountClient;
    public ResponseEntity<String> sendEmail(WelcomeMailDTO userDetails) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userDetails.getToMail());
        mailMessage.setSubject("Account Created Successfully!");
        mailMessage.setText("Happy to See You on MySimpleResume!");
        mailMessage.setFrom(emailSenderName);
        mailMessage.setSentDate(new Date());
        mailSender.send(mailMessage);
        return ResponseEntity.status(HttpStatus.OK).body("Email Sent Successfully");
    }
    public ResponseEntity<String> sendPasswordResetMail(String emailId, UUID uuid) {
        String userStatus = accountClient.validateUser(emailId);
        if (userStatus.equalsIgnoreCase("Valid User")){
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setFrom(emailSenderName);
                helper.setSentDate(new Date());
                helper.setSubject("Password Reset Mail - MySimpleResume");
                helper.setTo(emailId);
                String resetLink = domainUrl+"/reset-password.html?token=" + uuid + "&email=" + emailId;
                String htmlBody = "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1.0'></head>"
                                + "<body style='margin:0;padding:0;background-color:#f0f4ff;font-family:Segoe UI,Arial,sans-serif;'>"

                                // Outer wrapper
                                + "<table width='100%' cellpadding='0' cellspacing='0' border='0' style='background:#f0f4ff;padding:40px 16px;'>"
                                + "<tr><td align='center'>"

                                // Card
                                + "<table width='560' cellpadding='0' cellspacing='0' border='0' style='max-width:560px;width:100%;background:#ffffff;border-radius:20px;overflow:hidden;box-shadow:0 4px 40px rgba(80,120,255,0.13);'>"

                                // Top gradient bar
                                + "<tr><td style='background:linear-gradient(90deg,#4f8ef7,#7c3aed,#f472b6,#38bdf8);height:5px;font-size:0;line-height:0;'>&nbsp;</td></tr>"

                                // Header
                                + "<tr><td align='center' style='background:linear-gradient(135deg,#eef4ff 0%,#f5f0ff 50%,#fdf2fb 100%);padding:36px 40px 28px;border-bottom:1px solid #e8eeff;'>"
                                + "<table cellpadding='0' cellspacing='0' border='0' style='margin-bottom:20px;'>"
                                + "<tr>"
                                + "<td style='background:linear-gradient(135deg,#4f8ef7,#7c3aed);border-radius:8px;width:32px;height:32px;text-align:center;vertical-align:middle;font-size:16px;'>📄</td>"
                                + "<td style='padding-left:10px;font-size:14px;font-weight:700;color:#2d3a8c;letter-spacing:-0.01em;'>MySimpleResume</td>"
                                + "</tr>"
                                + "</table>"
                                + "<div style='font-size:13px;color:#7c3aed;font-weight:600;letter-spacing:0.08em;text-transform:uppercase;margin-bottom:12px;'>Password Reset Request</div>"
                                + "<h1 style='margin:0;font-size:26px;font-weight:800;color:#1a2060;letter-spacing:-0.03em;line-height:1.2;'>We got your request!</h1>"
                                + "</td></tr>"

                                // Body
                                + "<tr><td style='padding:32px 40px;'>"

                                // Greeting box
                                + "<table width='100%' cellpadding='0' cellspacing='0' border='0' style='background:#f8faff;border-radius:14px;border:1px solid #e4ecff;margin-bottom:24px;'>"
                                + "<tr>"
                                + "<td style='width:44px;padding:16px 0 16px 20px;vertical-align:top;'>"
                                + "<div style='width:44px;height:44px;border-radius:50%;background:linear-gradient(135deg,#4f8ef7,#7c3aed);text-align:center;line-height:44px;font-size:20px;'>👋</div>"
                                + "</td>"
                                + "<td style='padding:16px 20px;'>"
                                + "<div style='font-size:14px;font-weight:700;color:#1a2060;margin-bottom:4px;'>Hi there!</div>"
                                + "<div style='font-size:13px;color:#6b7db3;line-height:1.6;'>We have Recieved a request for password reset for your account. If this was you, click the button below.</div>"
                                + "</td>"
                                + "</tr>"
                                + "</table>"

                                // Expiry pill
                                + "<div style='text-align:center;margin-bottom:24px;'>"
                                + "<span style='display:inline-block;background:#fff7ed;border:1px solid #fed7aa;border-radius:99px;padding:6px 16px;font-size:12px;font-weight:600;color:#c2410c;'>&#9203; &nbsp;Link expires in <strong>30 minutes</strong></span>"
                                + "</div>"

                                // CTA Button
                                + "<table width='100%' cellpadding='0' cellspacing='0' border='0' style='margin-bottom:28px;'>"
                                + "<tr><td align='center'>"
                                + "<a href='" + resetLink + "' target='_blank' "
                                + "style='display:inline-block;padding:16px 44px;"
                                + "background:linear-gradient(135deg,#4f8ef7 0%,#7c3aed 50%,#ec4899 100%);"
                                + "color:#ffffff;font-size:16px;font-weight:700;text-decoration:none;"
                                + "border-radius:14px;letter-spacing:0.01em;"
                                + "box-shadow:0 4px 24px rgba(79,142,247,0.35),0 1px 4px rgba(124,58,237,0.2);'>"
                                + "Reset My Password &nbsp;&rarr;"
                                + "</a>"
                                + "</td></tr>"
                                + "<tr><td align='center' style='padding-top:10px;font-size:12px;color:#94a3b8;'>"
                                + "Button not working? <a href='" + resetLink + "' style='color:#4f8ef7;text-decoration:none;font-weight:600;'>Click here</a>"
                                + "</td></tr>"
                                + "</table>"

                                // Steps
                                + "<table width='100%' cellpadding='0' cellspacing='0' border='0' style='background:#f8faff;border-radius:14px;border:1px solid #e4ecff;margin-bottom:24px;'>"
                                + "<tr><td style='padding:20px 24px;'>"
                                + "<div style='font-size:12px;font-weight:700;color:#2d3a8c;letter-spacing:0.06em;text-transform:uppercase;margin-bottom:14px;'>What happens next?</div>"

                                + "<table cellpadding='0' cellspacing='0' border='0' style='margin-bottom:12px;'><tr>"
                                + "<td style='vertical-align:top;padding-right:12px;'><div style='width:24px;height:24px;border-radius:50%;background:#4f8ef7;color:#fff;font-size:11px;font-weight:700;text-align:center;line-height:24px;'>1</div></td>"
                                + "<td style='font-size:13px;color:#3d4f7c;padding-top:4px;'>Click the button above to open the reset page</td>"
                                + "</tr></table>"

                                + "<table cellpadding='0' cellspacing='0' border='0' style='margin-bottom:12px;'><tr>"
                                + "<td style='vertical-align:top;padding-right:12px;'><div style='width:24px;height:24px;border-radius:50%;background:#7c3aed;color:#fff;font-size:11px;font-weight:700;text-align:center;line-height:24px;'>2</div></td>"
                                + "<td style='font-size:13px;color:#3d4f7c;padding-top:4px;'>Enter and confirm your new password</td>"
                                + "</tr></table>"

                                + "<table cellpadding='0' cellspacing='0' border='0'><tr>"
                                + "<td style='vertical-align:top;padding-right:12px;'><div style='width:24px;height:24px;border-radius:50%;background:#ec4899;color:#fff;font-size:11px;font-weight:700;text-align:center;line-height:24px;'>3</div></td>"
                                + "<td style='font-size:13px;color:#3d4f7c;padding-top:4px;'>Sign in and continue building your resume!</td>"
                                + "</tr></table>"

                                + "</td></tr></table>"

                                // Warning box
                                + "<table width='100%' cellpadding='0' cellspacing='0' border='0'>"
                                + "<tr><td style='background:#fff5f5;border:1px solid #fecaca;border-radius:12px;padding:16px 20px;'>"
                                + "<div style='font-size:12px;font-weight:700;color:#dc2626;margin-bottom:6px;'>&#9888; &nbsp;Didn&apos;t request this?</div>"
                                + "<div style='font-size:12px;color:#b91c1c;line-height:1.6;'>"
                                + "If you did not request a password reset, please ignore this email or contact us immediately at "
                                + "<a href='mailto:" + customerCareEmail + "' style='font-weight:700;color:#dc2626;text-decoration:none;'>" + customerCareEmail + "</a>."
                                + "</div>"
                                + "</td></tr>"
                                + "</table>"

                                + "</td></tr>"

                                // Footer
                                + "<tr><td align='center' style='background:#f8faff;border-top:1px solid #e4ecff;padding:20px 40px;'>"
                                + "<div style='font-size:11px;color:#94a3b8;line-height:1.8;'>"
                                + "Sent by <strong style='color:#4f8ef7;'>MySimpleResume</strong> &nbsp;&middot;&nbsp; Do not reply to this email<br>"
                                + "&copy; 2026 MySimpleResume. All rights reserved."
                                + "</div>"
                                + "</td></tr>"

                                // Bottom gradient bar
                                + "<tr><td style='background:linear-gradient(90deg,#38bdf8,#4f8ef7,#7c3aed,#ec4899);height:4px;font-size:0;line-height:0;'>&nbsp;</td></tr>"

                                + "</table>"
                                + "</td></tr></table>"
                                + "</body></html>";
                helper.setText(htmlBody,true);
                mailSender.send(mimeMessage);
                return ResponseEntity.status(HttpStatus.OK).body("Reset Password Mail Sent Successfully");
            }catch (MessagingException e){
                return new ResponseEntity<>("Failed to send reset email. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
    }
}
