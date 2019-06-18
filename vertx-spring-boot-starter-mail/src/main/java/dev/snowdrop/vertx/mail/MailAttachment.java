package dev.snowdrop.vertx.mail;

import java.io.File;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

public interface MailAttachment {

    String getName();

    MailAttachment setName(String name);

    String getContentType();

    MailAttachment setContentType(String contentType);

    String getDisposition();

    MailAttachment setDisposition(String disposition);

    String getDescription();

    MailAttachment setDescription(String description);

    String getContentId();

    MailAttachment setContentId(String contentId);

    Flux<DataBuffer> getData();

    MailAttachment setData(Flux<DataBuffer> data);

    File getFile();

    MailAttachment setFile(File file);

    MultiValueMap<String, String> getHeaders();

    MailAttachment setHeaders(MultiValueMap<String, String> headers);

    MailAttachment addHeader(String key, String... values);

    MailAttachment removeHeader(String key);
}
