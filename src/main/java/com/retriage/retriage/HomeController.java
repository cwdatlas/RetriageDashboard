package com.retriage.retriage;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *  Controller for handling requests to the home page after successful SAML authentication.
 *  This controller is responsible for displaying user information retrieved from the
 *  {@link Saml2AuthenticatedPrincipal} after the user has been authenticated via SAML.
 */
@Controller
public class HomeController {
    AuthenticationPrincipal Saml2AuthenticatedPrincipal;
    /**
     *  Handles requests to the root path ("/") and displays the home page with user information.
     *  <p>
     *  This method is invoked when a user accesses the application's root URL. It retrieves the
     *  {@link Saml2AuthenticatedPrincipal} representing the authenticated user, extracts user details
     *  such as name, email address, and all attributes from the principal, and adds them to the
     *  model for rendering in the "home" view.
     *
     * @param principal The {@link Saml2AuthenticatedPrincipal} representing the authenticated SAML user.
     *                  This is injected by Spring Security based on the current authentication context.
     * @param model     The Spring {@link Model} object used to add attributes for rendering in the view.
     * @return          The name of the view to be rendered, which is "home" in this case.
     *                  This corresponds to the `home.html` (or similar) template file.
     */
    @RequestMapping("/")
    public String home(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal, Model model) {
        // Add the user's name to the model, retrieved from the Saml2AuthenticatedPrincipal
        model.addAttribute("name", principal.getName());
        // Add the user's email address to the model, retrieved from the "email" attribute of the principal
        model.addAttribute("emailAddress", principal.getFirstAttribute("email"));
        // Add all user attributes from the principal to the model for display
        model.addAttribute("userAttributes", principal.getAttributes());
        // Return the name of the home view template (e.g., home.html)
        return "home";
    }


}