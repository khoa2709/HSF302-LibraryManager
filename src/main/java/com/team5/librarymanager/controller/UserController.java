package com.team5.librarymanager.controller;

import com.team5.librarymanager.entity.User;
import com.team5.librarymanager.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String showUsers(@RequestParam(value = "keyword", required = false, defaultValue = "") String kw, Model model, HttpSession session){

        User user = (User) session.getAttribute("loggedInUser");

        if(user == null){
            return "redirect:/login";
        } else if (!user.getRole().equals("admin")) {
            return "redirect:/books";
        }

        if (kw.equals("")) {
            model.addAttribute("users", userService.findAll());
        } else {
            model.addAttribute("users", userService.searchUsers(kw));
        }

        return "users";
    }

    @PostMapping("/users/save")
    public String save(@Valid @ModelAttribute("user") User user, BindingResult result, Model model, @RequestParam(
            "formMode") String formMode) {
        if (result.hasErrors()) {

            model.addAttribute("users", userService.findAll() );

            model.addAttribute("formMode", formMode);

            return "user-form";
        }

        if (formMode.equals("new")) {
            if (userService.existsByUsername(user.getUsername())) {
                model.addAttribute("users", userService.findAll() );

                model.addAttribute("formMode", formMode);
                model.addAttribute("duplicated", "Duplicated username. Input another one!");
                return "user-form";
            }
        }

        user.setCreatedAt(LocalDateTime.now());
        userService.save(user);

        return "redirect:/users";
    }

    @GetMapping("/users/add")
    public String showCategoryForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        if(!user.getRole().equals("admin")) {
            return  "redirect:/books";
        }

        model.addAttribute("user", new User());

        model.addAttribute("formMode", "new");

        return "user-form";
    }


    @GetMapping("/users/edit/{id}")
    public String editCategory(Model model, HttpSession session, @PathVariable Long id){
        User user = (User) session.getAttribute("loggedInUser");
        if(user == null){
            return "redirect:/login";
        }

        if(!user.getRole().equals("admin")) {
            return  "redirect:/books";
        }

        User editUser = userService.findById(id).get();

        model.addAttribute("user", editUser);

        model.addAttribute("formMode", "edit");

        return "user-form";
    }

    @GetMapping("/users/delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpSession ss) {

        User acc = (User) ss.getAttribute("loggedInUser");
        if (acc == null) {
            return "redirect:/login";
        }

        if (!acc.getRole().equals("admin")) {
            return "redirect:/books";
        }

        userService.deleteById(id);

        return "redirect:/users";
    }

}


