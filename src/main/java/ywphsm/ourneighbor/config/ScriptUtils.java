package ywphsm.ourneighbor.config;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/*
    컨트롤러에서 alert를 쓰기위해 만든 정적 클래서
 */
public class ScriptUtils {

    public static void init(HttpServletResponse response) {
        response.setContentType("text/html; charset=euc-kr");
        response.setCharacterEncoding("euc-kr");
    }

    /*
        기본 alert
     */
    public static void alert(HttpServletResponse response, String alertText) throws IOException {
        init(response);
        PrintWriter out = response.getWriter();
        out.println("<script>alert('" + alertText + "');</script> ");
        out.flush();
    }

    /*
        페이지 지정해서 보내면서 alert
     */
    public static void alertAndMovePage(HttpServletResponse response, String alertText, String nextPage)
            throws IOException {
        init(response);
        PrintWriter out = response.getWriter();
        out.println("<script>alert('" + alertText + "'); location.href='" + nextPage + "';</script> ");
        out.flush();
    }

    /*
        이전페이지로 보내면서 alert
     */
    public static void alertAndBackPage(HttpServletResponse response, String alertText) throws IOException {
        init(response);
        PrintWriter out = response.getWriter();
        out.println("<script>alert('" + alertText + "'); history.go(-1);</script>");
        out.flush();
    }
}
