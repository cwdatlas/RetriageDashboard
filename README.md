## Welcome to The Mock Mass Casualty Event Dashboard 
### For The Carroll College Nursing Program

### Hardware Requirements

- Threads: 2
- Memory:  4-8GB
- Storage: 100GB

### Software Versions
- Linux Version: Ubuntu Server 24.04.1 LTS
- React Version: 19
- Spring Framework Version: 3.4.2
- Mysql Version: 9.2.0
- Java Version: 21 (21.0.3)
- JavaScript Version: ECMAScript 2023
- HTML Version:
- CSS Version:

### Running Instructions
#### Database Creation (Container)
If you are using podman instead of docker, make sure to replace docker with podman in the below command
- docker run --name retriage -e MYSQL_ROOT_PASSWORD=secretPass -d mysql:9.2.0 --expose=3306