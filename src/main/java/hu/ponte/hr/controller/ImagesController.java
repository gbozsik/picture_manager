package hu.ponte.hr.controller;


import hu.ponte.hr.repository.ImageMetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/images")
public class ImagesController {

    private final ImageMetaRepository imageMetaRepository;

    @Autowired
    public ImagesController(ImageMetaRepository imageMetaRepository) {
        this.imageMetaRepository = imageMetaRepository;
    }

    @GetMapping("meta")
    public ResponseEntity<List<ImageMeta>> listImages() {
		return new ResponseEntity<List<ImageMeta>>(imageMetaRepository.findAll(), HttpStatus.OK);
    }
}