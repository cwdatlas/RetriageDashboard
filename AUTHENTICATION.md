# AUTHENTICATION.md 
Authored by: John Botonakis  
Help received from Google Gemini for accurateness  
This document explains how the web application and Okta work together to log you in. There is both
a step-by-step guide, along with a more technical explanation at the bottom.

## Authentication Procedure between Okta and the Web Application (Simplified)
### **Step 1: User Tries to Access the Web Application**
When you open your web browser and go to the web app's address, you are trying to access its content.

### **Step 2: Web Application Asks Okta to Log You In**
The web application notices you aren't logged in yet, so it sends your browser to Okta to handle the login process.

### **Step 3: User Logs in on Okta**
Your web browser is automatically sent to Okta's login page. 
On Okta's website, you enter your username and password (or use other login methods Okta provides) to prove who you are. 
Okta verifies your login details.

### **Step 4: Okta Creates a Login "Document"**
Once you are successfully logged in, Okta creates a special digital "document" that confirms your identity. 
This document is called a **_SAML Response_**.

### **Step 5: Okta Sends You Back to the Web Application with the "Document"**
Okta sends your browser back to the web application. Along with this redirect, it includes the digital "document" it created.

### **Step 6: Web Application Checks the "Document"**
The web application receives the "document" from Okta and carefully checks if it's valid and truly from Okta. This verifies your login from Okta.

### **Step 7: Web Application Logs You In**
If the "document" is valid, the web application understands you are properly logged in by Okta and grants you access to the application.

### **Step 8: User Accesses the Web Application**
You are now logged into the web application! You can now use all the features and content it provides, knowing the application trusts your login from Okta.

### **Step 9: (Optional) User Logs Out**
When you are finished, you can log out of the web application. This may also log you out of Okta depending on the setup (Single Logout).


## Technical Explanation:
### How Okta and this Web App Speak
The authentication flow leverages Service Provider (SP) initiated Single Sign-On (SSO) using SAML 2.0 protocol. 
When an unauthenticated user attempts to access a protected resource on the web application (the SP), the
application redirects the user's browser to the configured Identity Provider (IdP), in this case, Okta. 
This redirection includes a SAML Authentication Request, detailing the SP's requirements. 
The user is then presented with Okta's login interface where they authenticate using their Okta credentials.

### How Okta Authenticates
Upon successful authentication at Okta, the IdP generates a SAML Response containing assertions about the 
user's identity and attributes. This SAML Response is then delivered back to the web application via a browser 
redirect, typically using HTTP-POST binding.  The Spring Security SAML Relying Party library in the application
intercepts this response, validates the SAML signature against the IdP's public certificate (obtained from metadata), 
and verifies the assertions. If validation is successful, a Saml2Authentication object is created, representing the 
authenticated principal, and a Spring Security session is established, granting the user access to the application's 
resources based on configured authorization rules and extracted SAML attributes, which can be mapped to 
Spring Security Granted Authorities.