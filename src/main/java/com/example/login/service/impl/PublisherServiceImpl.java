package com.example.login.service.impl;

import com.example.login.model.Publisher;
import com.example.login.model.dto.PublisherDto;
import com.example.login.repository.PublisherRepository;
import com.example.login.service.PublisherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
