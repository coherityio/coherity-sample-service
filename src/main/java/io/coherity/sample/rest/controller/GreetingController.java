package io.coherity.sample.rest.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/greeting")
public class GreetingController
{
    @GetMapping("/public")
    public String getPublicGreeting()
    {
        log.info("public greeting accessed");
        return "hello public";
    }
    
    @GetMapping("")
    @PreAuthorize("hasAuthority('greeting.protected.read')")
    public String getProtectedGreeting()
    {
        log.info("protected greeting accessed");
        return "hello protected";
    }

}
