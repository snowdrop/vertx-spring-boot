package dev.snowdrop.vertx.mail;

import java.io.File;
import java.util.Arrays;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

public class SimpleMailAttachment implements MailAttachment {

    private String name;

    private String contentType;

    private String disposition;

    private String description;

    private String contentId;

    private Flux<DataBuffer> data;

    private File file;

    private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SimpleMailAttachment setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public SimpleMailAttachment setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    @Override
    public String getDisposition() {
        return disposition;
    }

    @Override
    public SimpleMailAttachment setDisposition(String disposition) {
        this.disposition = disposition;
        return this;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public SimpleMailAttachment setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String getContentId() {
        return contentId;
    }

    @Override
    public SimpleMailAttachment setContentId(String contentId) {
        this.contentId = contentId;
        return this;
    }

    @Override
    public Flux<DataBuffer> getData() {
        return data;
    }

    @Override
    public SimpleMailAttachment setData(Flux<DataBuffer> data) {
        this.data = data;
        return this;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public SimpleMailAttachment setFile(File file) {
        this.file = file;
        return this;
    }

    @Override
    public MultiValueMap<String, String> getHeaders() {
        return headers;
    }

    @Override
    public SimpleMailAttachment setHeaders(MultiValueMap<String, String> headers) {
        if (headers == null) {
            this.headers = new LinkedMultiValueMap<>();
        } else {
            this.headers = headers;
        }
        return this;
    }

    @Override
    public SimpleMailAttachment addHeader(String key, String... values) {
        if (key == null || values == null) {
            throw new IllegalArgumentException("Cannot add header, key and value must not be null");
        }
        headers.addAll(key, Arrays.asList(values));
        return this;
    }

    @Override
    public SimpleMailAttachment removeHeader(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Cannot remove header, key must not be null");
        }
        headers.remove(key);
        return this;
    }
}
