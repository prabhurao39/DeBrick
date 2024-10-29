package org.example.resource;

import org.example.service.FileUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FileUploadResource {

    @PostMapping("/upload")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<String> uploadFile(@RequestParam("files") List<MultipartFile> file,
                                             @RequestParam("releaseName") String releaseName,
                                             @RequestParam("repositoryName") String repositoryName) {
        try {
            FileUploadService fileUploadService = new FileUploadService();
            fileUploadService.handleFileUpload(file, releaseName, repositoryName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error : " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("File Upload Successful ");
    }
}
