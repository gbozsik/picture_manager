package hu.ponte.hr.controller;


import hu.ponte.hr.entity.ImageMeta;
import hu.ponte.hr.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("api/images")
public class ImagesController {

    private final ImageService imageService;

    @Autowired
    public ImagesController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("meta")
    public ResponseEntity<List<ImageMeta>> listImages() {
		return new ResponseEntity<>(imageService.getAllImages(), HttpStatus.OK);
    }


    @GetMapping("preview/{id}")
    @ResponseBody
    public void getImage(@PathVariable("id") String id, HttpServletResponse response) {
        imageService.getImageFromFileSystem(id, response);
    }
}