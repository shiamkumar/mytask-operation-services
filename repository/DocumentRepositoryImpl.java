package com.ghx.api.operations.repository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import org.bson.BsonString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.ghx.api.operations.dto.BlobDTO;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.messagesource.CustomMessageSource;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.ErrorConstants;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;
import com.mongodb.MongoGridFSException;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;

/**
 * 
 * @author Ajith
 *
 */
@Component
public class DocumentRepositoryImpl implements DocumentRepositoryCustom {
    /** The GHX-LOGGER */
    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(DocumentRepositoryImpl.class);
    /** The MongoTemplate mongoTemplate */
    @Autowired
    private transient MongoTemplate mongoTemplate;

    /**
     * get Blob with mongo key and directory
     * 
     */
    @Override
    public BlobDTO getBlob(String id, String directory) {
        BlobDTO blobDTO = new BlobDTO();
        GridFSBucket gridFSBuckets = GridFSBuckets.create(mongoTemplate.getDb(), directory);
        try (GridFSDownloadStream gridFSDownload = gridFSBuckets.openDownloadStream(new BsonString(id))) {
            GridFSFile gridFSFile = gridFSDownload.getGridFSFile();
            blobDTO.setData(this.readBlobContent(gridFSDownload));
            blobDTO.setId(gridFSFile.getId().asString().getValue());
            blobDTO.setFileName(gridFSFile.getFilename());
            blobDTO.setSize(gridFSFile.getLength());
            blobDTO.setCreatedOn(gridFSFile.getUploadDate());
            if (Objects.nonNull(gridFSFile.getMetadata())) {
                blobDTO.setMimeType(gridFSFile.getMetadata().getString(ConstantUtils.CONTENT_TYPE));
            } else {
                blobDTO.setMimeType(Files.probeContentType(new File(gridFSFile.getFilename()).toPath()));
            }

        } catch (MongoGridFSException exception) {
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.DOCUMENT_NOT_FOUND, exception));
        } catch (IOException exception) {
            LOGGER.error("Exception while fetching content type from document", exception);
            blobDTO.setMimeType(null);
        }
        return blobDTO;
    }

    /**
     * read Blob Content by downloadStream
     * @param downloadStream
     * @return byte[]
     */
    private byte[] readBlobContent(GridFSDownloadStream downloadStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int data = downloadStream.read(); data >= 0; data = downloadStream.read()) {
            outputStream.write((char) data);
        }
        return outputStream.toByteArray();
    }

}
