package com.proyectofinal.clave_compas.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("")
public class Hello {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!!!";
    }
}
