package com.online.judge.amazons3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class AmazonS3Service {

    @Autowired
    private AmazonS3 s3client;

    @Value("${aws.testcases.bucketName}")
    private String bucketName;

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (final FileOutputStream outputStream = new FileOutputStream(convFile)) {
            outputStream.write(file.getBytes());
        } catch (final IOException ex) {
            throw new IOException(ex.getMessage());
        }
        return convFile;
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        System.out.println(fileName);
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file));
    }

    public void uploadFile(MultipartFile multipartFile, String fileName) {
        try {
            File file = convertMultiPartToFile(multipartFile);
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (AmazonServiceException | IOException ex) {
            throw new AmazonServiceException(ex.getMessage());
        }
    }
}
