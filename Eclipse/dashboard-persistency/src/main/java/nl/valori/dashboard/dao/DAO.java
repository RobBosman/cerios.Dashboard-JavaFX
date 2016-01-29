package nl.valori.dashboard.dao;

public interface DAO {

    public void closeSession();

    public void save(Object object);

    public void beginTransaction();

    public void commitTransaction();

    public void rollbackTransaction();

    public void clearDatabase();
}
