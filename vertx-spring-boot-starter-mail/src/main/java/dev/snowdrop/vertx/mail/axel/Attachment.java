package dev.snowdrop.vertx.mail.axel;

// TODO should come from an external dependency
public class Attachment {

    private final String name;
    private final String contentType;
    private final String disposition;
    private final String description;
    private final String contentId;
    private final byte[] data;


    public Attachment(String name, String contentType, String disposition, String description, String contentId, byte[] data) {
        this.name = name;
        this.contentType = contentType;
        this.disposition = disposition;
        this.description = description;
        this.contentId = contentId;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getContentType() {
        return contentType;
    }

    public String getDisposition() {
        return disposition;
    }

    public String getDescription() {
        return description;
    }

    public String getContentId() {
        return contentId;
    }

    public byte[] getData() {
        return data;
    }

    public static AttachmentBuilder create() {
        return new AttachmentBuilder();
    }


    public static final class AttachmentBuilder {
        private String name;
        private String contentType;
        private String disposition;
        private String description;
        private String contentId;
        private byte[] data;

        private AttachmentBuilder() {
            // Avoid direct instantiation.
        }

        public AttachmentBuilder name(String name) {
            this.name = name;
            return this;
        }

        public AttachmentBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public AttachmentBuilder disposition(String disposition) {
            this.disposition = disposition;
            return this;
        }

        public AttachmentBuilder description(String description) {
            this.description = description;
            return this;
        }

        public AttachmentBuilder contentId(String contentId) {
            this.contentId = contentId;
            return this;
        }

        public AttachmentBuilder data(byte[] data) {
            this.data = data;
            return this;
        }

        public Attachment build() {
            return new Attachment(name, contentType, disposition, description, contentId, data);
        }
    }
}
