package com.ws;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebService;

import com.domain.SampleEntity;

@WebService
//didnt work in the spring web app.only from main.
@HandlerChain(file = "handlers.xml")
public class ECustomerWS {
	@WebMethod(operationName="getSampleEntity")
	public SampleEntity getHelloWorld() {
		return new SampleEntity("bashar");
	}
}
