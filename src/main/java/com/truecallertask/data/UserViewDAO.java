package com.truecallertask.data;

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


    /***
     * Saves a new userview or updates the existing one
     * @param userView
     * @return
     */
    public UserView saveOrUpdate(UserView userView) {
        return persist(userView);
    }

    /***
     * Returns all userviews from database
     * @return
     */
    public List<UserView> findAll() {
        return list(namedQuery("com.truecallertask.core.UserView.findAll"));
    }

    /***
     * Returns list of userviews for given viewId
     * andlimits the query result by remainingRecordSize
     * @param viewId
     * @param remainingRecordSize
     * @return
     */
    public List<UserView> getViewList(Long viewId, int remainingRecordSize){
        DateTime today = DateTime.now();

        return list(
                namedQuery("com.truecallertask.core.UserView.getViewList")
                        .setParameter("dateLimit", new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 0, 0, 0, 0).minusDays(10))
                        .setParameter("viewedId", viewId)
                        .setMaxResults(remainingRecordSize));
    }

    /**
     * Returns userviews older than datetime parameter
     * @param dateTime
     * @return
     */
    public List<UserView> getUserViewsOlderThanDate(DateTime dateTime) {
        Criteria criteria = criteria()
                .add(Restrictions.le("viewDate", dateTime));
        return list(criteria);
    }

    /***
     * Possible way to validate record before inserting
     * if returns null, the data does not exists
     * @param viewerId
     * @param viewedId
     * @param dateTime
     * @return
     */
    public UserView checkUserView(Long viewerId, Long viewedId, DateTime dateTime)
    {
        return uniqueResult(namedQuery("com.truecallertask.core.UserView.checkViewExists")
                .setParameter("viewerId", viewerId)
                .setParameter("viewedId", viewedId)
                .setParameter("viewDate", dateTime));
    }
}
