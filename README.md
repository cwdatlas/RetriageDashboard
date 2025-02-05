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
- HTML Version: **TBA**
- CSS Version: **TBA**

### Running Instructions
#### Database Creation (Container)
If you are using podman instead of docker, make sure to replace docker with podman in the below command
- `docker run --name retriage -e MYSQL_ROOT_PASSWORD=secretPass -d mysql:9.2.0 --expose=3306`


## Use Cases
### Retriage Nurse
- Ability to log into a Retriage Nurse account
- Ability to add, remove, or otherwise change an admitting patient
- Allocate a patient to a particular service, 
such as an MRI machine, CT machine, ambulance, or specialty office
- Log time, patient information, and update accordingly
- Manage hospital resources

### Administrator / Director
- Ability to log into a Administrator/Director account
- Be able to review the activities of the Retriage Nurse in realtime and in the form of logs
- Ability to start, stop, monitor, and set up the environment


## Security
For this project, security will be handled by Okta which is what is used here at Carroll. 
The reason for this, is twofold:
- The ease of implementation, meaning that Okta will handle most authentication
- This project will expand beyond Carroll the next time the Mass Casualty Event is held, 
and using Okta will provide similar functionality both on and off campus