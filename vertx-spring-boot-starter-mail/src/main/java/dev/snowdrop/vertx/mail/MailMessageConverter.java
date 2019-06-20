package dev.snowdrop.vertx.mail;

import java.util.List;

import io.vertx.ext.mail.MailAttachment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class MailMessageConverter {

    private final MailAttachmentConverter mailAttachmentConverter;

    private final MultiMapConverter multiMapConverter;

    MailMessageConverter(MailAttachmentConverter mailAttachmentConverter, MultiMapConverter multiMapConverter) {
        this.mailAttachmentConverter = mailAttachmentConverter;
        this.multiMapConverter = multiMapConverter;
    }

    Mono<io.vertx.ext.mail.MailMessage> toVertxMailMessage(MailMessage message) {
        io.vertx.ext.mail.MailMessage delegateMessage = new io.vertx.ext.mail.MailMessage();
        delegateMessage.setBounceAddress(message.getBounceAddress());
        delegateMessage.setFrom(message.getFrom());
        delegateMessage.setTo(message.getTo());
        delegateMessage.setCc(message.getCc());
        delegateMessage.setBcc(message.getBcc());
        delegateMessage.setSubject(message.getSubject());
        delegateMessage.setText(message.getText());
        delegateMessage.setHtml(message.getHtml());
        delegateMessage.setHeaders(multiMapConverter.fromMultiValueMap(message.getHeaders()));

        Mono<List<MailAttachment>> attachmentsFuture = Flux
            .fromIterable(message.getAttachments())
            .flatMap(mailAttachmentConverter::toVertxMailAttachment)
            .collectList()
            .doOnNext(delegateMessage::setAttachment);

        Mono<List<io.vertx.ext.mail.MailAttachment>> inlineAttachmentsFuture = Flux
            .fromIterable(message.getInlineAttachments())
            .flatMap(mailAttachmentConverter::toVertxMailAttachment)
            .collectList()
            .doOnNext(delegateMessage::setInlineAttachment);

        return Mono.zip(inlineAttachmentsFuture, attachmentsFuture)
            .thenReturn(delegateMessage);
    }
}
