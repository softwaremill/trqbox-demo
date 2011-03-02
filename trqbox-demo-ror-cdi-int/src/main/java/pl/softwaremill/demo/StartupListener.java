package pl.softwaremill.demo;

import org.jboss.weld.Container;
import org.jboss.weld.bootstrap.api.SingletonProvider;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.manager.BeanManagerImpl;
import org.jboss.weld.servlet.WeldListener;
import pl.softwaremill.common.util.dependency.BeanManagerDependencyProvider;
import pl.softwaremill.common.util.dependency.D;
import pl.softwaremill.common.util.dependency.DependencyProvider;

import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContextEvent;
import java.lang.reflect.*;
import java.util.Map;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class StartupListener extends WeldListener {
    private DependencyProvider registeredDependencyProvider;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        super.contextInitialized(sce);

        // Setting a dependency provider with the right bean manager
        BeanManager bm = hackAndGetBeanManager();
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

    private BeanManager hackAndGetBeanManager() {
        try {
            // Getting direct access to Container.instance.store
            // The store is org.jboss.weld.integration.provider.JBossSingletonProvider.EarSingleton
            // See http://anonsvn.jboss.org/repos/jbossas/trunk/weld-int/deployer/src/main/java/org/jboss/weld/integration/provider/JBossSingletonProvider.java
            Field instanceField = Container.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            Object instanceFieldValue = instanceField.get(null);

            Field storeField = instanceFieldValue.getClass().getDeclaredField("store");
            storeField.setAccessible(true);
            Map storeFieldValue = (Map) storeField.get(instanceFieldValue);

            // There should be only one container
            final ClassLoader circularEarClassloader = (ClassLoader) storeFieldValue.keySet().iterator().next();
            Container container = (Container) storeFieldValue.values().iterator().next();

            // Hacking the store to always return the found container, no matter which classloader is found
            // Implementing http://anonsvn.jboss.org/repos/jbossas/trunk/weld-int/deployer/src/main/java/org/jboss/weld/integration/deployer/env/bda/TopLevelClassLoaderGetter.java
            // to do this.
            Class topLevelClassLoaderGetterClass = Thread.currentThread().getContextClassLoader().loadClass(
                    "org.jboss.weld.integration.deployer.env.bda.TopLevelClassLoaderGetter");

            Method getTopLevelClassLoaderMethod = instanceFieldValue.getClass().getDeclaredMethod("getTopLevelClassLoader");
            getTopLevelClassLoaderMethod.setAccessible(true);

            SingletonProvider singletonProviderInstance = SingletonProvider.instance();
            singletonProviderInstance.getClass().getMethod("setTopLevelClassLoaderGetter", topLevelClassLoaderGetterClass)
                    .invoke(singletonProviderInstance, Proxy.newProxyInstance(
                            Thread.currentThread().getContextClassLoader(),
                            new Class[]{topLevelClassLoaderGetterClass},
                            new InvocationHandler() {
                                @Override
                                public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                                    return circularEarClassloader;
                                }
                            }));

            // Looking for the ear's bean manager and returning it
            for (Map.Entry<BeanDeploymentArchive, BeanManagerImpl> entry : container.beanDeploymentArchives().entrySet()) {
                if (entry.getKey().getId().contains("trqbox-demo")) {
                    return entry.getValue();
                }
            }

            throw new RuntimeException("No trqbox-demo archive found in: " + container.beanDeploymentArchives());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}