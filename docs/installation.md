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

### Installation

1. Clone this project into a folder you link via `git clone git@github.com:WISVCH/events.git`
2. Import the project using IntelliJ IDEA, we recommend using [IntelliJ IDEA Ultimate](https://www.jetbrains.com/idea/download/) ([free for students](https://www.jetbrains.com/student/)) because it includes support for Spring.

![Gradle import](/images/import_project_gradle.png)
![Gradle import](/images/enable_auto_import.png)

2. [Import project from Gradle model](https://www.jetbrains.com/idea/help/importing-project-from-gradle-model.html)
3. [Install the lombok plugin](https://github.com/mplushnikov/lombok-intellij-plugin)
4. Duplicate the file `config/application.yml.example` and change its name to `config/application.yml`
5. Add a CH-LDAP-group you are in to the events.admin.groups in the configuration file(config/application.properties)
6. Start the project by right-clicking `EventsApplication` and clicking 'Run'