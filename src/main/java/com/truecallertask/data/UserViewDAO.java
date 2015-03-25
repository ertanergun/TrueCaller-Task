package com.truecallertask.data;

import com.google.common.base.Optional;
import com.truecallertask.core.UserView;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import java.util.List;

public class UserViewDAO extends AbstractDAO<UserView> {

    public UserViewDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<UserView> findById(Long id) {
        return Optional.fromNullable(get(id));
    }
    public UserView saveOrUpdate(UserView userView) {
        return persist(userView);
    }
    public List<UserView> findAll() {
        return list(namedQuery("com.truecallertask.core.UserView.findAll"));
    }
    public List<UserView> getViewList(Long viewId, int remainingRecordSize){
        DateTime today = DateTime.now();

        return list(
                namedQuery("com.truecallertask.core.UserView.getViewList")
                        .setParameter("dateLimit", new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 0, 0, 0, 0).minusDays(10))
                        .setParameter("viewedId", viewId)
                        .setMaxResults(remainingRecordSize));
    }

    public UserView checkUserView(Long viewerId, Long viewedId, DateTime dateTime)
    {
        return uniqueResult(namedQuery("com.truecallertask.core.UserView.checkViewExists")
                .setParameter("viewerId", viewerId)
                .setParameter("viewedId", viewedId)
                .setParameter("viewDate", dateTime));
    }

    public List<UserView> getUserViewsOlderThanDate(DateTime dateTime) {
        Criteria criteria = criteria()
                .add(Restrictions.le("viewDate", dateTime));
        return list(criteria);
    }
}
