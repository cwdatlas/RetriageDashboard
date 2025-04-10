package com.retriage.retriage.controllers;
import com.retriage.retriage.enums.Role;
import com.retriage.retriage.exceptions.ErrorResponse;
import com.retriage.retriage.models.User;
import com.retriage.retriage.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HomeControllerTest {
    private UserService userService;
    private HomeController homeController;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        response = mock(HttpServletResponse.class);
        homeController = new HomeController(userService);
    }

    @Test
    void oktaLogin_ShouldCreateNewUserAndRedirect_WhenValidAttributesProvided() {
        Saml2AuthenticatedPrincipal principal = mock(Saml2AuthenticatedPrincipal.class);
        when(principal.getAttribute("groups")).thenReturn(List.of("Admin"));
        when(principal.getFirstAttribute("firstname")).thenReturn("John");
        when(principal.getFirstAttribute("lastname")).thenReturn("Doe");
        when(principal.getFirstAttribute("email")).thenReturn("john@example.com");

        when(userService.getUserByEmail("john@example.com")).thenReturn(null); // New user

        ResponseEntity<?> responseEntity = homeController.oktaLogin(principal, response);

        assertEquals(302, responseEntity.getStatusCodeValue());
        assertTrue(responseEntity.getHeaders().get("Location").contains("http://localhost:3000/"));

        verify(userService, times(1)).saveUser(any(User.class));
        verify(response, times(4)).addCookie(any(Cookie.class));
    }

    @Test
    void oktaLogin_ShouldReturnForbidden_WhenRolesAreMissing() {
        Saml2AuthenticatedPrincipal principal = mock(Saml2AuthenticatedPrincipal.class);
        when(principal.getAttribute("groups")).thenReturn(null);

        ResponseEntity<?> responseEntity = homeController.oktaLogin(principal, response);

        assertEquals(403, responseEntity.getStatusCodeValue());
        assertInstanceOf(ErrorResponse.class, responseEntity.getBody());

        ErrorResponse body = (ErrorResponse) responseEntity.getBody();
        assertEquals("ACCESS_DENIED", body.getErrorCode());
        assertEquals(403, body.getStatusCode());
        assertTrue(body.getMessages().contains("Access denied due to missing role."));
    }

    @Test
    void oktaLogin_ShouldReturnUnauthorized_WhenEmailIsMissing() {
        Saml2AuthenticatedPrincipal principal = mock(Saml2AuthenticatedPrincipal.class);
        when(principal.getAttribute("groups")).thenReturn(List.of("Guest"));
        when(principal.getFirstAttribute("firstname")).thenReturn("Guesty");
        when(principal.getFirstAttribute("lastname")).thenReturn("McGuestFace");
        when(principal.getFirstAttribute("email")).thenReturn(null);

        ResponseEntity<?> responseEntity = homeController.oktaLogin(principal, response);

        assertEquals(401, responseEntity.getStatusCodeValue());
        assertInstanceOf(ErrorResponse.class, responseEntity.getBody());

        ErrorResponse body = (ErrorResponse) responseEntity.getBody();
        assertEquals("MISSING_EMAIL", body.getErrorCode());
        assertEquals(401, body.getStatusCode());
        assertTrue(body.getMessages().contains("Email not provided."));
    }


    @Test
    void oktaLogin_ShouldReturnUnauthorized_WhenNameAttributesMissing() {
        Saml2AuthenticatedPrincipal principal = mock(Saml2AuthenticatedPrincipal.class);
        when(principal.getAttribute("groups")).thenReturn(List.of("Guest"));
        when(principal.getFirstAttribute("firstname")).thenReturn(null);
        when(principal.getFirstAttribute("lastname")).thenReturn(null);
        when(principal.getFirstAttribute("email")).thenReturn("guest@example.com");

        when(userService.getUserByEmail("guest@example.com")).thenReturn(null);

        ResponseEntity<?> responseEntity = homeController.oktaLogin(principal, response);

        assertEquals(401, responseEntity.getStatusCodeValue());
        assertTrue(responseEntity.getBody() instanceof ErrorResponse);

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("AUTHENTICATION_ERROR", errorResponse.getErrorCode());
        assertEquals(401, errorResponse.getStatusCode());
        assertTrue(errorResponse.getMessages().contains("First name not provided."));
        assertTrue(errorResponse.getMessages().contains("Last name not provided."));

        verify(userService, never()).saveUser(any());
    }

}
