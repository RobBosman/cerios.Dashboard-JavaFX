package nl.valori.dashboard.dao;

import java.util.List;

import nl.valori.dashboard.model.BootstrapQueries;
import nl.valori.dashboard.model.KpiHolder;
import nl.valori.dashboard.model.KpiVariable;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class DashboardDAO extends HibernateDAOImpl implements BootstrapQueries {

    @SuppressWarnings("unchecked")
    public List<KpiHolder> getRootKpiHolders() {
	Criteria criteria = getSession().createCriteria(KpiHolder.class).add(Restrictions.isNull("parent"));
	return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<KpiVariable> getKpiVariables() {
	Criteria criteria = getSession().createCriteria(KpiVariable.class);
	return criteria.list();
    }
}
