package com.retriage.retriage.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FallbackController {

    // DO NOT include "/"
    @RequestMapping(value = { "/{x:[\\w\\-]+}", "/**/{x:[\\w\\-]+}" })
    public String redirectToIndex() {
        return "forward:/index.html";
    }
}

