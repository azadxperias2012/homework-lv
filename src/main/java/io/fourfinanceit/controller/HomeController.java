package io.fourfinanceit.controller;

import io.fourfinanceit.model.Home;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
    public HttpEntity<Home> home() {

        Link selfRelLink = linkTo(methodOn(this.getClass()).home()).withSelfRel();
        Link usersLink = linkTo(methodOn(UserController.class).users()).withRel("all-users");

        Home home = new Home();
        home.add(usersLink);
        home.add(selfRelLink);

        return new ResponseEntity<>(home, HttpStatus.OK);

    }

}
