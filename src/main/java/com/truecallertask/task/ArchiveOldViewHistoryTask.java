package com.truecallertask.task;

import com.google.common.collect.ImmutableMultimap;
import com.truecallertask.data.UserViewDAO;
import com.truecallertask.data.UserViewHistoryDAO;
import com.truecallertask.helper.ArchiveOldViewHistory;
import io.dropwizard.servlets.tasks.Task;
import org.hibernate.SessionFactory;
import java.io.PrintWriter;

public class ArchiveOldViewHistoryTask extends Task {

    private Runnable archiveOldUserViewsSchedule;

    public ArchiveOldViewHistoryTask(UserViewDAO userViewDAO, UserViewHistoryDAO userViewHistoryDAO, SessionFactory sessionFactory) {
        super("Archive-Old-ViewHistory");
        archiveOldUserViewsSchedule = new ArchiveOldViewHistory(userViewDAO, userViewHistoryDAO, sessionFactory);
    }

    @Override
    public void execute(ImmutableMultimap<String, String> immutableMultimap, PrintWriter printWriter) throws Exception {

        /***
         * Removes the old userviews from datebase and
         * re-writes them into the History table
         */
        archiveOldUserViewsSchedule.run();
    }
}
