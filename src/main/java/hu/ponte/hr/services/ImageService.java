package hu.ponte.hr.services;

import hu.ponte.hr.entity.ImageMeta;
import hu.ponte.hr.repository.ImageMetaRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@Service
public class ImageService {

    private final static Logger log = LoggerFactory.getLogger(ImageService.class);

    private final ImageMetaRepository imageMetaRepository;

    @Autowired
    public ImageService(ImageMetaRepository imageMetaRepository) {
        this.imageMetaRepository = imageMetaRepository;
    }

    public List<ImageMeta> getAllImages() {
        return imageMetaRepository.findAll();
    }

    public void getImageFromFileSystem(String id, HttpServletResponse response) {
        try {
            var projectFolder = System.getProperty("user.dir");
            var uploadsDir = String.format("%s/image/", projectFolder);
            ImageMeta meta = imageMetaRepository.findById(id).orElseThrow();
            var file = ResourceUtils.getFile("" + uploadsDir + meta.getName());
            InputStream in = new FileInputStream(file);
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            IOUtils.copy(in, response.getOutputStream());
        } catch (Exception e) {
            log.error("Could not get picture from filesystem.", e);
        }
    }
}
