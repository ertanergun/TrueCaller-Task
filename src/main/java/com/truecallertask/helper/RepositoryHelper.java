package com.truecallertask.helper;

import com.truecallertask.core.UserView;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;

import java.util.List;

public class RepositoryHelper<E> {

    public RepositoryHelper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private SessionFactory sessionFactory;

    public void saveOrUpdateAll(List<E> list)
    {
        Session session = sessionFactory.openSession();

        try
        {
            ManagedSessionContext.bind(session);
            Transaction transaction = session.beginTransaction();
            try
            {
                for (E userViewToCreate : list) {
                    session.saveOrUpdate(userViewToCreate);
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
