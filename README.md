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
**Required Environment Variables**
DOMAIN example: localhost or 10.0.0.10
`java -jar /build/libs/retriage-0.0.1-SNAPSHOT.jar`

Connect to localhost:8080, login and have fun.
`crtl^c` to stop application