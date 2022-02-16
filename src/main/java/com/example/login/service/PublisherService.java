package com.example.login.service;

import com.example.login.model.dto.PublisherDto;

import java.util.List;

public interface PublisherService {
    List<PublisherDto> findAllPublisher();

    void createPublisher(PublisherDto publisherDto);

    PublisherDto findById(int id);

    void updatePublisher(PublisherDto publisherDto);

    void deletePublisherById(int id);
}
