package io.fourfinanceit.controller;

import io.fourfinanceit.model.Loan;
import io.fourfinanceit.model.User;
import io.fourfinanceit.repository.LoanRepository;
import io.fourfinanceit.repository.UserRepository;
import io.fourfinanceit.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "api/v1/users/{id}/loans", produces = MediaTypes.HAL_JSON_VALUE)
public class LoanApplicationController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    LoanService loanService;

    @RequestMapping(method = RequestMethod.GET)
    public HttpEntity<Resources<Loan>> loans(@PathVariable Long id) {
        List<Loan> loans = loanService.findLoansByUser(id);
        Resources<Loan> loansResource = new Resources<>(loans);
        loansResource.add(linkTo(methodOn(this.getClass()).loans(id)).withSelfRel());
        loans.forEach(loan -> loansResource.add(linkTo(this.getClass(), id).slash(loan.getId()).withRel(String.valueOf(loan.getId()))));
        return new ResponseEntity<>(loansResource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<Resource<Loan>> applyLoan(@PathVariable Long id,
                          @RequestParam(name = "amount") Double amount,
                          @RequestParam(name = "term") Integer term,
                          HttpServletRequest request) {
        User user = userRepository.findOne(id);
        Loan loan = loanService.applyLoan(user, amount, term, request.getRemoteAddr());

        final URI uri = MvcUriComponentsBuilder.fromController(this.getClass())
                .path("/{loanId}")
                .buildAndExpand(id, loan.getId())
                .toUri();
        Resource<Loan> loanResource = getLoanResource(loan);
        return ResponseEntity.created(uri).body(loanResource);
    }

    @RequestMapping(value = "/{loanId}", method = RequestMethod.GET)
    public HttpEntity<Resource<Loan>> getLoanById(@PathVariable(value = "loanId") Long loanId) {
        Loan loan = loanService.findLoanById(loanId);
        Resource<Loan> loanResource = getLoanResource(loan);
        return new ResponseEntity<>(loanResource, HttpStatus.OK);
    }

    @RequestMapping(value = "/{loanId}/extend", method = RequestMethod.POST)
    public HttpEntity<Resource<Loan>> extendLoan(@PathVariable(value = "loanId") Long loanId,
                           @RequestParam(name = "term") Integer term) {
        Loan loan = loanService.extendLoan(loanId, term);
        return new ResponseEntity<>(getLoanResource(loan), HttpStatus.ACCEPTED);
    }

    private Resource<Loan> getLoanResource(Loan loan) {
        Resource<Loan> loanResource = new Resource<>(loan);
        loanResource.add(linkTo(this.getClass(), loan.getUser().getId()).slash(loan.getId()).withSelfRel());
        return loanResource;
    }

}
