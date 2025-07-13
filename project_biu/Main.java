package project_biu;

import project_biu.server.HTTPServer;
import project_biu.server.MyHTTPServer;
import project_biu.servlets.ConfLoader;
import project_biu.servlets.TopicDisplayer;
import project_biu.servlets.HtmlLoader;

public class Main {
    public static void main(String[] args) throws Exception {
        HTTPServer server = new MyHTTPServer(8080, 6);

        server.addServlet("GET", "/publish", new TopicDisplayer());
        server.addServlet("POST", "/upload", new ConfLoader());
        server.addServlet("GET", "/app/", new HtmlLoader("html_files"));  // ✅ Supports index.html etc.

        server.start();
        System.in.read();
        server.close();
        System.out.println("done");
    }
}
