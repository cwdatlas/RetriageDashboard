# Use Cases
### Retriage Nurse
#### Description
The Retriage Nurse takes patients and directs them to a resource like MRI or surgery.
1. Log in via carroll college nursing okta account. Nurse's name will be saved at first login.
2. Option to change own name from personal profile page
3. CRUD permissions for patient accounts
4. Add a patient account to a resource queue, such as an MRI machine, CT machine, ambulance, or specialty office
5. Log movement of patients to resources, and patient CRUD events with a timestamp and the responsible nurse
6. Scan patient's triage tag to fill user ID during patient creation

### Administrator / Director
#### Description
The Director will create, start, stop and manage the Mass Casualty Event.
When creating the event, they create resources like MRI or ambulance then set the number of those resources available.
After creating or selecting a default resource,
the Director will confirm the number of Retriage Nurses participating,
the accounts they are associated with and length of time the event will last.
#### Account Management
1. Ability to log into and out of an Administrator/Director account.
2. Ability to complete CRUD actions on Retriage Nurse profiles.
#### Event Organization
1. Ability to complete CRUD operations on resources during event creation.
2. resource values include resource name, reusable status, number of non-reusable resources, queue max length and resource wait time.
3. Ability to start and stop event timer though event page.
4. Be able to review the activities of the Retriage Nurse in realtime and in the form of logs

### Security
Security will be handled by Okta.
The reason for this is twofold:
- The ease of implementation.
- In the case that the Mock Mass Casualty Event expands past Carroll College,
  it will be able to integrate more organizations.

Details,
Use cases

### Living Schedule: (Weekly Goals)
- ~~2/14~~
    - ~~React front end, Spring Back end and Mysql database running and talking to each other.~~
    - ~~Persist a patient account from the front end to the database and back.~~
    - ~~Solidify project schedule and detailed use cases.~~
- 2/21
    - Create Models for resources, Retriage Nurses, Directors ~~and Patients~~
        - All resources will be built from the resource model then populated with predefined data.
    - Build Front page, Login, and event page and profile page
        - The Home page requires login button and instructions on how to use the application
        - Login will need to use okta. CCIT must designate who is a Director, but Directors choose who can be a Retriage Nurse
        - Event page must provide a view of active resources, Patient creation and time left in the event.
        - Profile page must include username change and information we can find from okta integration
        - **Functionality** is the focus for this week, **not style**.
- 2/28
    - Create Event Creation Page
        - Event Creation page is only accessible by the Director and can be accessed from the home page.
        - The Event Creation page allows the Director to choose between a set of predefined resources:
            - Ambulance, MRI, Surgery
        - The Event creation page starts the event so a timer must start when the event starts.
    - Build logging infrastructure
        - Logging takes place when an object is created or changed by a Director or Retriage Nurse.
            - Example: Patient 1234 is moved to MRI queue at 2:30pm 4/17/2025
        - Logging will be saved to a text file and output to Director's Event Page
- 3/7
    - Add correct organization, colors and other esthetic changes to webapp
    - Get in contact with Jeff Wald for help and guidance with first attempt at Carroll Design
- 3/14
    - Continue to update the style of the application.
- 3/21
    - TBD -- Wiggle Room --
- 3/28
    - Deployment of Application
- 4/4
    - Create online documentation for students in the future.
    - validate that all code is effectively commented and correct javadocs annotations
- 4/11
    - Build Pamphlet Director and Retriage Nurses can use to understand how to use the application
- deadline by april 17th