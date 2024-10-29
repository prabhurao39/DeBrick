package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.HttpClientBuilder;
import org.example.constants.AppConstants;
import org.example.exception.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class FileUploadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadService.class);

    private static final OkHttpClient client = HttpClientBuilder.getOKHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static void cleanUp(File tempFile) {
        tempFile.delete();
    }

    public void handleFileUpload(List<MultipartFile> files,
                                 String releaseName,
                                 String repositoryName) throws FileUploadException {

        for (MultipartFile file : files) {
            File tempFile = saveFileLocally(file);

            if (tempFile != null) {
                // Upload file using OkHttp client
                try {
                    makeHttpRequest(tempFile, releaseName, repositoryName);
                } catch (Exception e) {
                    throw new FileUploadException(e.getMessage());
                } finally {
                    // Clean up temporary file
                    cleanUp(tempFile);
                }
            } else {
                throw new FileUploadException(" Exception occurred : temp file is null");
            }
        }

    }

    // Helper method to save the uploaded MultipartFile to a local temporary file
    private File saveFileLocally(MultipartFile multipartFile) {
        try {
            Path tempDir = Files.createTempDirectory("upload");
            Path tempFile = Paths.get(tempDir.toString(), multipartFile.getOriginalFilename());
            Files.write(tempFile, multipartFile.getBytes());
            return tempFile.toFile();
        } catch (IOException e) {
            return null;
        }
    }


    // Method to upload a file using OkHttp
    private void makeHttpRequest(File file, String releaseName,
                                 String repositoryName) throws IOException, FileUploadException {

        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("fileData", file.getName(),
                        RequestBody.create(file, MediaType.parse("application/octet-stream")))
                .addFormDataPart("releaseName", releaseName)
                .addFormDataPart("repositoryName", repositoryName)
                .build();


        Request request = new Request.Builder()
                .url(AppConstants.FILE_UPLOAD_API)
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + AppConstants.BEARER_TOKEN)
                .addHeader("Accept", "*/*")
                .addHeader("Host", "debricked.com")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "multipart/form-data; boundary=--------------------------648440746469254821870403")
                .build();


        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // Get the response body as a string
                String jsonResponse = response.body().string();
                // Parse the JSON response using Jackson ObjectMapper
                JsonNode jsonNode = objectMapper.readTree(jsonResponse);
                jsonNode.get(0);

                // start the monitoring service
                new ScannerService().run();
                if (LOGGER.isDebugEnabled())
                    LOGGER.info("File upload successful for File name {}", file.getName());
            } else {
                if (LOGGER.isDebugEnabled())
                    LOGGER.info("File upload failed for File name {} response message {} ", file.getName(), response.message());
                throw new FileUploadException("File upload failed ");
            }
        }
    }

}
