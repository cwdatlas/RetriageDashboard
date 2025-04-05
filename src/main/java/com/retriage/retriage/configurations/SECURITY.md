# SECURITY

## How Security Works
1. When a User logs in, they get directed to Okta for authentication
2. Okta returns a SAML assertion to the back end
3. Spring then extracts user details from that assertion
4. Backend issues a JWT for API/ general access
5. Frontend should use that JWT for authorization on protected endpoints
6. All requests to the backend will include JWT in the authorization header
7. Spring security verifies and validates the token before allowing access 
