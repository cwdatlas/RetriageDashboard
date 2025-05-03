## Welcome to The Mock Mass Casualty Event Dashboard

### For The Carroll College Nursing Program

### Hardware Requirements

- Threads: 2
- Memory:  4-8GB
- Storage: 100GB

### Software Versions

- Linux Version: Ubuntu Server 24.04.1 LTS
- Next.js Version: 15
- React Version: 19.0.1
- TypeScript Version: 5.7.3
- ESLint Version: 9.20
- Spring Framework Version: 3.4.2
- Mysql Version: 9.2.0
- Java Version: 21 (21.0.3)
- JavaScript Version: ECMAScript 2023

### Running Instructions

### SSL
SSL requires a cert in the main/resources folder.
Create a self-signed certificate named retriage.p12 in the main/resources/keystore/ folder

### Okta Integration
There are four pieces of data that are needed before okta is set up.
1. certificate
2. key
3. Audience URI
4. Metadata URI

You will get these pieces of data from your intended app in your okta dev portal.
Make sure that the below lines have the correct file names set in application.properties. exp: local.crt or local.key
`spring.security.saml2.relyingparty.registration.okta.signing.credentials[0].private-key-location=classpath:`
`spring.security.saml2.relyingparty.registration.okta.signing.credentials[0].certificate-location=classpath`

#### Database Creation (Container)
#### Docker
Open a terminal and run the following command to create a docker container:  
`docker run --detach -p 3306:3306 --name retriage_database -e MYSQL_ROOT_PASSWORD=secretPass -e MYSQL_DATABASE=retriageDashboard -e MYSQL_USER=backend -e MYSQL_PASSWORD=secretPass -d mysql:9.2.0`

#### Podman
Open a terminal and run the following command to create a Podman container:  
`podman run --detach -p 3306:3306 --name retriage_database -e MYSQL_ROOT_PASSWORD=secretPass -e MYSQL_DATABASE=retriageDashboard -e MYSQL_USER=backend -e MYSQL_PASSWORD=secretPass -d mysql:9.2.0`

### Static Files
Navigate to /RetriageFrontEnd 
`cd RetriageFrontEnd`

Run the build process that creates all static files.
`npm run build`

Build the applications jar by typing:
`./gradlew build`

Now you are ready to start the spring backend. 

**Default Required Environment Variables**
DOMAIN=localhost
KEYSTOREPASS
AUDIENCEURI
METADATAURI
DATABASEPASS=secretPass

Run the command below to start the application
`java -jar /build/libs/retriage-0.0.1-SNAPSHOT.jar`

Connect to https://localhost, Login with a user you have created on your okta dev account.
Users can have one of three groups:
- Director
- Nurse
- Guest

Give your user the Director group, so they can edit, start, stop and delete events.
Give your user the Nurse group, so they can only create and move patients.
Give your user the Guest group, so they can only view patients and where they are.

`crtl^c` to stop application