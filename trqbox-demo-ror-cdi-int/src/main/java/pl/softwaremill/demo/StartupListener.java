package pl.softwaremill.demo;

import org.jboss.weld.servlet.WeldListener;
import pl.softwaremill.common.util.dependency.BeanManagerDependencyProvider;
import pl.softwaremill.common.util.dependency.D;
import pl.softwaremill.common.util.dependency.DependencyProvider;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class StartupListener extends WeldListener {
    private DependencyProvider registeredDependencyProvider;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        super.contextInitialized(sce);

        // Setting a dependency provider with the right bean manager
        BeanManager bm;
        try {
            bm = (BeanManager) new InitialContext().lookup("java:comp/BeanManager");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        registeredDependencyProvider = new BeanManagerDependencyProvider(bm);
        D.register(registeredDependencyProvider);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        super.contextDestroyed(sce);

        // Clearing up
        if (registeredDependencyProvider != null) {
            D.unregister(registeredDependencyProvider);
        }
    }
}
