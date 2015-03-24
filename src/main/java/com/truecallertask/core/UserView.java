package com.truecallertask.core;

import org.joda.time.DateTime;
import javax.persistence.*;

@Entity
@Table(name = "UserViews")
@NamedQueries({
        @NamedQuery(
                name = "com.truecallertask.core.UserView.findAll",
                query = "SELECT u FROM UserView u"
        ),
        @NamedQuery(
                name = "com.truecallertask.core.UserView.getViewList",
                query = "SELECT u FROM UserView u WHERE u.viewedId=:viewedId AND u.viewDate >=:dateLimit ORDER BY u.viewDate"
        )
})
public class UserView {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Id", nullable = false)
    private Long id;

    @Column(name="ViewerId", nullable = false)
    private Long viewerId;

    @Column(name="ViewedId", nullable = false)
    private long viewedId;

    @Column(name = "ViewDate", nullable = false)
    private DateTime viewDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getViewerId() {
        return viewerId;
    }

    public void setViewerId(Long viewerId) {
        this.viewerId = viewerId;
    }

    public long getViewedId() {
        return viewedId;
    }

    public void setViewedId(Long viewedId) {
        this.viewedId = viewedId;
    }

    public DateTime getViewDate() {
        return viewDate;
    }

    public void setViewDate(DateTime viewDate) {
        this.viewDate = viewDate;
    }

    public UserView()
    {}

    public UserView(Long viewerId, Long viewedId, DateTime viewDate)
    {
        this.viewerId = viewerId;
        this.viewedId = viewedId;
        this.viewDate = viewDate;
    }
}
