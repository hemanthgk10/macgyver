package io.macgyver.core.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpRequest;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public class MacGyverWebContext {

	ApplicationContext applicationContext;
	ServletContext servletContext;
	HttpServletRequest request;
	HttpServletResponse response;
	static final ThreadLocal<MacGyverWebContext> contextLocal = new ThreadLocal<>();

	public MacGyverWebContext(ApplicationContext context, ServletContext servletContext,
			HttpServletRequest request, HttpServletResponse response) {
		Preconditions.checkNotNull(context);
		Preconditions.checkNotNull(servletContext);
		Preconditions.checkNotNull(request);
		Preconditions.checkNotNull(response);
		
		this.applicationContext = context;
		this.servletContext = servletContext;
		this.request = request;
		this.response = response;
		
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	public ServletContext getServletContext() {
		return servletContext;
	}
	public HttpServletRequest getServletRequest() {
		return request;
	}
	public HttpServletResponse getServletResponse() {
		return response;
	}
	
	public String toString() {
		return MoreObjects.toStringHelper(this).add("request", request).add("response", response).add("applicationContext", applicationContext).toString();
	}
}
