package io.macgyver.core.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class MacGyverContextFilter implements Filter {

	@Autowired
	ApplicationContext applicationContext;


	FilterConfig filterConfig;

	Logger logger = LoggerFactory.getLogger(MacGyverContextFilter.class);
	public MacGyverContextFilter() {

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		this.filterConfig = filterConfig;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {
			MacGyverWebContext x = new MacGyverWebContext(applicationContext, filterConfig.getServletContext(),
					(HttpServletRequest) request, (HttpServletResponse) response);

			MacGyverWebContext.contextLocal.set(x);
			chain.doFilter(request, response);
		} finally {
			MacGyverWebContext.contextLocal.set(null);
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
