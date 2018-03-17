# Events [![Build Status](https://travis-ci.org/WISVCH/events.svg?branch=master)](https://travis-ci.org/WISVCH/events) [![Maintainability](https://api.codeclimate.com/v1/badges/f73308e49963d9782643/maintainability)](https://codeclimate.com/github/WISVCH/events/maintainability) [![Test Coverage](https://api.codeclimate.com/v1/badges/f73308e49963d9782643/test_coverage)](https://codeclimate.com/github/WISVCH/events/test_coverage)

Event registration for W.I.S.V. 'Christiaan Huygens'.



Uses [WISVCH/bootstrap-theme](https://github.com/WISVCH/bootstrap-theme) for styling.

## Development installation

1. Install [IntelliJ IDEA Professional](https://www.jetbrains.com/idea/) ([free for students](https://www.jetbrains.com/student/))
2. [Import project from Gradle model](https://www.jetbrains.com/idea/help/importing-project-from-gradle-model.html)
3. [Install the lombok plugin](https://github.com/mplushnikov/lombok-intellij-plugin)
4. Duplicate the file config/application.example.properties and change its name to config/application.properties
5. Add a CH-LDAP-group you are in to the events.admin.groups in the configuration file(config/application.properties)
6. Start the project by right-clicking `EventsApplication` and clicking 'Run'
