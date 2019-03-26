package io.fourfinanceit.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1/users/{id}/")
public class LoanApplicationController {

    @RequestMapping(value = "loans", method = RequestMethod.GET)
    public List<String> loanList(@PathVariable Long id) {
        System.out.println("User Id: " + id);
        return Arrays.asList("Car Loan", "Personal Loan");
    }

}
