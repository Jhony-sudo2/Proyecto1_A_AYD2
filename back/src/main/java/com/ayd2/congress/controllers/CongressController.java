package com.ayd2.congress.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayd2.congress.dtos.Congress.CongressResponse;
import com.ayd2.congress.dtos.Congress.NewCongressRequest;
import com.ayd2.congress.dtos.Congress.UpdateCongress;
import com.ayd2.congress.exceptions.InvalidDateRangeException;
import com.ayd2.congress.exceptions.InvalidPriceException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.services.Congress.CongressService;

@RestController
@RequestMapping("/congresses")
public class CongressController {
    private final CongressService service;
    @Autowired
    public CongressController(CongressService congressService){
        this.service = congressService;
    }

    @PostMapping
    public ResponseEntity<CongressResponse> createCongress(@Validated @RequestBody NewCongressRequest request) throws NotFoundException, InvalidDateRangeException, InvalidPriceException{
        CongressResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CongressResponse> getById(@PathVariable Long id) throws NotFoundException{
        return ResponseEntity.ok(service.getByIdResponse(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CongressResponse> updateCongress(@PathVariable Long id,@Validated @RequestBody UpdateCongress updateCongress) throws NotFoundException{
        CongressResponse response = service.update(updateCongress,id);
        return ResponseEntity.ok(response);
    }

}
