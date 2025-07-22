package com.team5.librarymanager.controller;

import com.team5.librarymanager.entity.Category;
import com.team5.librarymanager.entity.User;
import com.team5.librarymanager.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String showCategories(Model model, HttpSession session){

        User user = (User) session.getAttribute("loggedInUser");

        if(user == null){
            return "redirect:/login";
        } else if (!user.getRole().equals("admin")) {
            return "redirect:/books";
        }

        model.addAttribute("cates", categoryService.findAll());

        return "/categories";
    }

    @PostMapping("/categories/save")
    public String save(@Valid @ModelAttribute("cate") Category cate, BindingResult result, Model model, @RequestParam(
            "formMode") String formMode) {
        if (result.hasErrors()) {

            model.addAttribute("cates", categoryService.findAll() );

            model.addAttribute("formMode", formMode);

            return "categories-form";
        }

        if (formMode.equals("new")) {
            if (categoryService.existsCategory(cate.getName())) {
                model.addAttribute("cates", categoryService.findAll() );

                model.addAttribute("formMode", formMode);
                model.addAttribute("duplicated", "Duplicated name. Input another one!");
                return "category-form";
            }
        }

        categoryService.save(cate);

        return "redirect:/categories";
    }

    @GetMapping("/categories/add")
    public String showCategoryForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("cate", new Category());

        model.addAttribute("formMode", "new");

        return "/category-form";
    }
    

    @GetMapping("/categories/edit/{id}")
    public String editCategory(Model model, HttpSession session, @PathVariable Long id){
        User user = (User) session.getAttribute("loggedInUser");
        if(user == null){
            return "redirect:/login";
        }
        Category category = categoryService.findById(id).get();

        model.addAttribute("cate", category);

        model.addAttribute("formMode", "edit");

        return "/category-form";
    }

    @GetMapping("/categories/delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpSession ss) {

        User acc = (User) ss.getAttribute("loggedInUser");
        if (acc == null) {
            return "redirect:/login";
        }

        if (!acc.getRole().equals("admin")) {
            return "redirect:/books";
        }

        categoryService.deleteById(id);

        return "redirect:/categories";
    }
}
