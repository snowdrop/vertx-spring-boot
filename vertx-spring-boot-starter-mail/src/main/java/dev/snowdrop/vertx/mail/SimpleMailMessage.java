package dev.snowdrop.vertx.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class SimpleMailMessage implements MailMessage {

    private String from;

    private List<String> to = new ArrayList<>();

    private List<String> cc = new ArrayList<>();

    private List<String> bcc = new ArrayList<>();

    private String bounceAddress;

    private String subject;

    private String text;

    private String html;

    private List<MailAttachment> inlineAttachments = new ArrayList<>();

    private List<MailAttachment> attachments = new ArrayList<>();

    private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

    private boolean fixedHeaders = false;

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public SimpleMailMessage setFrom(String from) {
        this.from = from;
        return this;
    }

    @Override
    public List<String> getTo() {
        return to;
    }

    @Override
    public SimpleMailMessage setTo(List<String> to) {
        if (to == null) {
            this.to = new ArrayList<>();
        } else {
            this.to = to;
        }
        return this;
    }

    @Override
    public SimpleMailMessage addTo(String... to) {
        if (to != null) {
            Collections.addAll(this.to, to);
        }
        return this;
    }

    @Override
    public List<String> getCc() {
        return cc;
    }

    @Override
    public SimpleMailMessage setCc(List<String> cc) {
        if (cc == null) {
            this.cc = new ArrayList<>();
        } else {
            this.cc = cc;
        }
        return this;
    }

    @Override
    public SimpleMailMessage addCc(String... cc) {
        if (cc != null) {
            Collections.addAll(this.cc, cc);
        }
        return this;
    }

    @Override
    public List<String> getBcc() {
        return bcc;
    }

    @Override
    public SimpleMailMessage setBcc(List<String> bcc) {
        if (bcc == null) {
            this.bcc = new ArrayList<>();
        } else {
            this.bcc = bcc;
        }
        return this;
    }

    @Override
    public SimpleMailMessage addBcc(String... bcc) {
        if (bcc != null) {
            Collections.addAll(this.bcc, bcc);
        }
        return this;
    }

    @Override
    public String getBounceAddress() {
        return bounceAddress;
    }

    @Override
    public SimpleMailMessage setBounceAddress(String bounceAddress) {
        this.bounceAddress = bounceAddress;
        return this;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public SimpleMailMessage setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public SimpleMailMessage setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public String getHtml() {
        return html;
    }

    @Override
    public SimpleMailMessage setHtml(String html) {
        this.html = html;
        return this;
    }

    @Override
    public List<MailAttachment> getInlineAttachments() {
        return inlineAttachments;
    }

    @Override
    public SimpleMailMessage setInlineAttachments(List<MailAttachment> inlineAttachments) {
        if (inlineAttachments == null) {
            this.inlineAttachments = new ArrayList<>();
        } else {
            this.inlineAttachments = inlineAttachments;
        }
        return this;
    }

    @Override
    public SimpleMailMessage addInlineAttachment(MailAttachment inlineAttachment) {
        if (inlineAttachment != null) {
            inlineAttachments.add(inlineAttachment);
        }
        return this;
    }

    @Override
    public List<MailAttachment> getAttachments() {
        return attachments;
    }

    @Override
    public SimpleMailMessage setAttachments(List<MailAttachment> attachments) {
        if (attachments == null) {
            this.attachments = new ArrayList<>();
        } else {
            this.attachments = attachments;
        }
        return this;
    }

    @Override
    public SimpleMailMessage addAttachment(MailAttachment attachment) {
        if (attachments != null) {
            attachments.add(attachment);
        }
        return this;
    }

    @Override
    public MultiValueMap<String, String> getHeaders() {
        return headers;
    }

    @Override
    public SimpleMailMessage setHeaders(MultiValueMap<String, String> headers) {
        if (headers == null) {
            this.headers = new LinkedMultiValueMap<>();
        } else {
            this.headers = headers;
        }
        return this;
    }

    @Override
    public SimpleMailMessage addHeader(String key, String... values) {
        if (key == null || values == null) {
            throw new IllegalArgumentException("Cannot add header, key and value must not be null");
        }
        headers.addAll(key, Arrays.asList(values));
        return this;
    }

    @Override
    public SimpleMailMessage removeHeader(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Cannot remove header, key must not be null");
        }
        headers.remove(key);
        return this;
    }

    @Override
    public boolean isFixedHeaders() {
        return fixedHeaders;
    }

    @Override
    public SimpleMailMessage setFixedHeaders(boolean fixedHeaders) {
        this.fixedHeaders = fixedHeaders;
        return this;
    }
}
