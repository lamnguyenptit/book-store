//package com.example.login.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.web.session.HttpSessionEventPublisher;
//import org.springframework.web.filter.CharacterEncodingFilter;
//import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
//
//import javax.servlet.FilterRegistration;
//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//
//@Configuration
//public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
//
//    @Override
//    protected Class<?>[] getRootConfigClasses() {
//        return null;
//    }
//
//    @Override
//    protected Class<?>[] getServletConfigClasses() {
//        return null;
//    }
//
//    @Override
//    protected String[] getServletMappings() {
//        return null;
//    }
//
//    @Override
//    public void onStartup(ServletContext servletContext) throws ServletException {
//        super.onStartup(servletContext);
//        FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("encoding-filter", CharacterEncodingFilter.class);
//        encodingFilter.setInitParameter("encoding", "UTF-8");
//        encodingFilter.setInitParameter("forceEncoding", "true");
//        encodingFilter.setAsyncSupported(true);
//        encodingFilter.addMappingForUrlPatterns(null, false, "/*");
//        servletContext.addListener(new HttpSessionEventPublisher());
//    }
//}
