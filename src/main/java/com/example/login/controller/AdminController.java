package com.example.login.controller;

import com.example.login.model.dto.UserDto;
import com.example.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private SessionRegistry sessionRegistry;

    @RequestMapping( "/admin/home")
    public String viewMainPageAdmin(){
        return "/admin/admin-home-page";
    }

    @RequestMapping("/admin/login")
    public String viewAdminLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            return "loginAdmin";
        if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("USER")))
            return "redirect:/user/home";
        return "redirect:/admin/home";
    }

    @GetMapping("admin/add-admin")
    public String addAdmin(Model model){
        model.addAttribute("admin", new UserDto());
        return "admin/add-admin";
    }

    @PostMapping(path = "/admin/add-admin", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String processAddAdmin(@Valid @ModelAttribute(name = "admin") UserDto userDto, BindingResult bindingResult,
                                    Model model, @RequestPart("img") MultipartFile multipartFile) throws IOException {
        if (bindingResult.hasErrors()){
            return "admin/add-admin";
        }
        if (!multipartFile.isEmpty()) {
            String tail = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().length() - 4);
            if (tail.equals(".jpg") || tail.equals(".png")) {
                String imageName = UUID.randomUUID() + ".jpg";
                String filePath1 = Paths.get("").toAbsolutePath() + "/target/classes/static/images/";
                String filePath2 = Paths.get("").toAbsolutePath() + "/src/main/resources/static/images/";
                userDto.setImage(imageName);
                multipartFile.transferTo(Paths.get(filePath1 + imageName));
                multipartFile.transferTo(Paths.get(filePath2 + imageName));
            } else {
                String message = "Just accept .jpg or .png file";
                model.addAttribute("messageFile", message);
                return "admin/add-admin";
            }
        }
        userService.createAdmin(userDto);
        return "redirect:/admin/add-admin?success";
    }

    @GetMapping("/admin/update-admin/{id}")
    public String updateAdmin(@PathVariable(name = "id")int id, Model model){
        model.addAttribute("admin", userService.findAdminById(id));
        return "admin/update-admin";
    }

    @PostMapping(path = "/admin/update-admin", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String processUpdateAdmin(@Valid @ModelAttribute("admin")UserDto userDto, BindingResult bindingResult, @RequestPart("img") MultipartFile multipartFile, Model model) throws IOException {
        if (bindingResult.hasErrors()){
            return "admin/update-admin";
        }
        if (!multipartFile.isEmpty()) {
            String tail = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().length() - 4);
            if (tail.equals(".jpg") || tail.equals(".png")) {
                String imageName = userDto.getImage();
                if (imageName == null || imageName.equals("")){
                    imageName = UUID.randomUUID() + ".jpg";
                    userDto.setImage(imageName);
                }
                String filePath1 = Paths.get("").toAbsolutePath() + "/target/classes/static/images/";
                String filePath2 = Paths.get("").toAbsolutePath() + "/src/main/resources/static/images/";
                multipartFile.transferTo(Paths.get(filePath1 + imageName));
                multipartFile.transferTo(Paths.get(filePath2 + imageName));
            } else {
                String message = "Just accept .jpg or .png file";
                model.addAttribute("messageFile", message);
                return "admin/update-admin";
            }
        }
        userService.updateAdmin(userDto);
        return "redirect:/admin/list-admin";
    }

    @PostMapping("/admin/change-password")
    public String changPassword(@Valid @ModelAttribute("admin")UserDto userDto, @RequestParam("rePassword")String rePassword, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()){
            return "admin/update-admin";
        }
        if (!userDto.getPassword().equals(rePassword)){
            model.addAttribute("message", "Password doesn't match !");
            return "admin/update-admin";
        }
        userService.changeAdminPassword(userDto);
        return "redirect:/admin/list-admin";
    }

    @GetMapping("/admin/list-admin")
    public String listAllAdmin(Model model){
        List<UserDto> listAdmins = userService.listAllAdmin();
        model.addAttribute("listAdmin", listAdmins);
        return "admin/list-all-admins";
    }

    @GetMapping(value = "/admin/delete-admin")
    @ResponseBody
    public String deleteAdmin(@RequestParam("id") int id){
        if (userService.countAdmin() < 2) {
            return "Cannot delete admin !";
        }
        UserDto userDto = userService.findAdminById(id);
        List<String> users = sessionRegistry.getAllPrincipals().stream().filter(o ->
            !sessionRegistry.getAllSessions(o, false).isEmpty()).map(Object::toString).collect(Collectors.toList());
        users.forEach(o -> {
            List<SessionInformation> sessions = sessionRegistry.getAllSessions(o, false);
            sessions.forEach(session -> {
                User user = (User) session.getPrincipal();
                if (user.getUsername().equals(userDto.getUsername())){
                    session.expireNow();
                    sessionRegistry.removeSessionInformation(session.getSessionId());
                }
            });
        });
        userService.deleteAdmin(id);
        return "Delete success !";
    }

    @GetMapping("/admin/list-user")
    public String listAllUser(Model model){
        List<UserDto> listUsers = userService.listAllUser();
        model.addAttribute("listUser", listUsers);
        return "admin/list-user";
    }

    @GetMapping("/admin/enable-user")
    @ResponseBody
    public void enableUser(@RequestParam("id") int id){
        userService.enableUserById(id);
    }

    @GetMapping("/admin/lock-user")
    @ResponseBody
    public void lockUser(@RequestParam("id") int id){
        userService.lockUserById(id);
    }

}
