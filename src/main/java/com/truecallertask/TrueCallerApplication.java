package com.truecallertask;

import com.truecallertask.core.UserView;
import com.truecallertask.data.UserViewDAO;
import com.truecallertask.resources.UserViewResource;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class TrueCallerApplication extends Application<TrueCallerApplicationConfiguration> {
    public static void main(String[] args) throws Exception {
        new TrueCallerApplication().run(args);
    }

    private final HibernateBundle<TrueCallerApplicationConfiguration> hibernateUserViewBundle =
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

        bootstrap.addBundle(hibernateUserViewBundle);
    }

    @Override
    public void run(TrueCallerApplicationConfiguration trueCallerApplicationConfiguration, Environment environment) throws Exception {

        final UserViewDAO userViewDAO =  new UserViewDAO(hibernateUserViewBundle.getSessionFactory());
        environment.jersey().register(new UserViewResource(userViewDAO));
    }
}
