package nl.valori.dashboard.dao;

import org.hibernate.Session;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class HibernateDAOImpl implements DAO {

    private HibernateHelper hibernateHelper;

    public HibernateDAOImpl() {
	this.hibernateHelper = HibernateHelper.getInstance();
    }

    protected Session getSession() {
	return hibernateHelper.getSession();
    }

    public void closeSession() {
	hibernateHelper.closeSession();
    }

    public void save(Object object) {
	getSession().save(object);
    }

    public void beginTransaction() {
	getSession().beginTransaction();
    }

    public void commitTransaction() {
	getSession().getTransaction().commit();
    }

    public void rollbackTransaction() {
	getSession().getTransaction().rollback();
    }

    public void clearDatabase() {
	SchemaExport schemaExport = new SchemaExport(hibernateHelper.getConfiguration());
	// Remove all content (tables, etc) from the database schema.
	schemaExport.drop(false, true);
	// Re-create the database schema. This removes any existing data.
	schemaExport.create(false, true);
    }
}
