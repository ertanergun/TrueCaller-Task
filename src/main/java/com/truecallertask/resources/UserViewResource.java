package com.truecallertask.resources;

import com.truecallertask.core.UserView;
import com.truecallertask.data.UserViewDAO;
import com.truecallertask.helper.RepositoryHelper;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Path("/view")
public class UserViewResource {

    private final UserViewDAO userViewDAO;
    private RepositoryHelper<UserView> repositoryHelper;
    private List<UserView> userViewList;

    public UserViewResource(UserViewDAO userViewDAO, SessionFactory sessionFactory)
    {
        this.userViewDAO = userViewDAO;
        this.repositoryHelper = new RepositoryHelper<UserView>(sessionFactory);
        this.userViewList = new ArrayList<UserView>();
    }

    @GET
    @UnitOfWork
    @Path("/viewer={viewerId}&viewing={viewedId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response recordUserView(@PathParam("viewerId") Long viewerId, @PathParam("viewedId") Long viewedId) {

        StringBuilder builder = new StringBuilder();
        try
        {
            DateTime today = DateTime.now();
            DateTime recordCreateDate = new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), today.getHourOfDay(), today.getMinuteOfHour(), 0, 0);
            UserView userView = new UserView(viewerId,viewedId, recordCreateDate);

            if(!userViewList.contains(userView)) {
                userViewList.add(userView);
            }

            if (userViewList.size() == 10) {
                this.repositoryHelper.saveOrUpdateAll(userViewList);
                userViewList.clear();
            }
            String message = String.format("User with Id: %d is viewing the User with id: %d on %s",viewerId,viewedId, recordCreateDate.toString("dd/MM/yyyy hh:mm"));
            builder.append(message);
        }
        catch (Exception ex)
        {
            builder = new StringBuilder();
            builder.append("ERROR: " + ex.getMessage());
        }

        return getResponse(builder);
    }

    @GET
    @UnitOfWork
    @Path("/listviewerfor={viewedId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response listUserViews(@PathParam("viewedId") long viewedId) {

        StringBuilder builder = new StringBuilder();
        List<UserView> viewList  = new ArrayList<UserView>();
        try {
            for(UserView userView : userViewList) {
                if(userView.getViewedId() == viewedId) {
                    viewList.add(userView);
                }
            }

            Collections.sort(viewList, new Comparator<UserView>() {
                @Override
                public int compare(UserView t1, UserView t2) {
                    return t1.getViewDate().compareTo(t2.getViewDate());
                }
            });

            if(viewList.size() != 10)
            {
                viewList.addAll(this.userViewDAO.getViewList(viewedId, 10 - viewList.size()));
            }

            if(viewList.size() == 0)
            {
                builder.append("No records to display");
            }

            for (int i = 0; i < viewList.size(); i++) {
                String row = String.format("User with Id: %d was viewed the User with id: %d on %s %n",viewList.get(i).getViewerId(),viewList.get(i).getViewedId(), viewList.get(i).getViewDate().toString("dd/MM/yyyy hh:mm"));
                builder.append(row);
            }
        }
        catch (Exception ex)
        {
            builder = new StringBuilder();
            builder.append("ERROR: " + ex.getMessage());
        }

       return getResponse(builder);
    }

    private Response getResponse(final StringBuilder builder) {
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException,
                    WebApplicationException {
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                writer.write(builder.toString());
                writer.flush();
            }
        };
        return Response.ok(stream).build();
    }
}
