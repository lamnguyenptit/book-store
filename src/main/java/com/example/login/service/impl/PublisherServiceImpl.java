package com.example.login.service.impl;

import com.example.login.model.Category;
import com.example.login.model.Publisher;
import com.example.login.model.dto.PublisherDto;
import com.example.login.repository.PublisherRepository;
import com.example.login.service.PublisherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublisherServiceImpl implements PublisherService {
    @Autowired
    private PublisherRepository publisherRepository;

    @Override
    public List<PublisherDto> findAllPublisher() {
        return convertToDtos(publisherRepository.findAll());
    }

    @Override
    public void createPublisher(PublisherDto publisherDto) {
        Publisher publisher = new Publisher();
        publisher.setName(publisherDto.getName());
        publisher.setLogo(publisherDto.getLogo());
        publisherRepository.save(publisher);
    }

    @Override
    public PublisherDto findById(int id) {
        return convertToDto(publisherRepository.findById(id).orElse(null));
    }

    @Override
    public void updatePublisher(PublisherDto publisherDto) {
        Publisher publisher = publisherRepository.findById(publisherDto.getId()).orElse(null);
        if (publisher == null)
            return;
        publisher.setId(publisherDto.getId());
        publisher.setName(publisherDto.getName());
        if (publisher.getLogo() == null || publisher.getLogo().equals(""))
            publisher.setLogo(publisherDto.getLogo());
        publisherRepository.saveAndFlush(publisher);
    }

    @Override
    public void deletePublisherById(int id) {
        Publisher publisher = publisherRepository.findById(id).orElse(null);
        if (publisher == null)
            return;
        if (publisher.getLogo() != null || !publisher.getLogo().equals("")){
            String filePath1 = Paths.get("").toAbsolutePath() + "/target/classes/static/publisher-images/";
            String filePath2 = Paths.get("").toAbsolutePath() + "/src/main/resources/static/publisher-images/";
            try {
                Files.delete(Paths.get(filePath1 + publisher.getLogo()));
                Files.delete(Paths.get(filePath2 + publisher.getLogo()));
            } catch (IOException ignored) {
            }
            try {
                Files.delete(Paths.get(filePath2 + publisher.getLogo()));
            } catch (IOException ignored) {
            }
        }
        publisherRepository.delete(publisher);
    }

    public PublisherDto convertToDto(Publisher publisher){
        if (publisher == null)
            return null;
        PublisherDto publisherDto = new PublisherDto();
        BeanUtils.copyProperties(publisher, publisherDto);
        return publisherDto;
    }

    public List<PublisherDto> convertToDtos(List<Publisher> publishers){
        if (publishers == null)
            return null;
        return publishers.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}
