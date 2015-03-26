package com.truecallertask.helper;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;


public class RemoveDublicatedRecords implements Runnable {

    private SessionFactory sessionFactory;

    public RemoveDublicatedRecords(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    /***
     * Removes dublicated records from database
     */
    @Override
    public void run() {
        Session session = sessionFactory.openSession();
        try {
            ManagedSessionContext.bind(session);
            Transaction transaction = session.beginTransaction();
            try {
                String query = "DELETE FROM USERVIEWS WHERE ID NOT IN (SELECT MIN(ID) FROM USERVIEWS GROUP BY VIEWERID,VIEWEDID,VIEWDATE)";
                session.createSQLQuery(query).executeUpdate();
                transaction.commit();
            }
            catch (Exception e) {
                transaction.rollback();
                throw new RuntimeException(e);
            }
        } finally {
            session.close();
            ManagedSessionContext.unbind(sessionFactory);
        }
    }
}
