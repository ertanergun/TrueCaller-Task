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
import java.util.List;


@Path("/view")
public class UserViewResource {

    private final UserViewDAO userViewDAO;
    private RepositoryHelper<UserView> repositoryHelper;
    private List<UserView> userViewList;
    private int userViewListLimit = 10;

    public UserViewResource(UserViewDAO userViewDAO, SessionFactory sessionFactory)
    {
        this.userViewDAO = userViewDAO;
        this.repositoryHelper = new RepositoryHelper<UserView>(sessionFactory);
    }

    /***
     * Records view action between two user with ids
     * @param viewerId
     * @param viewedId
     * @return
     */
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

            /***
             * The received view action can be cached here in a list and
             * inserted into database with large amount
             *
             * This arise missing record issue for listing last view actions
             * before inserting the recorded list into the database
             */
            UserView userView = new UserView(viewerId,viewedId, recordCreateDate);
            userViewDAO.saveOrUpdate(userView);

            String message = String.format("User with Id: %d is viewing the User with id: %d on %s",viewerId,viewedId, recordCreateDate.toString("dd/MM/yyyy hh:mm aa"));
            builder.append(message);
        }
        catch (Exception ex)
        {
            builder = new StringBuilder();
            builder.append("ERROR: " + ex.getMessage());
        }

        return getResponse(builder);
    }

    /***
     * Returns a list of user view actions for given user id
     * @param viewedId
     * @return
     */
    @GET
    @UnitOfWork
    @Path("/listviewerfor={viewedId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response listUserViews(@PathParam("viewedId") long viewedId) {

        StringBuilder builder = new StringBuilder();

        try {
            /**
             * If the user view actions are cached in a list
             * the list can be searched here and collect the missing
             * record from database to complete 10 items to display
             */

            List<UserView> viewList = this.userViewDAO.getViewList(viewedId, userViewListLimit);

            if(viewList.size() == 0)
            {
                builder.append("No records to display");
            }
            else
            {
                String header = String.format("The visit history for user with id %d: %n",viewedId);
                builder.append(header);
            }


            for (int i = 0; i < viewList.size(); i++) {
                String row = String.format("User with Id: %d on %s %n",viewList.get(i).getViewerId(), viewList.get(i).getViewDate().toString("dd/MM/yyyy hh:mm aa"));
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
