package com.example.TastyKing.controller;

import com.example.TastyKing.entity.User;
import com.example.TastyKing.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @Autowired
    private AuthenticationService authenticationService;
    @GetMapping("/redirectWithRedirectView")
    public RedirectView redirectWithUsingRedirectView(RedirectAttributes attributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String email = oauthToken.getPrincipal().getAttribute("email");
        String name = oauthToken.getPrincipal().getAttribute("name");

        // Save user details to the database
        User user = authenticationService.saveOAuth2User(email, name);

        // Generate JWT token
        String token = authenticationService.generateToken(user);

        // Add token as an attribute
        attributes.addAttribute("token", token);

        // Set the redirect URL
        String redirectUrl = "http://localhost:63343/TastyKing-FE/index.html";

        // Redirect to the front-end with the token
        return new RedirectView(redirectUrl);
    }

}