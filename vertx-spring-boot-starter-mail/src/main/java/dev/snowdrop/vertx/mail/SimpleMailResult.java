package dev.snowdrop.vertx.mail;

import java.util.List;

class SimpleMailResult implements MailResult {

    private final String messageId;

    private final List<String> recipients;

    SimpleMailResult(String messageId, List<String> recipients) {
        this.messageId = messageId;
        this.recipients = recipients;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public List<String> getRecipients() {
        return recipients;
    }
}
