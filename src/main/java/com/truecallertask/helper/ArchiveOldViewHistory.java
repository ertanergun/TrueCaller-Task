package com.truecallertask.helper;

import com.truecallertask.core.UserView;
import com.truecallertask.core.UserViewHistory;
import com.truecallertask.data.UserViewDAO;
import com.truecallertask.data.UserViewHistoryDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;
import org.joda.time.DateTime;


/***
 * Removes the old userviews from datebase and
 * re-writes them into the History table
 */
public class ArchiveOldViewHistory implements Runnable {

    private UserViewDAO userViewDAO;
    private UserViewHistoryDAO userViewHistoryDAO;
    private SessionFactory sessionFactory;

    public ArchiveOldViewHistory(UserViewDAO userViewDAO, UserViewHistoryDAO userViewHistoryDAO, SessionFactory sessionFactory) {

        this.userViewDAO = userViewDAO;
        this.userViewHistoryDAO = userViewHistoryDAO;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void run() {

        Session session = sessionFactory.openSession();
        try {
            ManagedSessionContext.bind(session);
            Transaction transaction = session.beginTransaction();
            try {
                for (UserView userViewToArchive : userViewDAO.getUserViewsOlderThanDate(DateTime.now())) {
                    userViewHistoryDAO.saveOrUpdate(new UserViewHistory(userViewToArchive.getViewerId(),userViewToArchive.getViewedId(),userViewToArchive.getViewDate()));
                    session.delete(userViewToArchive);
                }
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
