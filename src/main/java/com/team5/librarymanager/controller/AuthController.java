package com.team5.librarymanager.controller;


import com.team5.librarymanager.entity.User;
import com.team5.librarymanager.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping({"/", "/login"})
    public String showLogin() {
        return "login";
    }

    @GetMapping("/logout")
    public String doLogout(HttpSession ses) {
        ses.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/auth")
    public String doLogin(@RequestParam("uname") String uname, @RequestParam("password") String password, Model model, HttpSession session) {

        User acc = userService.auth(uname, password);
        if (acc == null) {
            model.addAttribute("error", "Invalid credentials!!!");
            return "login";
        }
        session.setAttribute("loggedInUser", acc);
        return "redirect:/books";
    }

}
