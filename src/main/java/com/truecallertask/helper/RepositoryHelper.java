package com.truecallertask.helper;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;

import java.util.List;

public class RepositoryHelper<E> {

    private SessionFactory sessionFactory;

    public RepositoryHelper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    /***
     * Inserts a list of <E> type object into the
     * database with the same session
     * @param list
     */
    public void saveOrUpdateAll(List<E> list)
    {
        Session session = sessionFactory.openSession();

        try
        {
            ManagedSessionContext.bind(session);
            Transaction transaction = session.beginTransaction();
            try
            {
                for (E entity : list) {
                    session.saveOrUpdate(entity);
                }
                transaction.commit();
            }
            catch (Exception e) {
                transaction.rollback();
                throw new RuntimeException(e);
            }
        }
        finally {
            session.close();
            ManagedSessionContext.unbind(sessionFactory);
        }
    }
}
