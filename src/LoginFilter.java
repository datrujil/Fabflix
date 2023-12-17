//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// * Servlet Filter implementation class LoginFilter
// */
//@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
//public class LoginFilter implements Filter {
//    private final ArrayList<String> allowedURIs = new ArrayList<>();
//
//    /**
//     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
//     */
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//
//        System.out.println("LoginFilter: " + httpRequest.getRequestURI());
//
//        // Check if this URL is allowed to access without logging in
//        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
//            // Keep default action: pass along the filter chain
//            chain.doFilter(request, response);
//            return;
//        }
//
////         Redirect to login page if the "user" attribute doesn't exist in session
////        if (httpRequest.getSession().getAttribute("user") == null && httpRequest.getSession().getAttribute("employee") == null) {
////            System.out.println(httpRequest.getSession().getAttribute("employee"));
////            httpResponse.sendRedirect("login.html");
////
////        } else if (httpRequest.getSession().getAttribute("user") == null || httpRequest.getSession().getAttribute("employee") == null)  {
////            chain.doFilter(request, response);
////
//        /*
//        // remove this since jmeter can't do filter
//        if (httpRequest.getSession().getAttribute("user") != null || httpRequest.getSession().getAttribute("employee") != null) {
//            System.out.println(httpRequest.getSession().getAttribute("employee"));
//            chain.doFilter(request, response);
//        } else {
//            // User is not authenticated, redirect to login.html or employee-login.html
//            if (httpRequest.getRequestURI().endsWith("_dashboard")) {
//                httpResponse.sendRedirect("employee-login.html");
//            } else {
//                httpResponse.sendRedirect("login.html");
//            }
//        }
//        */
//        chain.doFilter(request, response);
//    }
//
//    private boolean isUrlAllowedWithoutLogin(String requestURI) {
//        /*
//         Setup your own rules here to allow accessing some resources without logging in
//         Always allow your own login related requests(html, js, servlet, etc..)
//         You might also want to allow some CSS files, etc..
//         */
//        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
//    }
//
//    public void init(FilterConfig fConfig) {
//        allowedURIs.add("login.html");
//        allowedURIs.add("login.js");
//        allowedURIs.add("api/login");
//        // for employee
//        allowedURIs.add("employee-login.html");
//        allowedURIs.add("employee-login.js");
////        allowedURIs.add("_dashboard");
//        allowedURIs.add("api/employee-login");
//
//
//    }
//
//    public void destroy() {
//        // ignored.
//    }
//
//}