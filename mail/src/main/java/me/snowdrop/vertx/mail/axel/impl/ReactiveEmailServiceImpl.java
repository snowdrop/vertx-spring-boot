package me.snowdrop.vertx.mail.axel.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.mail.MailAttachment;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailMessage;
import me.snowdrop.vertx.mail.axel.Attachment;
import me.snowdrop.vertx.mail.axel.Email;
import me.snowdrop.vertx.mail.axel.ReactiveEmailService;

public class ReactiveEmailServiceImpl implements ReactiveEmailService {

    private MailClient client;

    public ReactiveEmailServiceImpl(MailClient client) {
        this.client = client;
    }

    @Override
    public CompletionStage<Void> send(Email email) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        client.sendMail(
                toMailMessage(email),
                ar -> {
                    if (ar.succeeded()) {
                        future.complete(null);
                    } else {
                        future.completeExceptionally(ar.cause());
                    }
                }
        );
        return future;
    }

    private MailMessage toMailMessage(Email email) {
        return new MailMessage()
                .setBounceAddress(email.getBounceAddress())
                .setAttachment(email.getAttachments().stream().map(this::toMailAttachment).collect(Collectors.toList()))
                .setInlineAttachment(email.getInlineAttachments().stream().map(this::toMailAttachment).collect(Collectors.toList()))
                .setBcc(email.getBcc())
                .setCc(email.getCc())
                .setFrom(email.getFrom())
                .setHtml(email.getHtml())
                .setSubject(email.getSubject())
                .setTo(email.getTo())
                .setText(email.getText());
    }

    private MailAttachment toMailAttachment(Attachment attachment) {
        return new MailAttachment()
                .setName(attachment.getName())
                .setDisposition(attachment.getDisposition())
                .setContentType(attachment.getContentType())
                .setContentId(attachment.getContentId())
                .setData(Buffer.buffer(attachment.getData()))
                .setDescription(attachment.getDescription());
    }

}
