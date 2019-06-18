package dev.snowdrop.vertx.mail;

import java.util.List;

import org.springframework.util.MultiValueMap;

public interface MailMessage {

    String getFrom();

    MailMessage setFrom(String from);

    List<String> getTo();

    MailMessage setTo(List<String> to);

    MailMessage addTo(String... to);

    List<String> getCc();

    MailMessage setCc(List<String> cc);

    MailMessage addCc(String... cc);

    List<String> getBcc();

    MailMessage setBcc(List<String> bcc);

    MailMessage addBcc(String... bcc);

    String getBounceAddress();

    MailMessage setBounceAddress(String bounceAddress);

    String getSubject();

    MailMessage setSubject(String subject);

    String getText();

    MailMessage setText(String text);

    String getHtml();

    MailMessage setHtml(String html);

    List<MailAttachment> getInlineAttachments();

    MailMessage setInlineAttachments(List<MailAttachment> inlineAttachments);

    MailMessage addInlineAttachment(MailAttachment inlineAttachment);

    List<MailAttachment> getAttachments();

    MailMessage setAttachments(List<MailAttachment> attachments);

    MailMessage addAttachment(MailAttachment attachment);

    MultiValueMap<String, String> getHeaders();

    MailMessage setHeaders(MultiValueMap<String, String> headers);

    MailMessage addHeader(String key, String... values);

    MailMessage removeHeader(String key);

    boolean isFixedHeaders();

    MailMessage setFixedHeaders(boolean fixedHeaders);
}
