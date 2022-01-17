package com.example.login.service.impl;

import com.example.login.repository.SchoolRepository;
import com.example.login.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class SchoolServiceImpl implements SchoolService {
    @Autowired
    private SchoolRepository schoolRepository;

    @Override
    public void deleteAllByUser(int id) {
        schoolRepository.deleteAllByUserId(id);
    }
}
