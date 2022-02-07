package com.example.login.controller;

import com.example.login.model.*;
import com.example.login.model.dto.*;
import com.example.login.service.GoogleService;
import com.example.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private GoogleService googleService;

    @RequestMapping( "/user/home")
    public String viewMainPage(){
        return "home";
    }

    @GetMapping("")
    public String viewDefault(){
        return "redirect:/view";
    }

    @GetMapping( "/loginUser")
    public String viewLoginUserPage(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            return "loginUser";
        if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("USER")))
            return "redirect:/user/home";
        return "redirect:/admin/home";
    }

    @GetMapping("/register")
    public String viewRegisterPage(Model model){
        model.addAttribute("user", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserDto userDto, Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "register";
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
            return "redirect:/loginUser?error";
        }
        String accessToken = googleService.getToken(code);
        GooglePojo googlePojo = googleService.getUserInfo(accessToken);
        UserDetails userDetail = googleService.buildUser(googlePojo);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null,
                userDetail.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "redirect:/user/home";
    }

    @GetMapping(path = "/change-profile")
    public String changeProfile(Model model, HttpServletRequest request) {
        UserDto userDto = userService.findByEmail(request.getUserPrincipal().getName());
        model.addAttribute("user", userDto);
        return "change-profile";
    }

    @PostMapping(path = "/change-profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String processChangeProfile(@Valid @ModelAttribute(name = "user") UserDto param, BindingResult bindingResult, Model model, @RequestPart("img")MultipartFile multipartFile) throws IOException {
        if (param.getSchools() != null){
            for (SchoolDto schoolDto: param.getSchools()){
                if (schoolDto.getAdmissionDate().getTime() >= schoolDto.getGraduateDate().getTime()){
                    String err = "AdmissionDate must less than GraduateDate";
                    ObjectError error = new ObjectError("dateError", err);
                    bindingResult.addError(error);
                    break;
                }
            }
        }
        if (bindingResult.hasErrors()){
            return "change-profile";
        }
        if (!multipartFile.isEmpty()){
            String tail = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().length() - 4);
            if (tail.equals(".jpg") || tail.equals(".png")){
                String imageName = param.getId() + ".jpg";
                String filePath1 = Paths.get("").toAbsolutePath() + "/target/classes/static/images/";
                String filePath2 = Paths.get("").toAbsolutePath() + "/src/main/resources/static/images/";
                param.setImage("images/" + imageName);
//                Files.deleteIfExists(Paths.get(filePath + param.getId() + ".jpg"));
                multipartFile.transferTo(Paths.get(filePath1 + imageName));
                multipartFile.transferTo(Paths.get(filePath2 + imageName));
            }
            else {
                String message = "Just accept .jpg or .png file";
                model.addAttribute("messageFile", message);
                return "change-profile";
            }
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
        userService.updateUser(param);
        return "redirect:/user/home";
    }

    @RequestMapping("/add-school")
    public String addSchool(Model model, @RequestParam("index") String index){
        model.addAttribute("user", new UserDto());
        model.addAttribute("indexSchool", index);
        return "add-school";
    }

    @GetMapping("/403")
    public String getAccessDeniedPage() {
        return "/error/403";
    }
}
