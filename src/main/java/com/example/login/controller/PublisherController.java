package com.example.login.controller;

import com.example.login.model.dto.PublisherDto;
import com.example.login.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
public class PublisherController {
    @Autowired
    private PublisherService publisherService;

    @GetMapping("/admin/list-publisher")
    public String listAllPublisher(Model model){
        model.addAttribute("publishers", publisherService.findAllPublisher());
        return "admin/list-publisher";
    }

    @GetMapping("/admin/add-publisher")
    public String addPublisher(Model model) {
        model.addAttribute("publisher", new PublisherDto());
        return "admin/add-publisher";
    }

    @PostMapping(path = "/admin/add-publisher", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String processAddPublisher(@Valid @ModelAttribute(name = "publisher") PublisherDto publisherDto, BindingResult bindingResult,
                                     Model model, @RequestPart("img") MultipartFile multipartFile) throws IOException {
        if (bindingResult.hasErrors()){
            return "admin/add-publisher";
        }
        if (!multipartFile.isEmpty()) {
            String tail = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().length() - 4);
            if (tail.equals(".jpg") || tail.equals(".png")) {
                String imageName = UUID.randomUUID() + ".jpg";
                String filePath1 = Paths.get("").toAbsolutePath() + "/target/classes/static/publisher-images/";
                String filePath2 = Paths.get("").toAbsolutePath() + "/src/main/resources/static/publisher-images/";
                publisherDto.setLogo(imageName);
                multipartFile.transferTo(Paths.get(filePath1 + imageName));
                multipartFile.transferTo(Paths.get(filePath2 + imageName));
            } else {
                String message = "Just accept .jpg or .png file";
                model.addAttribute("messageFile", message);
                return "admin/add-publisher";
            }
        }
        publisherService.createPublisher(publisherDto);
        return "redirect:/admin/add-publisher?success";
    }

    @GetMapping("/admin/update-publisher/{id}")
    public String updatePublisher(@PathVariable(name = "id")int id, Model model) {
        model.addAttribute("publisher", publisherService.findById(id));
        return "admin/update-publisher";
    }

    @PostMapping(path = "/admin/update-publisher", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String processUpdatePublisher(@Valid @ModelAttribute(name = "publisher") PublisherDto publisherDto, BindingResult bindingResult,
                                        Model model, @RequestPart("img") MultipartFile multipartFile) throws IOException {
        if (bindingResult.hasErrors()){
            return "admin/update-publisher";
        }
        if (!multipartFile.isEmpty()) {
            String tail = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().length() - 4);
            if (tail.equals(".jpg") || tail.equals(".png")) {
                String imageName = publisherDto.getLogo();
                if (imageName == null || imageName.equals("")){
                    imageName = UUID.randomUUID() + ".jpg";
                    publisherDto.setLogo(imageName);
                }
                String filePath1 = Paths.get("").toAbsolutePath() + "/target/classes/static/publisher-images/";
                String filePath2 = Paths.get("").toAbsolutePath() + "/src/main/resources/static/publisher-images/";
                multipartFile.transferTo(Paths.get(filePath1 + imageName));
                multipartFile.transferTo(Paths.get(filePath2 + imageName));
            } else {
                String message = "Just accept .jpg or .png file";
                model.addAttribute("messageFile", message);
                return "admin/add-publisher";
            }
        }
        publisherService.updatePublisher(publisherDto);
        return "redirect:/admin/list-publisher";
    }

    @GetMapping("/admin/delete-publisher")
    @ResponseBody
    public void deleteProduct(@RequestParam("id") int id){
        publisherService.deletePublisherById(id);
    }
}
