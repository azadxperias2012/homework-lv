package io.fourfinanceit.controller;

import io.fourfinanceit.model.Loan;
import io.fourfinanceit.model.User;
import io.fourfinanceit.repository.LoanRepository;
import io.fourfinanceit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1/users/{id}/", produces = MediaTypes.HAL_JSON_VALUE)
public class LoanApplicationController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    LoanRepository loanRepository;

    @RequestMapping(value = "loans", method = RequestMethod.GET)
    public List<String> loanList(@PathVariable Long id) {
        return Arrays.asList("Car Loan", "Personal Loan");
    }

    @RequestMapping(value = "loans", method = RequestMethod.POST)
    public Loan applyLoan(@PathVariable Long id,
                          @RequestParam(name = "amount") Double amount,
                          @RequestParam(name = "term") Integer term) {

        User user = userRepository.findOne(id);
        Loan loan = new Loan(amount, term, user);

        return loanRepository.save(loan);
    }


}
