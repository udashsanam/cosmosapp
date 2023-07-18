//package com.cosmos.common.security;
//
//import java.io.IOException;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.cosmos.common.exception.CustomException;
//import com.cosmos.common.model.ApiError;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//
//// We should use OncePerRequestFilter since we are doing a database call,
//// there is no point in doing this more than once
//public class JwtTokenFilter extends OncePerRequestFilter {
//
//	private JwtTokenProvider jwtTokenProvider;
//
//	public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
//		this.jwtTokenProvider = jwtTokenProvider;
//	}
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
//			FilterChain filterChain) throws ServletException, IOException {
//		String token = jwtTokenProvider.resolveToken(httpServletRequest);
//		try {
//			if (token != null && jwtTokenProvider.validateToken(token)) {
//				Authentication auth = jwtTokenProvider.getAuthentication(token);
//				SecurityContextHolder.getContext().setAuthentication(auth);
//			}
//		} catch (CustomException ex) {
//			// this is very important, since it guarantees the user is not authenticated at
//			// all
//			SecurityContextHolder.clearContext();
//
//			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//			ApiError error = new ApiError(ex.getHttpStatus(), ex.getMessage());
//			String json = ow.writeValueAsString(error);
//
//			httpServletResponse.setContentType("application/json");
//			httpServletResponse.getOutputStream().println(json);
//			return;
//		}
//
//		filterChain.doFilter(httpServletRequest, httpServletResponse);
//	}
//
//}
