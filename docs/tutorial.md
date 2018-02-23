---
# You don't need to edit this file, it's empty on purpose.
# Edit theme's home layout instead if you wanna make some changes
# See: https://jekyllrb.com/docs/themes/#overriding-theme-defaults
layout: default
title: Short tutorial of Spring Boot 
---

In Spring Boot there are four main components: Controllers, Services, Repositories and Models. Which have there own resposibilities. Controllers handle the users request and converts it to commands for the model or the view, Services provided Controllers with information which they get from the Repositories, Repositories handle the connection with the "database" and Models contain data about something. Let's explain them a bit futher.

## Controllers

// TODO: add text here

### Controller types

There are two types of controllers: the normal controller (annotation `@Controller`) and the rest controller (annotation `@RestController`). Difference between the two is the that `@RestController` is used to create a REST api and `@Controller` is used in all other cases.

Declaration of the controllers is done via annotation above the Controller class. In the code block below you can see the syntax for both types of Controllers.
```java
@Controller
public class MyController { }

@RestController
public class MyRestController { }
```

### RequestMapping

Lets say you want to create two pages on paths `/events/example/page-1` and on `/events/example/page-2`. To do this you need to create a Controller with the syntax as described above. But now you need to link this Controller to the desired paths. Like the deleration of the Controller this is done via annotation. The annotation for this is `@RequestMapping(value = [path])` where `[path]` is the a String ...... The annotation `@RequestMapping` can be used on an whole class and on a specific method.

```java
@Controller
@RequestMapping(value = "/events/example")
public class MyController { 

    @RequestMapping(value= "/page-1")
    public String handlePageOne() { return "page 1"; }

    @RequestMapping(value= "/page-2")
    public String handlePageTwo() { return "page 2"; }
}
```

Or you could uses a `@PathVariable` which makes a part of the Path variable which you can use in the method.

```java
@Controller
@RequestMapping(value = "/events/example")
public class MyController { 

    @RequestMapping(value= "/{page}")
    public String handleBothPages(@PathVariable String page) { return page; }
}
```

### Model and ModelAttribute

// TODO: add text here

### Return values

// TODO: add text here