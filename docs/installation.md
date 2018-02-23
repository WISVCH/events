---
# You don't need to edit this file, it's empty on purpose.
# Edit theme's home layout instead if you wanna make some changes
# See: https://jekyllrb.com/docs/themes/#overriding-theme-defaults
layout: default
title: Installation
---

### Tools

In order to run this project you need to have the following tool installed:

- [PostgresQL](https://www.postgresql.org/)
- [mailcatcher](https://mailcatcher.me/), mailcatcher creates a mailserver locally on your pc. All mail sent from the API is cought here, you end 
up with a mailbox with every outgoing mailaddress. Unix-like systems: gem install mailcatcher. Windows users can try
mailcatcher as well, but [Papercut](https://github.com/changemakerstudios/papercut) has an easier installation.
- [lombok](https://github.com/mplushnikov/lombok-intellij-plugin), lombok allowes users to uses annotations to create getters, setters, etc. All the things needed in a data class.
There is a lombok IntelliJ plugin which you should install.

### Clone and setup

1. Clone this project into a folder you link via `git clone git@github.com:WISVCH/events.git`.
2. Import the project using IntelliJ IDEA, we recommend using [IntelliJ IDEA Ultimate](https://www.jetbrains.com/idea/download/) ([free for students](https://www.jetbrains.com/student/)) because it includes support for Spring. ![Gradle import](/images/import_steps.png)
3. Enable annotation processing `Preferences > Build, Execution, Depolyment > Compiler > Annotiation Processing`.
4. Create an PostgreSQL database ([Tutorial PostgreSQL database](https://www.postgresqltutorial.com/postgresql-create-database/)). 
5. Duplicate the file `config/application.yml.example` and change its name to `config/application.yml` and change:
    1. Replace `<MyDb>` with the name of the database you just created.
    2. Replace `<MyDbUser>` with the database user.
    3. Replace `<MyDbPassword>` with the password of the database user.
    4. Replace `<MyLDAPgroup>` with a committee you are in (e.g. akcie, choco)
        - **Note:** if you are not in a committee you can use `users` as LDAPgroup
6. Start the project by right-clicking `EventsApplication` and clicking 'Run'


### Running test

The test of the project can be run via the command

```
./gradlew test
```

Or by right-clicking the test folder and selecting `Run 'All the test'`