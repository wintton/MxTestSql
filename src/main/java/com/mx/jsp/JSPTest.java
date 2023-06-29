package com.mx.jsp;

import org.apache.jasper.runtime.HttpJspBase;
import org.apache.jasper.runtime.JspSourceDependent;
import org.apache.jasper.runtime.JspSourceImports;
import org.apache.tomcat.InstanceManager;

import javax.el.ExpressionFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;

public class JSPTest extends HttpJspBase implements JspSourceDependent, JspSourceImports {


    boolean isRun = true;
    HashMap<Long,Long> timeMap = new HashMap<Long,Long>();

    public void jspInit(){
        System.out.println("jspInit");
        while(isRun){
            timeMap.put(System.currentTimeMillis(),System.currentTimeMillis());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                decimalFormat.format(11);
            }
            System.out.println(System.currentTimeMillis() + "");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
    }

    public void jspDestroy(){
        isRun = false;
        System.out.println("jspDestroy");
    }


    private static final javax.servlet.jsp.JspFactory _jspxFactory =
            javax.servlet.jsp.JspFactory.getDefaultFactory();

    private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

    private static final java.util.Set<java.lang.String> _jspx_imports_packages;

    private static final java.util.Set<java.lang.String> _jspx_imports_classes;

    static {
        _jspx_imports_packages = new java.util.HashSet<>();
        _jspx_imports_packages.add("javax.servlet");
        _jspx_imports_packages.add("java.util");
        _jspx_imports_packages.add("javax.servlet.http");
        _jspx_imports_packages.add("javax.servlet.jsp");
        _jspx_imports_classes = null;
    }

    private volatile javax.el.ExpressionFactory _el_expressionfactory;
    private volatile org.apache.tomcat.InstanceManager _jsp_instancemanager;

    public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
        return _jspx_dependants;
    }

    public java.util.Set<java.lang.String> getPackageImports() {
        return _jspx_imports_packages;
    }

    public java.util.Set<java.lang.String> getClassImports() {
        return _jspx_imports_classes;
    }

    public ExpressionFactory _jsp_getExpressionFactory() {
        if (_el_expressionfactory == null) {
            synchronized (this) {
                if (_el_expressionfactory == null) {
                    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
                }
            }
        }
        return _el_expressionfactory;
    }

    public InstanceManager _jsp_getInstanceManager() {
        if (_jsp_instancemanager == null) {
            synchronized (this) {
                if (_jsp_instancemanager == null) {
                    _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
                }
            }
        }
        return _jsp_instancemanager;
    }

    public void _jspInit() {
    }

    public void _jspDestroy() {
    }

    public void _jspService(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)
            throws java.io.IOException, javax.servlet.ServletException {

        final java.lang.String _jspx_method = request.getMethod();
        if (!"GET".equals(_jspx_method) && !"POST".equals(_jspx_method) && !"HEAD".equals(_jspx_method) && !javax.servlet.DispatcherType.ERROR.equals(request.getDispatcherType())) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "JSP 只允许 GET、POST 或 HEAD。Jasper 还允许 OPTIONS");
            return;
        }

        final javax.servlet.jsp.PageContext pageContext;
        final javax.servlet.ServletContext application;
        final javax.servlet.ServletConfig config;
        javax.servlet.jsp.JspWriter out = null;
        final java.lang.Object page = this;
        javax.servlet.jsp.JspWriter _jspx_out = null;
        javax.servlet.jsp.PageContext _jspx_page_context = null;


        try {
            response.setContentType("text/html; charset=UTF-8");
            pageContext = _jspxFactory.getPageContext(this, request, response,
                    null, false, 8192, true);
            _jspx_page_context = pageContext;
            application = pageContext.getServletContext();
            config = pageContext.getServletConfig();
            out = pageContext.getOut();
            _jspx_out = out;

            out.write('\n');
            out.write('\n');
            out.write('\n');
            out.write('\n');

            ServletContext servletContext = request.getServletContext();
            servletContext.log("hello Wolrd");
            out.println("8082");

            out.write(' ');
        } catch (java.lang.Throwable t) {
            if (!(t instanceof javax.servlet.jsp.SkipPageException)){
                out = _jspx_out;
                if (out != null && out.getBufferSize() != 0)
                    try {
                        if (response.isCommitted()) {
                            out.flush();
                        } else {
                            out.clearBuffer();
                        }
                    } catch (java.io.IOException e) {}
                if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
                else throw new ServletException(t);
            }
        } finally {
            _jspxFactory.releasePageContext(_jspx_page_context);
        }
    }
}