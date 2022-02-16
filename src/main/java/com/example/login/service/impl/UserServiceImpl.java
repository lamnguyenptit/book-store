package com.example.login.service.impl;

import com.example.login.model.*;
import com.example.login.model.dto.*;
import com.example.login.repository.CartRepository;
import com.example.login.repository.UserRepository;
import com.example.login.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfirmationTokenService confirmationTokenService;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email: %s not found", email)));
    }

    @Override
    public String register(UserDto userDto) {
        if (!emailService.isValid(userDto.getEmail())) {
            return "Email not valid";
        }
        User user = userRepository.findByEmail(userDto.getEmail()).orElse(null);
        if (user != null) {
            if (user.isEnabled()) {
                return "Cannot send your email";
            }
            String token = updateToken(user);
            if (!emailService.sendEmailRegister(user, token)){
                return "Cannot send your email";
            }
        }
        else {
            userDto.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
            user = new User(userDto.getName(), userDto.getEmail(), userDto.getPassword(), Role.USER);
            user.setProvider(Provider.LOCAL);
            userRepository.save(user);
            String token = generateToken(user);
            if (!emailService.sendEmailRegister(user, token)){
                return "Cannot send your email";
            }
        }
        return "An email was sent to your email address. Please verify your email!";
    }

    private String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = ConfirmationToken.builder().token(token).createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15)).user(user).build();
//        ConfirmationToken confirmationToken = new ConfirmationToken(
//                token,
//                LocalDateTime.now(),
//                LocalDateTime.now().plusMinutes(15),
//                user
//        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }

    private String updateToken(User user){
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = ConfirmationToken.builder().token(token).createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15)).user(user).build();
//        ConfirmationToken confirmationToken = new ConfirmationToken(
//                user.getConfirmationToken().getId(),
//                token,
//                LocalDateTime.now(),
//                LocalDateTime.now().plusMinutes(15),
//                user
//        );
        confirmationTokenService.updateConfirmationToken(confirmationToken);
        return token;
    }

    @Override
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.findByToken(token).orElse(null);

        if (confirmationToken == null)
            return "Wrong token";

        if (confirmationToken.getConfirmedAt() != null) {
            return "Error";
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            return "Token expired";
        }

        confirmationTokenService.setConfirmedAt(token);
        userRepository.enableUser(confirmationToken.getUser().getEmail());
        return "Confirmed";
    }

    @Override
    public String resetPassword(String email) {
        if (!emailService.isValid(email)) {
            return "Invalid email";
        }
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null && user.getProvider().equals(Provider.LOCAL)) {
            if (!user.isEnabled())
                return "Account doesn't exist!";
            String token = updateToken(user);
            if (emailService.sendEmailResetPassword(user, token))
                return "We have sent a reset password link to your email. Please check.";
        }
        return "Cannot send to your email";
    }

    @Override
    public boolean confirmRestPassword(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.findByToken(token).orElse(null);

        if (confirmationToken == null)
            return false;

        if (confirmationToken.getConfirmedAt() != null) {
            return false;
        }

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        confirmationTokenService.setConfirmedAt(token);
        return true;
    }

    @Override
    public boolean changePassword(String token, String password) {
        ConfirmationToken confirmationToken = confirmationTokenService.findByToken(token).orElse(null);
        if (confirmationToken == null)
            return false;

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = confirmationTokenService.findUserByToken(token).orElse(null);
        if (user == null)
            return false;
        userRepository.updatePassword(bCryptPasswordEncoder.encode(password), user.getId());
        return true;
    }

    @Override
    public User processOAuthPostLogin(GooglePojo googlePojo) {
        User existUser = userRepository.findByEmail(googlePojo.getEmail()).orElse(null);
        if (existUser == null) {
            User newUser = new User();
            newUser.setEmail(googlePojo.getEmail());
            newUser.setProvider(Provider.GOOGLE);
            newUser.setEnabled(true);
            newUser.setLocked(false);
            newUser.setRole(Role.USER);
            newUser.setName(googlePojo.getName());
            newUser.setImage(googlePojo.getPicture());
            userRepository.save(newUser);
        }
        return existUser;
    }

    @Override
    public UserDto findByEmail(String email){
        return convertToUserDto(userRepository.findByEmail(email).orElse(null));
    }

    @Override
    public void updateUser(UserDto userDto) {
        User param = convertToUser(userDto);
        User user = userRepository.findByEmail(param.getEmail()).orElse(null);
        if (user == null)
            return;
        user.setId(param.getId());
        user.setName(param.getName());
        user.setPhone(param.getPhone());
        user.setAddress(param.getAddress());
        if (param.getImage() != null && !param.getImage().equals(user.getImage()))
            user.setImage(param.getImage());
        if (param.getSchools() != null){
            if (user.getSchools() != null){
                for (School school: param.getSchools()){
                    school.setUser(user);
                }
            }
            user.getSchools().clear();
            param.getSchools().forEach(user.getSchools()::add);
        }
        else
            schoolService.deleteAllByUser(user.getId());

//        User test = new User();
//        test.setEmail("lam");
//        test.setName("Lam");
//        test.setPassword("$2a$10$q1IOTSOXqGMvHOV0ooDupezSTgcZ22DvKX.Ade/OdgFdiW4Zfs6Bm");
//        test.setRole(Role.ADMIN);
//        test.setLocked(false);
//        test.setProvider(Provider.LOCAL);
//        test.setEnabled(true);
//        userRepository.save(test);

        user.setPassword(param.getPassword());
        userRepository.saveAndFlush(user);
    }

    private UserDto convertToUserDto(User user){
        if (user == null)
            return null;
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        if (user.getConfirmationToken() != null){
            ConfirmationTokenDto confirmationTokenDto = new ConfirmationTokenDto();
            BeanUtils.copyProperties(user.getConfirmationToken(), confirmationTokenDto);
            userDto.setConfirmationTokenDto(confirmationTokenDto);
        }
        if (user.getSchools() != null) {
            List<SchoolDto> schoolDtos = new ArrayList<>();
            for (School school : user.getSchools()) {
                SchoolDto schoolDto = new SchoolDto();
                BeanUtils.copyProperties(school, schoolDto);
                schoolDto.setUser(userDto);
                schoolDtos.add(schoolDto);
            }
            userDto.setSchools(schoolDtos);
        }
        if (user.getCompanies() != null) {
            List<CompanyDto> companyDtos = new ArrayList<>();
            for (Company company : user.getCompanies()) {
                CompanyDto companyDto = new CompanyDto();
                BeanUtils.copyProperties(company, companyDto);
                companyDto.setUser(userDto);
                companyDtos.add(companyDto);
            }
            userDto.setCompanies(companyDtos);
        }
        if (user.getDegrees() != null) {
            List<DegreeDto> degreeDtos = new ArrayList<>();
            for (Degree degree : user.getDegrees()) {
                DegreeDto degreeDto = new DegreeDto();
                BeanUtils.copyProperties(degree, degreeDto);
                degreeDto.setUser(userDto);
                degreeDtos.add(degreeDto);
            }
            userDto.setDegrees(degreeDtos);
        }
        return userDto;
    }

    private User convertToUser(UserDto userDto) {
        if (userDto == null)
            return null;
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        if (userDto.getConfirmationTokenDto() != null){
            ConfirmationToken confirmationToken = new ConfirmationToken();
            BeanUtils.copyProperties(userDto.getConfirmationTokenDto(), confirmationToken);
            user.setConfirmationToken(confirmationToken);
        }
        if (userDto.getSchools() != null) {
            List<School> schools = new ArrayList<>();
            for (SchoolDto schoolDto : userDto.getSchools()) {
                School school = new School();
                BeanUtils.copyProperties(schoolDto, school);
                school.setUser(user);
                schools.add(school);
            }
            user.setSchools(schools);
        }
        if (userDto.getCompanies() != null) {
            List<Company> companies = new ArrayList<>();
            for (CompanyDto companyDto : userDto.getCompanies()) {
                Company company = new Company();
                BeanUtils.copyProperties(companyDto, company);
                company.setUser(user);
                companies.add(company);
            }
            user.setCompanies(companies);
        }
        if (userDto.getDegrees() != null) {
            List<Degree> degrees = new ArrayList<>();
            for (DegreeDto degreeDto : userDto.getDegrees()) {
                Degree degree = new Degree();
                BeanUtils.copyProperties(degreeDto, degree);
                degree.setUser(user);
                degrees.add(degree);
            }
            user.setDegrees(degrees);
        }

        User userInDB = userRepository.findById(userDto.getId()).get();

        if(userInDB.getProvider().equals(Provider.LOCAL)){
            if(!userDto.getPassword().isEmpty()){
                String password = bCryptPasswordEncoder.encode(userDto.getPassword());
                user.setPassword(password);
            }else{
                user.setPassword(userInDB.getPassword());
            }
        }else{
            user.setPassword(userInDB.getPassword());
        }
        return user;
    }

    @Override
    public List<User> listAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public void delete(Integer id) {
        Long countById = userRepository.countById(id);
        if(countById == null || countById == 0) {
        }
        userRepository.deleteById(id);
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id).get();
    }

    @Override
    public User getUserByEmail(String userEmail) {
        return userRepository.findUserByEmail(userEmail);
    }

}
