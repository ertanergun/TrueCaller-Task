package com.truecallertask.data;

import com.truecallertask.core.UserViewHistory;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;

public class UserViewHistoryDAO extends AbstractDAO<UserViewHistory> {
    public UserViewHistoryDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /***
     * Saves a new userviewhistory or updates existing one
     * @param userView
     * @return
     */
    public UserViewHistory saveOrUpdate(UserViewHistory userView) {
        return persist(userView);
    }

    /***
     * Returns all UserViews from history
     * @return
     */
    public List<UserViewHistory> findAll() {
        return list(namedQuery("com.truecallertask.core.UserViewHistory.findAll"));
    }
}
