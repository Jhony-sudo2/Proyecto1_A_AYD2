package com.ayd2.congress.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayd2.congress.dtos.SysConfig.SysConfigResponse;
import com.ayd2.congress.dtos.SysConfig.SysConfigUpdate;
import com.ayd2.congress.exceptions.InvalidPriceException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.services.systemconfig.SystemConfigService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final SystemConfigService service;
    @Autowired
    public AdminController(SystemConfigService service){
        this.service = service;
    }

    @GetMapping("/config")
    public ResponseEntity<SysConfigResponse> getConfig() throws NotFoundException{
        SysConfigResponse response = service.getConfigResponse();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/config")
    public ResponseEntity<SysConfigResponse> updateSysConfig(@Valid @RequestBody SysConfigUpdate request) throws NotFoundException, InvalidPriceException{
        SysConfigResponse response = service.update(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    

}
