package io.b3.quicktalk

import android.content.Context
import dagger.Module
import dagger.Provides
import io.b3.quicktalk.activity.CardViewActivity
import io.b3.quicktalk.activity.SelectActivity
import io.b3.quicktalk.config.ConfigService
import io.b3.quicktalk.config.ConfigServiceImpl
import io.b3.quicktalk.dataprovider.DataProvider
import io.b3.quicktalk.dataprovider.DataProviderImpl
import io.b3.quicktalk.engine.CardManager
import io.b3.quicktalk.engine.CardManagerImpl
import io.b3.quicktalk.engine.CardSetCatalog
import io.b3.quicktalk.engine.CardSetCatalogImpl
import io.b3.quicktalk.engine.CardSetFactory
import io.b3.quicktalk.engine.Tutor
import io.b3.quicktalk.engine.TutorImpl
import io.b3.quicktalk.internal.Speaker
import io.b3.quicktalk.internal.SpeakerImpl
import io.b3.quicktalk.internal.Timer
import io.b3.quicktalk.internal.TimerImpl
import javax.inject.Singleton

@Module(injects = arrayOf(CardViewActivity::class, SelectActivity::class))
class AppModule(private val context: Context) {

    @Provides
    @Singleton
    internal fun provideConfigService(): ConfigService = ConfigServiceImpl(context)

    @Provides
    @Singleton
    internal fun provideTutor(configService: ConfigService, catalog: CardSetCatalog,
                              manager: CardManager, speaker: Speaker, timer: Timer): Tutor =
            TutorImpl(configService, catalog, manager, speaker, timer)
    @Provides
    @Singleton
    internal fun provideCardManager(): CardManager = CardManagerImpl()

    @Provides
    @Singleton
    internal fun provideCardSetFactory(dataProvider: DataProvider): CardSetFactory =
            CardSetFactory(dataProvider)

    @Provides
    @Singleton
    internal fun provideCardSetCatalog(dataProvider: DataProvider, factory: CardSetFactory): CardSetCatalog =
            CardSetCatalogImpl(dataProvider, factory)

    @Provides
    @Singleton
    internal fun provideDataProvider(): DataProvider = DataProviderImpl(context)

    @Provides
    @Singleton
    internal fun provideTimer(): Timer = TimerImpl()

    @Provides
    @Singleton
    internal fun provideSpeaker(): Speaker = SpeakerImpl(context)

}