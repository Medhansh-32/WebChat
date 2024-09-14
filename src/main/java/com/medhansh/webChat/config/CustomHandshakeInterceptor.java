package com.medhansh.webChat.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@Component
public class CustomHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(CustomHandshakeInterceptor.class);
//
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//
//        if (request instanceof ServletServerHttpRequest) {
//
//
//            log.info(request.getHeaders().toString());
//
//            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
//            log.info(servletRequest.getHeader(""));
//            Cookie cookie[]= servletRequest.getCookies();
//            for(Cookie c:cookie){
//                if(c.getName().equals("userId")){
//                    attributes.put("userId", c.getValue());
//                    log.info(c.getName(),c.getValue());
//                }
//
//            }
//            HttpSession httpSession = servletRequest.getSession(false); // Retrieve existing session, don't create a new one
//            log.info((String) httpSession.getAttribute("userId"));
//
//            if (httpSession != null) {
//                attributes.put("httpSession", httpSession);
//                log.info("HttpSession attached to WebSocket session: " + httpSession.getId());
//            } else {
//                log.error("HttpSession is null during WebSocket handshake");
//            }
//        }
//        return super.beforeHandshake(request, response, wsHandler, attributes);
//    }
//@Override
//public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                               WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//
//    if (request instanceof ServletServerHttpRequest) {
//        // Cast the ServerHttpRequest to ServletServerHttpRequest to access HTTP-specific features
//        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
//
//        // Retrieve cookies from the request
//        Cookie[] cookies = servletRequest.getCookies();
//        if (cookies != null) {
//            for (Cookie c : cookies) {
//                if (c.getName().equals("userId")) {
//                    // Store the userId in WebSocket attributes
//                    attributes.put("userId", c.getValue());
//                    log.info("Cookie userId: " + c.getValue());
//                }
//            }
//        }
//
//        // Retrieve the existing HttpSession (don't create a new one)
//        HttpSession httpSession = servletRequest.getSession(false);  // false means don't create a new session
//        if (httpSession != null) {
//            // Store the HttpSession in WebSocket attributes
//            attributes.put("httpSession", httpSession);
//            log.info("HttpSession attached to WebSocket session: " + httpSession.getId());
//        } else {
//            log.error("HttpSession is null during WebSocket handshake");
//        }
//    }
//
//    return super.beforeHandshake(request, response, wsHandler, attributes);
//}
@Override
public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    if (request instanceof ServletServerHttpRequest) {
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

        // Get session ID from the query parameter
        String sessionId = servletRequest.getParameter("sessionId");
        if (sessionId != null) {
            HttpSession httpSession = servletRequest.getSession(false);
    //        if (httpSession != null && sessionId.equals(httpSession.getId())) {
                attributes.put("sessionId", sessionId);
       //     }
        }
    }
    return true;
}

}
