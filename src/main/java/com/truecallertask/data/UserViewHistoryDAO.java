package com.truecallertask.data;

import com.google.common.base.Optional;
import com.truecallertask.core.UserViewHistory;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;

public class UserViewHistoryDAO extends AbstractDAO<UserViewHistory> {
    public UserViewHistoryDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<UserViewHistory> findById(Long id) {
        return Optional.fromNullable(get(id));
    }
    public UserViewHistory saveOrUpdate(UserViewHistory userView) {
        return persist(userView);
    }
    public List<UserViewHistory> findAll() {
        return list(namedQuery("com.truecallertask.core.UserViewHistory.findAll"));
    }
}
