package dev.snowdrop.vertx.mail;

import java.io.File;

import io.smallrye.mutiny.converters.multi.MultiReactorConverters;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import io.vertx.core.file.OpenOptions;
import io.vertx.ext.mail.impl.MailAttachmentImpl;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.core.file.AsyncFile;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class MailAttachmentConverter {

    private final Vertx vertx;

    private final MultiMapConverter multiMapConverter;

    private final MailAttachmentConverter mailAttachmentConverter=null;

    MailAttachmentConverter(Vertx vertx, MultiMapConverter multiMapConverter) {
        this.vertx = vertx;
        this.multiMapConverter = multiMapConverter;
    }

    Mono<io.vertx.ext.mail.MailAttachment> toVertxMailAttachment(MailAttachment attachment) {
        io.vertx.ext.mail.MailAttachment delegateAttachment = new MailAttachmentImpl();
        delegateAttachment.setName(attachment.getName());
        delegateAttachment.setContentType(attachment.getContentType());
        delegateAttachment.setDisposition(attachment.getDisposition());
        delegateAttachment.setDescription(attachment.getDescription());
        delegateAttachment.setContentId(attachment.getContentId());
        delegateAttachment.setHeaders(multiMapConverter.fromMultiValueMap(attachment.getHeaders()));

        if (attachment.getFile() != null) {
            return fileAttachmentToBuffer(attachment.getFile())
                .map(Buffer::getDelegate)
                .map(delegateAttachment::setData);
        } else if (attachment.getData() != null) {
            return dataBufferAttachmentToBuffer(attachment.getData())
                .map(Buffer::getDelegate)
                .map(delegateAttachment::setData);
        }

        return Mono.error(new IllegalArgumentException("Attachment has no data"));
    }

    private Mono<Buffer> dataBufferAttachmentToBuffer(Flux<DataBuffer> dataBufferStream) {
        return dataBufferStream
            .map(NettyDataBufferFactory::toByteBuf)
            .map(Buffer::buffer)
            .collect(Buffer::buffer, Buffer::appendBuffer);
    }

    private Mono<Buffer> fileAttachmentToBuffer(File file) {
        return vertx.fileSystem()
            .open(file.getAbsolutePath(), new OpenOptions().setRead(true).setCreate(false))
            .convert()
            .with(UniReactorConverters.toMono())
            .flatMap(this::readAndCloseFile);
    }

    private Mono<Buffer> readAndCloseFile(AsyncFile asyncFile) {
        return asyncFile.toMulti()
            .convert()
            .with(MultiReactorConverters.toFlux())
            .collect(Buffer::buffer, Buffer::appendBuffer)
            .doOnTerminate(asyncFile::close);
    }
}
