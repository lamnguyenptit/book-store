package com.example.login.service.impl;

import com.example.login.model.School;
import com.example.login.model.User;
import com.example.login.model.dto.CompanyDto;
import com.example.login.model.dto.DegreeDto;
import com.example.login.model.dto.SchoolDto;
import com.example.login.model.dto.UserDto;
import com.example.login.service.ConverterService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//@Service
//public class ConverterServiceImpl implements ConverterService {
//    @Override
//    public UserDto convertToUserDto(User user){
//        if (user == null)
//            return null;
//        UserDto userDto = new UserDto();
//        userDto.setId(user.getId());
//        userDto.setEmail(user.getEmail());
//        userDto.setName(user.getName());
//        userDto.setImage(user.getImage());
//        userDto.setAddress(user.getAddress());
//        userDto.setPhone(user.getPhone());
//        userDto.setEnabled(user.isEnabled());
//        userDto.setLocked(user.isLocked());
//        userDto.setRole(user.getRole());
//        userDto.setProvider(user.getProvider());
//        user.setConfirmationToken(user.getConfirmationToken());
//        if (user.getSchools() == null)
//            userDto.setSchools(null);
//        else
//            userDto.setSchools(new ArrayList<>(convertToSchoolDtos(user.getSchools())));
//        if (user.getCompanies() == null)
//            userDto.setCompanies(null);
//        else
//            userDto.setCompanies(new ArrayList<CompanyDto>(user.getCompanies()));
//        if (user.getDegrees() == null)
//            userDto.setDegrees(null);
//        else
//            userDto.setDegrees(new ArrayList<DegreeDto>(user.getDegrees()));
//        return userDto;
//    }
//
//    public List<SchoolDto> convertToSchoolDtos(List<School> schools){
//        if (schools == null)
//            return null;
//        return schools.stream().map(this::convertToSchoolDto).collect(Collectors.toList());
//    }
//
//    public SchoolDto convertToSchoolDto(School school){
//        if (school == null)
//            return null;
//        SchoolDto schoolDto = new SchoolDto();
//        schoolDto.setId(school.getId());
//        schoolDto.setName(school.getName());
//        schoolDto.setAdmissionDate(school.getAdmissionDate());
//        schoolDto.setGraduateDate(school.getGraduateDate());
//        schoolDto.setUser(school.getUser());
//    }
//}
