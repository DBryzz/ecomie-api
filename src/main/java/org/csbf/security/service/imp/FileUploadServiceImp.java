package org.csbf.security.service.imp;

import lombok.extern.slf4j.Slf4j;
import org.csbf.security.exceptions.ResourceNotFoundException;
import org.csbf.security.service.FileUploadService;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service
public class FileUploadServiceImp implements FileUploadService {

    Environment env;

//    @Value("${app.profile.pics.dir}")
//    private String dir;

//    private final Path root = Paths.get(Objects.requireNonNull(env.getProperty("app.profile.pics.dir")));
    private final Path root = Paths.get("src/main/resources/static/profile-pics");


    @Override
    public void init() {
        try {
            if (Files.exists(root)) {
                log.info(root.toString() + " already exist");
                System.out.println(root.toString() + " already exist");
            } else {
                Files.createDirectories(root);
            }

        } catch (IOException e) {
            log.info("Could not initialize folder for upload!");
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public String save(MultipartFile file) {
        String fileName = StringUtils.cleanPath(UUID.randomUUID() + "__" + Objects.requireNonNull(file.getOriginalFilename()));

        /** saving source code **/

        Path imageUrl = root.resolve(fileName);

        try {
            log.info("In INPUT STREAM: {}", file.getSize());
            log.info("LOCATION: {}", imageUrl);

            Files.copy(file.getInputStream(), imageUrl, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            throw new RuntimeException("Could not store the file.");
        }
        return fileName;
    }

    @Override
    public Resource load(String fileName) {
        String normalizedFileName = StringUtils.getFilename(fileName).replace("%20", " ");

        try {
            Path file = root.resolve(normalizedFileName).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public boolean resourceExist(String fileName) {
        String normalizedFileName = StringUtils.getFilename(fileName).replace("%20", " ");

        String normalFilename = StringUtils.getFilename(fileName);
        try {
            Path file = root.resolve(normalizedFileName).normalize();
            Resource resource = new UrlResource(file.toUri());
            return (resource.exists() || resource.isReadable());
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String fileName) {
        String normalizedFileName = StringUtils.getFilename(fileName).replace("%20", " ");

        try {
            Path file = root.resolve(normalizedFileName).normalize();
            Resource resource = new UrlResource(file.toUri());
            File file1 = new File(file.toString());
            if (resource.exists() || resource.isReadable()) {
                file1.delete();
            } else {
                log.info("FileUploadServiceImp.deleteFile --- Old file does not exist, fileName : {}", fileName);
                throw new ResourceNotFoundException("Could not read OLD file or it doesn't exist!");
            }
        } catch (MalformedURLException e) {
            log.error("{}", e.getMessage());
            throw new RuntimeException("Could not delete file");
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }
}
