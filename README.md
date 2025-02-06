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
- `docker run -p 3306:3306 --name retriage -e MYSQL_ROOT_PASSWORD=secretPass -d mysql:9.2.0`


## Use Cases
### Retriage Nurse
#### Account Management
- Ability to admit or create a patient file with all relevant information such as name, date of birth,
current condition (Denoted by a color i.e. Red for Critical, Black for dead).
- Ability to log into and out of a triage account with a username and password setup through Okta services
- Ability to update a patient's file given any conditional or placement changes through the use of a GUI

#### Patient Interaction
- Ability to assign or remove a patient to an open bed or service such as 
CT Machine, MRI, Ambulance or Specialty Office through the use of a GUI.
- Ability to log the time of any patient changes through the use of time stamps in each action

### Administrator / Director
#### Account Management
- Ability to log into a Administrator/Director account
- Be able to review the activities of the Retriage Nurse in realtime and in the form of logs
- Ability to start, stop, monitor, and set up the environment


## Security
For this project, security will be handled by Okta which is what is used here at Carroll. 
The reason for this, is twofold:
- The ease of implementation, meaning that Okta will handle most authentication
- This project will expand beyond Carroll the next time the Mass Casualty Event is held, 
and using Okta will provide similar functionality both on and off campus