package com.truecallertask.resources;

import com.truecallertask.core.UserView;
import com.truecallertask.data.UserViewDAO;
import io.dropwizard.hibernate.UnitOfWork;
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

    public UserViewResource(UserViewDAO userViewDAO)
    {
        this.userViewDAO = userViewDAO;
    }

    @GET
    @UnitOfWork
    @Path("/viewer={viewerId}&viewing={viewedId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getUserView(@PathParam("viewerId") Long viewerId, @PathParam("viewedId") Long viewedId) {

        StringBuilder builder = new StringBuilder();
        try
        {
            UserView userView = new UserView(viewerId,viewedId, DateTime.now());
            this.userViewDAO.create(userView);
            String message = String.format("User with Id: %d is viewing the User with id: %d on %s",userView.getViewerId(),userView.getViewedId(), userView.getViewDate().toString());
            builder.append(message);
        }
        catch (Exception ex)
        {
            builder = new StringBuilder();
            builder.append(ex.getMessage());
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
            viewList = this.userViewDAO.getViewList(viewedId);

            if(viewList.size() == 0)
            {
                builder.append("No records to display");
            }

            for (int i = 0; i < viewList.size(); i++) {
                String row = String.format("User with Id: %d was viewed the User with id: %d on %s %n",viewList.get(i).getViewerId(),viewList.get(i).getViewedId(), viewList.get(i).getViewDate().toString());
                builder.append(row);
            }
        }
        catch (Exception ex)
        {
            builder = new StringBuilder();
            builder.append(ex.getMessage());
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
