package pl.softwaremill.demo;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.mockito.Mockito.*;
import static org.fest.assertions.Assertions.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class HelloWorldTest extends Arquillian {
    @Deployment
    public static JavaArchive createTestArchive() {
        JavaArchive ar = ShrinkWrap.create(JavaArchive.class)
                .addClass(HelloWorld.class)
                .addClass(SecondsCounter.class);

        // Creating beans.xml
        ar = ar.addManifestResource(
                new ByteArrayAsset("<beans></beans>".getBytes()),
                ArchivePaths.create("beans.xml"));

        return ar;
    }

    @Inject
    private HelloWorld helloWorld;

    @Inject
    private SecondsCounter secondsCounter;

    @Test
    public void testInitialHelloWorld() {
        assertThat(helloWorld.getMessage()).isEqualTo("Hello world! 0");
    }

    @Test(dependsOnMethods = "testInitialHelloWorld")
    public void testGetIncreasesAfterInc() {
        // When
        secondsCounter.inc();
        secondsCounter.inc();

        // Then
        assertThat(secondsCounter.getSeconds()).isEqualTo(2);
    }

    @Test(dependsOnMethods = "testGetIncreasesAfterInc")
    public void testHelloWorldAfterTwoSeconds() {
        assertThat(helloWorld.getMessage()).isEqualTo("Hello world! 2");
    }
}
