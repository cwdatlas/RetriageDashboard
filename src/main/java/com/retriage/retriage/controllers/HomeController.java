package com.retriage.retriage.controllers;

import com.retriage.retriage.enums.Role;
import com.retriage.retriage.models.User;
import com.retriage.retriage.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * HomeController.java
 * <br></br>
 * Controller for handling requests to the home page after successful SAML authentication.
 * This controller is responsible for displaying user information retrieved from the
 * {@link Saml2AuthenticatedPrincipal} after the user has been authenticated via SAML.
 *
 * @Author: John Botonakis
 * @Resource: With help provided by Matt Raible (https://developer.okta.com/blog/2022/08/05/spring-boot-saml)
 */
@Controller
@CrossOrigin
public class HomeController {
    AuthenticationPrincipal Saml2AuthenticatedPrincipal;
    UserService userService;

    HomeController(UserService userService) {
        this.userService = userService;
    }

    /**
     * home Mapping
     * <br></br>
     * Handles requests to the root path ("/") and displays the home page with user information.
     * <p>
     * This method is invoked when a user accesses the application's root URL. It retrieves the
     * {@link Saml2AuthenticatedPrincipal} representing the authenticated user, extracts user details
     * such as name, email address, and all attributes from the principal, and adds them to the
     * model for rendering in the "home" view.
     *
     * @param principal The {@link Saml2AuthenticatedPrincipal} representing the authenticated SAML user.
     *                  This is injected by Spring Security based on the current authentication context.
     * @param response  The Spring {@link Model} object used to add attributes for rendering in the view.
     * @return The name of the view to be rendered, which is "home" in this case.
     * This corresponds to the `home.html` (or similar) template file.
     */
    @RequestMapping("/")
    public String oktaLogin(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal, HttpServletResponse response) {
        List<String> roles = principal.getAttribute("groups");
        Role userRole = Role.Guest;

        if (roles != null) {
            for (Role role : Role.values()) {
                if (roles.contains(role.toString())) {
                    userRole = role;
                    break;
                }
            }
        }
        String firstName = principal.getFirstAttribute("firstname");
        String lastName = principal.getFirstAttribute("lastname");
        String username = principal.getName();
        if (firstName != null && lastName != null) {
            username = firstName + lastName;
        }

        String email = principal.getFirstAttribute("email");
        //There should always be an email
        if (email == null) {
            //maybe throw an error or redirect because there should ALWAYS be an email
            email = "guest.email.com";
        }

        if (userService.getUserByEmail(email) == null) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setRole(userRole);
            userService.saveUser(newUser);
        }

        Cookie nameCookie = new Cookie("username", username);
        nameCookie.setPath("/");
        nameCookie.setDomain("localhost");
        nameCookie.setHttpOnly(false);
        nameCookie.setSecure(false);
        response.addCookie(nameCookie);
        Cookie roleCookie = new Cookie("role", userRole.toString());
        roleCookie.setPath("/");
        roleCookie.setDomain("localhost");
        roleCookie.setHttpOnly(false);
        roleCookie.setSecure(false);
        response.addCookie(roleCookie);
        Cookie emailCookie = new Cookie("email", email);
        emailCookie.setPath("/");
        emailCookie.setDomain("localhost");
        emailCookie.setHttpOnly(false);
        emailCookie.setSecure(false);
        response.addCookie(emailCookie);

        return "redirect:http://localhost:3000/";
    }
}