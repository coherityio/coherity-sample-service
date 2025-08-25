package io.coherity.sample.rest.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestLoggingFilter implements Filter
{
    @Autowired
    private LoggingSystem loggingSystem;
    
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		//this.setSpringLoggingSystem(req);
		
		// Wrap response to capture output
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(req);
		ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(res);
		
		// Log request details
		log.debug("REST Request URI: " + req.getMethod() + " " + req.getRequestURI());
        String requestBody = new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8);
        log.debug("REST Request Body: " + requestBody);

		chain.doFilter(wrappedRequest, wrappedResponse);

		// Log response details
		String responseBody = new String(wrappedResponse.getContentAsByteArray(), response.getCharacterEncoding());
		log.debug("REST Response: " + responseBody);

		wrappedResponse.copyBodyToResponse(); // Important to send response back to client
	}
	
	private void setSpringLoggingSystem(HttpServletRequest request)
	{
		if(request != null)
		{
			String debugHeader = request.getHeader("debug");
			if ("true".equalsIgnoreCase(debugHeader))
			{
				loggingSystem.setLogLevel("org.hibernate.SQL", LogLevel.DEBUG);
				loggingSystem.setLogLevel("org.hibernate.type.descriptor.sql.BasicBinder", LogLevel.TRACE);
			}
			else
			{
				loggingSystem.setLogLevel("org.hibernate.SQL", LogLevel.OFF);
				loggingSystem.setLogLevel("org.hibernate.type.descriptor.sql.BasicBinder", LogLevel.OFF);
			}			
		}
	}
}