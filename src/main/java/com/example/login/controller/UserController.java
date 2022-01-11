package com.example.login.controller;

import com.example.login.model.*;
import com.example.login.model.dto.*;
import com.example.login.service.GoogleService;
import com.example.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private GoogleService googleService;

    @RequestMapping( "/home")
    public String viewMainPage(){
        return "home";
    }

    @GetMapping( "/login")
    public String viewLoginPage(HttpServletRequest request){
        if (request.getUserPrincipal() != null)
            return "redirect:/home";
        return "login";
    }

    @GetMapping("/register")
    public String viewRegisterPage(Model model){
        model.addAttribute("user", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserDto userDto, Model model) {
        String msg = userService.register(userDto);
        model.addAttribute("msg", msg);
        return "register";
    }

    @RequestMapping("/register/confirm")
    @ResponseBody
    public String confirm(@RequestParam("token") String token) {
        return userService.confirmToken(token);
    }

    @GetMapping( "/forgot-password")
    public String viewForgotPasswordPage(){
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam(value = "email") String email, Model model){
        try {
            String msg = userService.resetPassword(email);
            model.addAttribute("message", msg);
        } catch (UsernameNotFoundException e){
            model.addAttribute("error", e.getMessage());
        }
        return "forgot-password";
    }

    @GetMapping("/reset-password/confirm")
    public String viewResetPasswordPage(@RequestParam(value = "token") String token, Model model) {
        if (!userService.confirmRestPassword(token)){
            model.addAttribute("message", "Cannot change password");
            return "login";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@Param(value = "token") String token, @Param(value = "password") String password,
                                       @Param(value = "repassword") String repw, Model model) {
        if (!password.equals(repw)){
            model.addAttribute("message", "Passwords do not match!");
            return "reset-password";
        }
        if (userService.changePassword(token, password)){
            model.addAttribute("message", "Change password success !");
            return "login";
        }
        model.addAttribute("message", "Cannot reset password !");
        return "reset-password";
    }

    @RequestMapping("/login-google")
    public String loginGoogle(HttpServletRequest request) throws IOException {
        String code = request.getParameter("code");

        if (code == null || code.isEmpty()) {
            return "redirect:/login?error";
        }
        String accessToken = googleService.getToken(code);
        GooglePojo googlePojo = googleService.getUserInfo(accessToken);
        UserDetails userDetail = googleService.buildUser(googlePojo);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null,
                userDetail.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "redirect:/home";
    }

    @GetMapping(path = "/change-profile")
    public String changeProfile(Model model, HttpServletRequest request) throws ParseException {
        UserDto userDto = userService.findByEmail(request.getUserPrincipal().getName());
        userDto.getSchools().add(new SchoolDto("LAM", new SimpleDateFormat("yyyy-MM-dd").parse("1999-06-06"), new SimpleDateFormat("yyyy-MM-dd").parse("1999-06-06"), userDto));
        userDto.getSchools().add(new SchoolDto("LAM1", new SimpleDateFormat("yyyy-MM-dd").parse("1999-06-06"), new SimpleDateFormat("yyyy-MM-dd").parse("1999-06-06"), userDto));
        model.addAttribute("user", userDto);
        return "change-profile";
    }

    @PostMapping(path = "/change-profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String processChangeProfile(@Valid @ModelAttribute(name = "user") UserDto param, BindingResult bindingResult, Model model, @RequestPart("img")MultipartFile multipartFile) throws IOException {
        if (bindingResult.hasErrors()){
            return "change-profile";
        }
        UserDto userDto = userService.findByEmail(param.getEmail());
        if (!multipartFile.isEmpty()){
            String imageName = userDto.getId() + multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().length() - 4);
            String filePath = Paths.get("").toAbsolutePath() + "/src/main/resources/static/images/" + imageName;
            userDto.setImage("/images/" + imageName);
            multipartFile.transferTo(Paths.get(filePath));
//            String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
//            user.setImage(filename);
//            String uploadDir = "images/" + user.getId();
//            Path uploadPath = Paths.get(uploadDir);
//            if (!Files.exists(uploadPath))
//                Files.createDirectories(uploadPath);
//            InputStream inputStream = multipartFile.getInputStream();
//            Path filePath = uploadPath.resolve(filename);
//            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        userDto.setId(param.getId());
        userDto.setName(param.getName());
        userDto.setPhone(param.getPhone());
        userDto.setAddress(param.getAddress());
        if (param.getSchools() != null){
            userDto.setSchools(param.getSchools());
            for (SchoolDto school: userDto.getSchools())
                System.out.println(school);
        }
//        if (param.getSchools() != null && user.getSchools() == null)
//            user.setSchools(new ArrayList<>());
//        for (int i = user.getSchools().size(); i < param.getSchools().size(); ++i){
//            user.getSchools().add(param.getSchools().get(i));
//        }
        return "redirect:/change-profile";
    }

    @RequestMapping("/add-school")
    public String addSchool(Model model, @RequestParam("index") String index){
//        User user = (User) request.getSession().getAttribute("user");
//        if (user.getSchools() == null)
//            user.setSchools(new ArrayList<>());
//        user.getSchools().add(new School());
//        model.addAttribute("user", user);
        model.addAttribute("user", new UserDto());
        model.addAttribute("indexSchool", index);
        return "add-school";
    }

//    @RequestMapping("/delete-school")
//    public String deleteSchool(HttpServletRequest request, Model model){
//        UserDto userDto = (UserDto) request.getSession().getAttribute("user");
//        if (userDto.getSchools() != null)
//            userDto.getSchools().removeAll(userDto.getSchools());
//        model.addAttribute("user", userDto);
//        return "add-school";
//    }

//    @GetMapping("/home/getImage/{image}")
//    @ResponseBody
//    public ResponseEntity<ByteArrayResource> getImage(@PathVariable("image") String image) throws IOException {
//        if (!image.equals("")){
//            Path fileName = Paths.get(Paths.get("").toAbsolutePath() + "/src/main/resources/images/", image);
//            byte[] buffer = Files.readAllBytes(fileName);
//            ByteArrayResource byteArrayResource = new ByteArrayResource(buffer);
//            return ResponseEntity.ok().contentType(MediaType.parseMediaType("image/jpg")).body(byteArrayResource);
//        }
//        return ResponseEntity.badRequest().build();
//    }
}
