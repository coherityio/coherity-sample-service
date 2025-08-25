package io.coherity.sample.rest.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {
    "io.coherity.sample.rest.controller",
    "io.coherity.sample.rest.config" })
public class Application
{
    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
        log.info("started coherity-sample-service");
    }
}
