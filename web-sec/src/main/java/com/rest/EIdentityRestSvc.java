package com.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.domain.SampleEntity;

@Controller
@RequestMapping("/eIdentity")
public class EIdentityRestSvc {
	@RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody SampleEntity index() {
        return new SampleEntity("index");
    }
}
