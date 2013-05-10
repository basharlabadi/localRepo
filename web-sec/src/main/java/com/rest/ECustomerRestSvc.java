package com.rest;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.domain.SampleEntity;


@Controller
@RequestMapping("/eCustomer")
public class ECustomerRestSvc {
	
		@RequestMapping(value = "/", method = RequestMethod.GET)
	    public @ResponseBody SampleEntity index() {
	        return new SampleEntity("index");
	    }
		
	    @RequestMapping(value = "/getSomethingbyId/{id}", method = RequestMethod.GET)
	    public @ResponseBody SampleEntity getJsonRestObject(@PathVariable String id) {
	        return new SampleEntity(id);
	    }
	    
	    @RequestMapping(value = "/password", method = RequestMethod.GET)
	    //this should be added to the BL service bean not here, but this is done for demo
	    @PreAuthorize(value = "isAuthenticated() and hasRole('ROLE_ADMIN')")
	    public @ResponseBody String getAdminResource() {
	        return new SampleEntity("admin password").getName();
	    }
}
