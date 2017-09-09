package io.b3.quicktalk;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.b3.quicktalk.activity.CardViewActivity;
import io.b3.quicktalk.activity.SelectActivity;
import io.b3.quicktalk.engine.CardManager;
import io.b3.quicktalk.engine.CardSetCatalog;
import io.b3.quicktalk.config.ConfigService;
import io.b3.quicktalk.dataprovider.DataProvider;
import io.b3.quicktalk.internal.Speaker;
import io.b3.quicktalk.internal.Timer;
import io.b3.quicktalk.engine.Tutor;
import io.b3.quicktalk.engine.CardManagerImpl;
import io.b3.quicktalk.engine.CardSetCatalogImpl;
import io.b3.quicktalk.engine.CardSetFactory;
import io.b3.quicktalk.config.ConfigServiceImpl;
import io.b3.quicktalk.dataprovider.DataProviderImpl;
import io.b3.quicktalk.internal.SpeakerImpl;
import io.b3.quicktalk.internal.TimerImpl;
import io.b3.quicktalk.engine.TutorImpl;

/**
 * @author Stas Sukhanov
 * @since 6.08.2016
 */
@Module(injects = { CardViewActivity.class, SelectActivity.class })
public class AppModule {

    @Provides
    @Singleton
    ConfigService provideConfigService() {
        return new ConfigServiceImpl();
    }

    @Provides
    @Singleton
    Tutor provideTutor(ConfigService configService, CardSetCatalog catalog,
                       CardManager manager, Speaker speaker, Timer timer) {
        return new TutorImpl(configService, catalog, manager, speaker, timer);
    }

    @Provides
    @Singleton
    CardManager provideCardManager() {
        return new CardManagerImpl();
    }

    @Provides
    @Singleton
    CardSetFactory provideCardSetFactory(DataProvider dataProvider) {
        return new CardSetFactory(dataProvider);
    }

    @Provides
    @Singleton
    CardSetCatalog provideCardSetCatalog(DataProvider dataProvider, CardSetFactory factory) {
        return new CardSetCatalogImpl(dataProvider, factory);
    }

    @Provides
    @Singleton
    DataProvider provideDataProvider() {
        return new DataProviderImpl(AppContext.context());
    }

    @Provides
    @Singleton
    Timer provideTimer() {
        return new TimerImpl();
    }

    @Provides
    @Singleton
    Speaker provideSpeaker() {
        return new SpeakerImpl(AppContext.context());
    }

}
