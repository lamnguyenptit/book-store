package com.example.login.service;

import com.example.login.model.dto.PublisherDto;

import java.util.List;

public interface PublisherService {
    List<PublisherDto> findAllPublisher();
}
