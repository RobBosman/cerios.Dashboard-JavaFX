package nl.valori.dashboard.server;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.valori.dashboard.dao.DashboardDAO;
import nl.valori.dashboard.model.BootstrapQueries;
import nl.valori.space.Space;
import nl.valori.space.SpaceId;
import nl.valori.space.serialize.marshall.ChainableMarshallWriter;
import nl.valori.space.serialize.marshall.Marshaller;
import nl.valori.space.serialize.marshall.writers.DefaultWriter;
import nl.valori.space.serialize.marshall.writers.IndentingWriter;

public class Servlet extends HttpServlet {

    private static final long serialVersionUID = -8772581880920631829L;
    private static final Charset CHARSET = Charset.forName("UTF8");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	response.setHeader("pragma", "no-cache");
	response.setHeader("cache-control", "no-cache");
	response.setCharacterEncoding(CHARSET.name());
	response.setContentType("text/plain");

	BootstrapQueries service = new DashboardDAO();
	Object responseObject = null;
	Space space = Space.getInstance();
	String ids[] = request.getParameterValues("id");
	if (ids != null) {
	    List<Object> spaceObjects = new ArrayList<Object>();
	    for (String id : ids) {
		spaceObjects.add(space.read(new SpaceId(id, false)));
	    }
	    responseObject = spaceObjects;
	} else if (request.getParameter("listSpace") != null) {
	    response.getWriter().println("Space content:");
	    response.getWriter().println(space.toString());
	} else if (request.getParameter("getRootKpiHolders") != null) {
	    responseObject = service.getRootKpiHolders();
	} else if (request.getParameter("collectGarbage") != null) {
	    System.gc();
	    response.getWriter().print("Collected garbage");
	} else {
	    response.getWriter().println("Supported services:");
	    response.getWriter().println("\tlistSpace");
	    response.getWriter().println("\tgetRootKpiHolders");
	    response.getWriter().println("\tcollectGarbage");
	}

	if (responseObject != null) {
	    // Serialize
	    ChainableMarshallWriter chainableWriter = new DefaultWriter(response.getOutputStream(), CHARSET);
	    chainableWriter = new IndentingWriter(chainableWriter);
	    Marshaller marshaller = new Marshaller();
	    if (responseObject instanceof Collection<?>) {
		marshaller.marshall(chainableWriter, ((Collection<?>) responseObject).toArray());
	    } else {
		marshaller.marshall(chainableWriter, responseObject);
	    }
	}
    }
}
