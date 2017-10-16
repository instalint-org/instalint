package io.instalint.daemon;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.sonar.api.web.ServletFilter;

public class ExceptionFilter extends ServletFilter {

  public void init(FilterConfig config) throws ServletException {
    // no action required
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    try {
      chain.doFilter(request, response);
    } catch (Exception e) {
      Logger.getLogger(ExceptionFilter.class.getName()).log(Level.WARNING, "Servlet threw exception", e);
      if (!response.isCommitted() && response instanceof HttpServletResponse) {
        ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("{\"exception\": true}");
      }
    }
  }

  @Override
  public void destroy() {
    // no action required
  }
}
