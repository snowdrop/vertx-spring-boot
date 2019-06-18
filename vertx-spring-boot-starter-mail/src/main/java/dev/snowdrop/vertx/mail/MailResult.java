package dev.snowdrop.vertx.mail;

import java.util.List;

public interface MailResult {

    String getMessageId();

    List<String> getRecipients();
}
