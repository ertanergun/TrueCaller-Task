package com.truecallertask.core;

import org.joda.time.DateTime;
import javax.persistence.*;

@Entity
@Table(name = "UserViewHistory")
@NamedQueries({
        @NamedQuery(
                name = "com.truecallertask.core.UserViewHistory.findAll",
                query = "SELECT u FROM UserViewHistory u"
        )
})
public class UserViewHistory {

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

    public UserViewHistory()
    {}

    public UserViewHistory(Long viewerId, Long viewedId, DateTime viewDate)
    {
        this.viewerId = viewerId;
        this.viewedId = viewedId;
        this.viewDate = viewDate;
    }
}
