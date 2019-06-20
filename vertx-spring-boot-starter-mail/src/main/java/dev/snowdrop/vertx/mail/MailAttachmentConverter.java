package dev.snowdrop.vertx.mail;

import java.io.File;
import java.util.concurrent.CompletionStage;

import io.vertx.axle.core.Vertx;
import io.vertx.axle.core.file.AsyncFile;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.OpenOptions;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class MailAttachmentConverter {

    private final Vertx vertx;

    private final MultiMapConverter multiMapConverter;

    MailAttachmentConverter(Vertx vertx, MultiMapConverter multiMapConverter) {
        this.vertx = vertx;
        this.multiMapConverter = multiMapConverter;
    }

    Mono<io.vertx.ext.mail.MailAttachment> toVertxMailAttachment(MailAttachment attachment) {
        io.vertx.ext.mail.MailAttachment delegateAttachment = new io.vertx.ext.mail.MailAttachment();
        delegateAttachment.setName(attachment.getName());
        delegateAttachment.setContentType(attachment.getContentType());
        delegateAttachment.setDisposition(attachment.getDisposition());
        delegateAttachment.setDescription(attachment.getDescription());
        delegateAttachment.setContentId(attachment.getContentId());
        delegateAttachment.setHeaders(multiMapConverter.fromMultiValueMap(attachment.getHeaders()));

        if (attachment.getFile() != null) {
            return fileAttachmentToBuffer(attachment.getFile())
                .map(delegateAttachment::setData);
        } else if (attachment.getData() != null) {
            return dataBufferAttachmentToBuffer(attachment.getData())
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
        CompletionStage<AsyncFile> fileFuture = vertx.fileSystem()
            .open(file.getAbsolutePath(), new OpenOptions().setRead(true).setCreate(false));

        CompletionStage<Buffer> bufferFuture = ReactiveStreams.fromCompletionStage(fileFuture)
            .flatMap(asyncFile -> asyncFile
                .toPublisherBuilder()
                .map(io.vertx.axle.core.buffer.Buffer::getDelegate)
                .onTerminate(asyncFile::close))
            .collect(Buffer::buffer, Buffer::appendBuffer)
            .run();

        return Mono.fromCompletionStage(bufferFuture);
    }
}
