package com.truecallertask.task;

import com.google.common.collect.ImmutableMultimap;
import com.truecallertask.core.UserView;
import com.truecallertask.core.UserViewHistory;
import com.truecallertask.data.UserViewDAO;
import com.truecallertask.data.UserViewHistoryDAO;
import io.dropwizard.servlets.tasks.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;
import org.joda.time.DateTime;
import java.io.PrintWriter;

public class ArchiveOldViewHistoryTask extends Task {

    private UserViewDAO userViewDAO;
    private UserViewHistoryDAO userViewHistoryDAO;
    private SessionFactory sessionFactory;

    public ArchiveOldViewHistoryTask(UserViewDAO userViewDAO, UserViewHistoryDAO userViewHistoryDAO, SessionFactory sessionFactory) {
        super("Archive-Old-ViewHistory");
        this.userViewDAO = userViewDAO;
        this.userViewHistoryDAO = userViewHistoryDAO;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void execute(ImmutableMultimap<String, String> immutableMultimap, PrintWriter printWriter) throws Exception {

        Session session = sessionFactory.openSession();
        try {
            ManagedSessionContext.bind(session);
            Transaction transaction = session.beginTransaction();
            try {
                    for (UserView userViewToArchive : userViewDAO.getUserViewsOlderThanDate(DateTime.now().minusDays(10))) {
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
