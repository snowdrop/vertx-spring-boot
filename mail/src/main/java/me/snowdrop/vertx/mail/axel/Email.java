package me.snowdrop.vertx.mail.axel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO should come from an external dependency
public class Email {

    private final String bounceAddress;
    private final String from;
    private final List<String> to;
    private final List<String> cc;
    private final List<String> bcc;
    private final String subject;
    private final String text;
    private final String html;
    private final List<Attachment> inlineAttachment;
    private final List<Attachment> attachment;

    public Email(String from, List<String> to, List<String> cc, List<String> bcc, String subject, String text, String html,
            String bounceAddress, List<Attachment> inlineAttachment, List<Attachment> attachment) {
        this.bounceAddress = bounceAddress;
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.text = text;
        this.html = html;
        this.inlineAttachment = inlineAttachment;
        this.attachment = attachment;
    }

    public String getBounceAddress() {
        return bounceAddress;
    }

    public String getFrom() {
        return from;
    }

    public List<String> getTo() {
        return to;
    }

    public List<String> getCc() {
        return cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public String getHtml() {
        return html;
    }

    public List<Attachment> getInlineAttachments() {
        return inlineAttachment;
    }

    public List<Attachment> getAttachments() {
        return attachment;
    }

    public static EmailBuilder create() {
        return new EmailBuilder();
    }


    public static final class EmailBuilder {
        private String bounceAddress;
        private String from;
        private List<String> to = new ArrayList<>();
        private List<String> cc = new ArrayList<>();
        private List<String> bcc = new ArrayList<>();
        private String subject;
        private String text;
        private String html;
        private List<Attachment> inlineAttachments = new ArrayList<>();
        private List<Attachment> attachments = new ArrayList<>();

        private EmailBuilder() {
            // Avoid direct instantiation.
        }


        public EmailBuilder bounceAddress(String bounceAddress) {
            this.bounceAddress = bounceAddress;
            return this;
        }

        public EmailBuilder from(String from) {
            this.from = from;
            return this;
        }

        public EmailBuilder to(List<String> to) {
            this.to = to;
            return this;
        }

        public EmailBuilder to(String to) {
            this.to.add(Objects.requireNonNull(to, "email address must not be `null`"));
            return this;
        }

        public EmailBuilder cc(List<String> cc) {
            this.cc = cc;
            return this;
        }

        public EmailBuilder cc(String to) {
            this.cc.add(Objects.requireNonNull(to, "email address must not be `null`"));
            return this;
        }

        public EmailBuilder bcc(List<String> bcc) {
            this.bcc = bcc;
            return this;
        }

        public EmailBuilder bcc(String to) {
            this.bcc.add(Objects.requireNonNull(to, "email address must not be `null`"));
            return this;
        }

        public EmailBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public EmailBuilder text(String text) {
            this.text = text;
            return this;
        }

        public EmailBuilder html(String html) {
            this.html = html;
            return this;
        }

        public EmailBuilder inlineAttachments(List<Attachment> inlineAttachment) {
            if (inlineAttachment == null) {
                this.inlineAttachments = new ArrayList<>();
            } else {
                this.inlineAttachments = inlineAttachment;
            }
            return this;
        }

        public EmailBuilder inlineAttachment(Attachment inline) {
            this.inlineAttachments.add(inline);
            return this;
        }

        public EmailBuilder attachments(List<Attachment> attachment) {
            this.attachments = attachment;
            return this;
        }

        public EmailBuilder attachment(Attachment attachment) {
            this.attachments.add(attachment);
            return this;
        }


        public Email build() {
            validate();
            return new Email(
                    from,
                    to, cc, bcc,
                    subject,
                    text, html,
                    bounceAddress,
                    inlineAttachments, attachments
            );
        }

        private void validate() {
            if (this.from == null  || this.from.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid email - `from` must be set");
            }

            if (this.to.isEmpty()  && this.cc.isEmpty()  && this.bcc.isEmpty()) {
                throw new IllegalArgumentException("Invalid email - at least one recipient is required");
            }
        }
    }
}
