package es.nttdata.assetsproxy.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Setter
@Getter
public class Asset {

    private Long id;
    private String filename;
    private String contentType;
    private Long size;
    private String url;
    private OffsetDateTime uploadDate;
    private AssetStatus status;

    private byte[] fileBytes;

    public Asset() {
    }

    public Asset(Long id,
                 String filename,
                 String contentType,
                 Long size,
                 String url,
                 OffsetDateTime uploadDate,
                 AssetStatus status) {

        this.id = id;
        this.filename = filename;
        this.contentType = contentType;
        this.size = size;
        this.url = url;
        this.uploadDate = uploadDate != null ? uploadDate : OffsetDateTime.now();
        this.status = status != null ? status : AssetStatus.PENDING;
    }
}
