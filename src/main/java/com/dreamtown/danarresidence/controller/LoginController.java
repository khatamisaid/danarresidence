package com.dreamtown.danarresidence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dreamtown.danarresidence.entity.Website;
import com.dreamtown.danarresidence.repository.WebsiteRepository;

@Controller
public class LoginController {

    @Autowired
    private WebsiteRepository websiteRepository;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) throws Exception {
        Website web = websiteRepository.findAll().get(0);
        if (isAuthenticated()) {
            return "redirect:/admin";
        }
        model.addAttribute("web", web);
        return "auth/login";
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }
}
