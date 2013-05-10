// https://github.com/karasatishkumar/webservice-JAX-WS-handler
// for client impl : https://github.com/karasatishkumar/webservice-client-handler
// http://www.mkyong.com/webservices/jax-ws/jax-ws-soap-handler-in-server-side/
package com.security;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

public class SOAPHandler implements javax.xml.ws.handler.soap.SOAPHandler<SOAPMessageContext> {

	private AuthenticationManager authenticationManager;
	public boolean handleMessage(SOAPMessageContext context) {

		System.out.println("handleMessage() called.");

		Boolean isRequest = (Boolean) context
				.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		// for request message only, true for outbound messages, false for
		// inbound
		if (!isRequest) {

			try {
				SOAPMessage soapMsg = context.getMessage();
				SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
				SOAPHeader soapHeader = soapEnv.getHeader();

				// if no header, add one
				if (soapHeader == null) {
					soapHeader = soapEnv.addHeader();
					// No header found, throw exception
					embedSOAPException(soapMsg, "Missing Header", "ERR:001");
				}

				// Get user Id and password from SOAP header
				Iterator<Node> it = soapHeader.extractHeaderElements(SOAPConstants.URI_SOAP_ACTOR_NEXT);

				// if no header block for next actor found? throw exception
				if (it == null || !it.hasNext()) {
					embedSOAPException(soapMsg, "No Header Data", "ERR:002");
				}

				String authtoken = null;
				
				while (it.hasNext()) {
					Node node = it.next();
					if (node != null && node.getNodeName().equals("x-tcss-auth"))
						authtoken = node.getValue();
				}

				if (authtoken == null) {
					embedSOAPException(soapMsg, "No userName or password.",
							"ERR:003");
				}
				
				String[] credentials = decodeHeader(authtoken);
		        assert credentials.length == 2;
		        Authentication authentication = new AuthToken(credentials[0], credentials[1]);
		        try {
		            //Request the authentication manager to authenticate the token
		            Authentication successfulAuthentication = authenticationManager.authenticate(authentication);
		            //Pass the successful token to the SecurityHolder where it can be retrieved by this thread at any stage.
		            SecurityContextHolder.getContext().setAuthentication(successfulAuthentication);
		            //Continue with the handlers chain
		            return true;
		        } catch (AuthenticationException authenticationException) {
		            //If it fails clear this threads context and kick off the authentication entry point process.
		            SecurityContextHolder.clearContext();
		            return false;
		        }
			} catch (SOAPException e) {
				System.err.println(e);
				return false;
			} catch (IOException e) {
				System.err.println(e);
				return false;
			}

		}

		// continue other handler chain
		return true;
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public boolean handleFault(SOAPMessageContext context) {

		System.out.println("handleFault() called.");

		return true;
	}

	public void close(MessageContext context) {
		System.out.println("close() called");
	}

	public Set<QName> getHeaders() {
		System.out.println("getHeaders() called");
		return null;
	}
	
	public String[] decodeHeader(String authorization) {
		String credentials = new String(authorization);
		return credentials.split(":");
    }
	private void embedSOAPException(SOAPMessage msg, String reason,
			String faultCode) throws SOAPException, IOException {
		SOAPBody soapBody = msg.getSOAPPart().getEnvelope().getBody();
		SOAPFault soapFault = soapBody.getFault();
		if (soapFault == null)
			soapFault = soapBody.addFault();
		soapFault.setFaultString(reason);
		soapFault.setFaultCode(faultCode);
		msg.writeTo(System.out);
	}

}
