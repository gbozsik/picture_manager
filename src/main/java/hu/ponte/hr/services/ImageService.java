package hu.ponte.hr.services;

import hu.ponte.hr.controller.ImageMeta;
import hu.ponte.hr.exception.ServiceException;
import hu.ponte.hr.repository.ImageMetaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.format;


@Service
public class ImageService {

    private final static Logger log = LoggerFactory.getLogger(ImageService.class);

    private final SignService signService;
    private final ImageMetaRepository imageMetaRepository;

    @Autowired
    public ImageService(SignService signService, ImageMetaRepository imageMetaRepository) {
        this.signService = signService;
        this.imageMetaRepository = imageMetaRepository;
    }

    @Transactional
    public ImageMeta uploadPicture(MultipartFile file) throws ServiceException {
        validatePictureSize(file);
        return doUploadPicture(file);
    }

    private ImageMeta doUploadPicture(MultipartFile file) throws ServiceException {
        try {
            String uploadsDir = getUploadDirectory();
            String originalName = file.getOriginalFilename();
            Path path = savePictureToFilesystem(file, uploadsDir, originalName);
            return getPersistedImageMeta(uploadsDir, originalName, path);
        } catch (Exception e) {
            log.error("Could not save uploaded picture!", e);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not save uploaded picture!");
        }
    }

    private static String getUploadDirectory() {
        var projectFolder = System.getProperty("user.dir");
        String uploadsDir = format("%s/image", projectFolder);
        if (!new File(uploadsDir).exists()) {
            new File(uploadsDir).mkdir();
        }
        return uploadsDir;
    }

    private Path savePictureToFilesystem(MultipartFile file, String uploadsDir, String originalName) throws IOException {
        String filePath = format("%s/%s", uploadsDir, originalName);
        File dest = new File(filePath);
        var path = dest.toPath();
        file.transferTo(dest);
        return path;
    }

    private ImageMeta getPersistedImageMeta(String uploadsDir, String originalName, Path path) throws IOException, ServiceException {
        String mimeType = Files.probeContentType(path);
        var imageData = ImageMeta.builder()
                .name(originalName)
                .mimeType(mimeType)
                .size(Files.size(path))
                .digitalSign(signService.sign(originalName, uploadsDir))
                .build();
        var savedImageData = imageMetaRepository.save(imageData);
        log.info("Image saved: {}", savedImageData);
        return savedImageData;
    }

    private static void validatePictureSize(MultipartFile file) throws ServiceException {
        if (file.getSize() > 2000000) {
            log.error("File can not be larger than 2 MB.");
            throw new ServiceException(HttpStatus.BAD_REQUEST, "File can not be larger than 2 MB.");
        }
    }
}
