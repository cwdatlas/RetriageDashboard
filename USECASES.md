//Only One event at a time
//What properties are required to create a patient
    //Internal ID vs local ID (Database vs Reused patient ID)
//Patient properties (Name, Condition, ID, Bed Assignment, Queue Assignment)
//Patient x Resource development
//Deleting a Patient verbose explanation
//Resource scheduler (Automatic or manual?) 
    //What happens when A patient is removed before the operation is completed atomically
//Simplify data model
    //We made UML whoops
//Do the normal things we said we're going to do

//Okta Actors
    //

# Use Cases

### Retriage Nurse

#### Description

The Retriage Nurse takes patients and directs them to a medical resources like MRI or surgery. They create, update and
delete
patients. All their actions are logged on the back end.

1. Nurse has CRUD permissions for patient accounts
2. IF the nurse access the Event page and there is not an active event then:
    - The page will read "There is no event at this time, get the Director to start the event"
3. If the nurse accesses the Event page if there is an active event they will be able to
    - create a patient via a button in the top left corner of the page.
    - Update a patient by right-clicking the patient tile shown in the unallocated patients box at the top of the page.
    - Move patients to medical resources via drag and drop
    - Delete a patient via right-click and selecting the delete option
4. When an patient is created the nurse adds the medical resources the patient needs.
    - Those resources are added to a queue in the patient object.
    - THe patient will move through those resources based on a timer at each medical service
5. Logging of each change to patients and nurses occurs starting in the services for each model in the backend
    - Logging starts with a timestamp then nurse that updated the specific object, then the object and what was changed
6. (stretch goal) Nurse can Scan patient's triage tag to fill user ID during patient creation when in user creation
   popup.

### Administrator / Director

#### Description

The Director will create, start, stop and manage the Mass Casualty Event.
When creating the event, they create resources like MRI or ambulance then set the number of those resources available.
After creating or selecting a default resource,
the Director will confirm the number of Retriage Nurses participating via email address,
the accounts they are associated with and length of time the event will last.

#### Event Organization

1. Directors can access the Event page when there is not an event, then click a link to the event creation page
    - The event page will say "There is no event at this time, click here to start an event" if the user is a director
    - The event creation page will have inputs for event name, the time the event will take, and the medical resources
      used in the event
    - There will be a template of "Ambulance", "MRI", "Surgeon", "ICU" and "Create New" for the Director to choose from.
    - Medical resources are chosen by selecting the resource from a dropdown then clicking a plus symbol to select from
      the dropdown again
    - "Create New" will open a form that includes "Name", "Patient Process Time", "Max Number of Patients in Queue",
      and "Is Reusable Resource"
3. When the event creation page is filled out the Director can click the create event button at the bottom of the page
    - In the header of the Directors event page there will be a button to stop and start the event
    - A timer runs on the backend which is started and stopped and is checked against to end the event when time is up.
4. Print event logs to a txt file and send them to Director when event completes.

### Security

Security will be handled by Okta.
The reason for this is twofold:

- The ease of implementation.
- In the case that the Mock Mass Casualty Event expands past Carroll College,
  it will be able to integrate more organizations.

1. When a person access the website they will be redirected to an okta login screen from the front end
    - Front end implementation still in progress

//TODO Reevaluate schedule

### Living Schedule: (Weekly Goals)

- ~~2/14~~
    - ~~React front end, Spring Back end and Mysql database running and talking to each other.~~
    - ~~Persist a patient account from the front end to the database and back.~~
    - ~~Solidify project schedule and detailed use cases.~~
- 2/21
    - Create Models for resources, Retriage Nurses, Directors ~~and Patients~~
        - All resources will be built from the resource model then populated with predefined data.
    - Build Front page, and event page
        - Login will need to use okta. CCIT must designate who is a Director
        - Event page must provide a view of active resources, Patient creation and time left in the event.
        - **Functionality** is the focus for this week, **not style**.
- 2/28
    - Create Event Creation Page
        - Event Creation page is only accessible by the Director and can be accessed from the home page.
        - The Event Creation page allows the Director to choose between a set of predefined resources:
            - Ambulance, MRI, Surgery
- 3/7
    - The Event creation page starts the event so a timer must start when the event starts.
    - On the Event Creation page there will be a section to the right side of the page will have the resource template creation
      - The resource template creation side has all but active as a savable variable
      - The created templates need to be added to the event that is being made. Events are only added during creation time
      - Events will be added with a corresponding int that represents the number of those events available
    - The Event display page will display the current events attributes like 
      John:
        - Build logging infrastructure
          - Logging takes place when an object is created or changed by a Director or Retriage Nurse.
              - Example: Patient 1234 is moved to MRI queue at 2:30pm 4/17/2025
                - Logging occurs at the controller and service level with appropriate severity level which is a case by case basis. 
          - Logging will be saved to a text file and output to Director's Event Page
- 3/14
    - Add correct organization, colors and other esthetic changes to webapp
    - Get in contact with Jeff Wald for help and guidance with first attempt at Carroll Design
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

![image](https://github.com/user-attachments/assets/2f966fef-0dfd-47a8-872a-21263b0eb799)
