# Briefly summarize the requirements and goals of the app you developed. What user needs was this app designed to address?
------------------------------------------------------------------------------------------------------------------------------------------
This app was built to help users track their daily weight and monitor progress toward a personalized goal. It allows users to log entries 
with a date and weight, view history, edit or delete past entries, and receive a notification when their goal is reached. The app
addresses the need for simple, offline weight tracking without account creation or complex features.

# What screens and features were necessary to support user needs and produce a user-centered UI for the app?
# How did your UI designs keep users in mind? Why were your designs successful?
------------------------------------------------------------------------------------------------------------------------------------------
The app includes input fields for weight and date, a history table showing past entries, edit/delete buttons for each row, a goal field,
and an optional phone number field for SMS alerts. The UI was kept clean and simple, using intuitive controls and labels to ensure ease 
of use. By focusing on clarity and quick interactions, the design supports user needs effectively.

# How did you approach the process of coding your app? What techniques or strategies did you use? How could those techniques or 
# strategies be applied in the future?
------------------------------------------------------------------------------------------------------------------------------------------
Development started with a rough software design document outlining classes, data handling, and UI structure. This upfront planning 
reduced confusion later in development. Code was written incrementally, testing small pieces at a time, which ensured fewer bugs and
easier debugging. This approach is highly transferable to future projects.

# How did you test to ensure your code was functional? Why is this process important, and what did it reveal?
------------------------------------------------------------------------------------------------------------------------------------------
Testing was done manually through emulator and device runs, checking validation rules, CRUD actions, and goal notifications. This process
is vital to catch edge cases and confirm all paths through the app work as expected. It revealed a few logic errors early on, which were 
quickly resolved.

# Consider the full app design and development process from initial planning to finalization. Where did you have to innovate to overcome
# a challenge?
------------------------------------------------------------------------------------------------------------------------------------------
One major challenge was integrating user-defined goals and adding conditional logic to notify the user when the goal was reached. To solve
this, the database was extended with a goal field, and logic was added to check goal conditions dynamically after each entry.

# In what specific component of your mobile app were you particularly successful in demonstrating your knowledge, skills, and experience?
------------------------------------------------------------------------------------------------------------------------------------------
The most successful component was the editable data table with edit/delete capabilities. It demonstrates database integration, dynamic UI
updates, and solid understanding of Android UI controls. The structured layout and responsive interactions reflect effective 
implementation of both design and development skills.
