package dev.snowdrop.vertx.mail;

class MailResultConverter {

    MailResult fromVertxMailResult(io.vertx.ext.mail.MailResult mailResult) {
        return new SimpleMailResult(mailResult.getMessageID(), mailResult.getRecipients());
    }
}
