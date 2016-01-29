package nl.valori.dashboard.dataloader;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;
import nl.valori.dashboard.dao.DAO;
import nl.valori.dashboard.dao.HibernateDAOImpl;

public class DataLoaderTestCase extends TestCase {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private DataLoader dataLoader;
    private DAO dao;

    public DataLoaderTestCase() {
	dataLoader = new DataLoader();
	dao = new HibernateDAOImpl();
    }

    protected void tearDown() throws Exception {
	dao.closeSession();
	super.tearDown();
    }

    protected Date toDate(String dateString) throws ParseException {
	return DATE_FORMAT.parse(dateString);
    }

    protected void clearDatabase() {
	dao.clearDatabase();
	dao.closeSession();
    }

    protected void loadXml(InputStream xmlStream) throws Exception {
	dataLoader.loadXml(xmlStream, "TEST");
	dao.closeSession();
    }
}