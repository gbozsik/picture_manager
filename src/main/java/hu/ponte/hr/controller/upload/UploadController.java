package hu.ponte.hr.controller.upload;

import hu.ponte.hr.controller.ImageMeta;
import hu.ponte.hr.exception.ServiceException;
import hu.ponte.hr.services.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequestMapping("api/file")
public class UploadController {

    private final static Logger log = LoggerFactory.getLogger(UploadController.class);

    private final UploadService uploadService;

    @Autowired
    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @RequestMapping(value = "post", method = RequestMethod.POST)
    public ResponseEntity<ImageMeta> handleFormUpload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                var imageMeta = uploadService.uploadPicture(file);
                return new ResponseEntity<>(imageMeta, HttpStatus.CREATED);
            } catch (ServiceException e) {
                log.error("Could not upload picture", e);
                return new ResponseEntity<>(e.getHttpStatus());
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
