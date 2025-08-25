package io.coherity.sample.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/greeting")
public class GreetingController
{
    @GetMapping("")
    public String getGreeting()
    {
        log.info("Root endpoint accessed");
        return "hello";
    }
}
