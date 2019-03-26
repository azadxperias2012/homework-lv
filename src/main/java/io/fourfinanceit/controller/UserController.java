package io.fourfinanceit.controller;

import io.fourfinanceit.model.User;
import io.fourfinanceit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.*;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "api/v1/", produces = HAL_JSON_VALUE)
public class UserController {

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "users", method = RequestMethod.POST)
    public HttpEntity<Resource<User>> create(@RequestBody User user) {
        User newUser = userRepository.saveAndFlush(user);
        return new ResponseEntity<>(getUserResource(newUser), HttpStatus.CREATED);
    }

    @RequestMapping(value = "users", method = RequestMethod.GET)
    public HttpEntity<Resources<User>> users() {
        List<User> users = userRepository.findAll();

        Resources<User> usersResource = new Resources<>(users);
        usersResource.add(linkTo(methodOn(this.getClass()).users()).withSelfRel());
        users.forEach(user ->
                usersResource.add(linkTo(methodOn(this.getClass()).getUserById(user.getId()))
                        .withRel(String.valueOf(user.getId()))));

        return new ResponseEntity<>(usersResource, HttpStatus.OK);
    }

    @RequestMapping(value = "users/{id}", method = RequestMethod.GET)
    public HttpEntity<Resource<User>> getUserById(@PathVariable Long id) {
        User user = userRepository.findOne(id);
        return new ResponseEntity<>(getUserResource(user), HttpStatus.OK);
    }

    private Resource<User> getUserResource(User user) {
        Resource<User> userResource = new Resource<>(user);
        userResource.add(linkTo(methodOn(this.getClass()).getUserById(user.getId())).withSelfRel());
        userResource.add(linkTo(methodOn(LoanApplicationController.class).loanList(user.getId()))
                .withRel("loans"));
        return userResource;
    }

}
