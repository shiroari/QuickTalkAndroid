package io.b3.quicktalk;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import io.b3.quicktalk.engine.CardManager;
import io.b3.quicktalk.config.ConfigService;
import io.b3.quicktalk.engine.Tutor;
import io.b3.quicktalk.engine.CardManagerImpl;
import io.b3.quicktalk.config.ConfigServiceImpl;
import io.b3.quicktalk.engine.TutorImpl;

import static org.junit.Assert.assertEquals;

public class TutorTest {

    private Tutor subject;

    @Inject
    CardManager manager;

    @Inject
    ConfigService configService;

    @Before
    public void setUp() {
        //ObjectGraph.create(new TestModule()).inject(this);
        //subject = new TutorImpl(manager, configService);
    }

    @Test
    public void test() throws Exception {
        //assertEquals(subject.isPaused(), true);
    }

    @Module(
            includes = AppModule.class,
            injects = TutorTest.class,
            overrides = true
    )
    static class TestModule {
        @Provides
        @Singleton
        ConfigService provideConfigService() {
            return new ConfigServiceImpl();
        }

        @Provides
        @Singleton
        CardManager provideCardManager() {
            return new CardManagerImpl();
        }
    }
}