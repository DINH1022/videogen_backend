package com.suplerteam.video_creator.security;

import com.suplerteam.video_creator.service.JWTService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component

public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final String AUTH_HEADER="Authorization";

    @Autowired
    private JWTService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;

    private boolean isAuthenticated(){
        return SecurityContextHolder.getContext().getAuthentication()!=null;
    }
    private boolean isHavingToken(HttpServletRequest req){
        final String authHeader=req.getHeader(AUTH_HEADER);
        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            return false;
        }
        return true;
    }
    private String getToken(HttpServletRequest request){
        final String authHeader=request.getHeader(AUTH_HEADER);
        final int START_INDEX_TOKEN=7;
        return authHeader.substring(START_INDEX_TOKEN);
    }
    private void setUserDetailsOfRequestForSecurityContextHolder(HttpServletRequest req,UserDetails userDetails){
        UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void handleAuthenticate(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String jwt=getToken(req);
        if(jwtService.isTokenExpired(jwt)){
            return;
        }
        final String username=jwtService.extractUsername(jwt);
        UserDetails userDetails=userDetailsService.loadUserByUsername(username);
        if(!jwtService.isTokenValid(jwt,userDetails)){
            return;
        }
        setUserDetailsOfRequestForSecurityContextHolder(req,userDetails);
    }
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        try{
            if(isAuthenticated() || !isHavingToken(request)){
                filterChain.doFilter(request,response);
                return;
            }
            handleAuthenticate(request,response,filterChain);
            filterChain.doFilter(request,response);
        }
        catch (ExpiredJwtException ex){
            //log error
            System.out.println(ex.getMessage());
            filterChain.doFilter(request,response);
        }
        catch (Exception ex){
            throw ex;
        }
    }
}
