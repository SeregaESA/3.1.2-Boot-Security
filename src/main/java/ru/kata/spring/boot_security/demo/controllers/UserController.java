package ru.kata.spring.boot_security.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.Services;

import java.util.List;


@Controller
public class UserController {

    @Autowired
    private Services service;

    @GetMapping("/user")
    public String user(Model model) {
        model.addAttribute("user", service.oneUser());
        return "oneUser";
    }

    @GetMapping("/admin/")
    public String admin(Model model) {
        model.addAttribute("user", service.getAllUsers());
        return "users";
    }

    @GetMapping(value = "/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        return "newUser";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute User user, @RequestParam("role") List<String> roles) {
        service.save(service.createUser(user, roles));
        return "redirect:/admin/";
    }

    @GetMapping(value = "/delete")
    public String delete(@RequestParam("id") long id) {
        service.delete(id);
        return "redirect:/admin/";
    }

    @GetMapping(value = "/edit")
    public String edit(@RequestParam(value = "id") long id, Model model) {
        model.addAttribute("user", service.getOne(id));
        return "edit";
    }

    @PostMapping(value = "/update")
    public String update(@ModelAttribute("user") User user, @RequestParam("id") long id, @RequestParam(value = "role") List<String> roles) {
        service.update(id, service.updateUser(user, roles, id));
        return "redirect:/admin/";
    }
}
