package com.truecallertask;

import com.truecallertask.core.UserView;
import com.truecallertask.core.UserViewHistory;
import com.truecallertask.data.UserViewDAO;
import com.truecallertask.data.UserViewHistoryDAO;
import com.truecallertask.helper.ArchiveOldViewHistory;
import com.truecallertask.resources.UserViewResource;
import com.truecallertask.task.ArchiveOldViewHistoryTask;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TrueCallerApplication extends Application<TrueCallerApplicationConfiguration> {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) throws Exception {
        new TrueCallerApplication().run(args);
    }

    private final HibernateBundle<TrueCallerApplicationConfiguration> hibernateBundle =
            new HibernateBundle<TrueCallerApplicationConfiguration>(UserView.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(TrueCallerApplicationConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    @Override
    public String getName() {
        return "TrueCaller Java-Task";
    }

    @Override
    public void initialize(Bootstrap<TrueCallerApplicationConfiguration> bootstrap) {

        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap.addBundle(new MigrationsBundle<TrueCallerApplicationConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(TrueCallerApplicationConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });

        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(TrueCallerApplicationConfiguration trueCallerApplicationConfiguration, Environment environment) throws Exception {

        final UserViewDAO userViewDAO =  new UserViewDAO(hibernateBundle.getSessionFactory());
        final UserViewHistoryDAO userViewHistoryDAO = new UserViewHistoryDAO(hibernateBundle.getSessionFactory());
        ArchiveOldViewHistoryTask archiveOldUserViewsTask = new ArchiveOldViewHistoryTask(userViewDAO, userViewHistoryDAO, hibernateBundle.getSessionFactory());
        Runnable archiveOldUserViewsSchedule = new ArchiveOldViewHistory(userViewDAO, userViewHistoryDAO, hibernateBundle.getSessionFactory());
        scheduler.scheduleAtFixedRate(archiveOldUserViewsSchedule, 8, 8, TimeUnit.HOURS);
        environment.admin().addTask(archiveOldUserViewsTask);
        environment.jersey().register(new UserViewResource(userViewDAO,hibernateBundle.getSessionFactory()));
    }
}
