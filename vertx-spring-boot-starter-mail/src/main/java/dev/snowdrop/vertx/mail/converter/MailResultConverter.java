package dev.snowdrop.vertx.mail.converter;

import dev.snowdrop.vertx.mail.MailResult;
import dev.snowdrop.vertx.mail.SimpleMailResult;

public final class MailResultConverter {

    public MailResult fromVertxMailResult(io.vertx.ext.mail.MailResult mailResult) {
        return new SimpleMailResult(mailResult.getMessageID(), mailResult.getRecipients());
    }
}
