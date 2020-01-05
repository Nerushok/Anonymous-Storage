package io.anonymous.storage.presentation

import android.app.Application
import io.anonymous.storage.presentation.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AppDelegate : Application() {

    override fun onCreate() {
        super.onCreate()
        inject()
    }

    private fun inject() {
        startKoin {
            androidLogger(Level.INFO)
            androidContext(this@AppDelegate)
            modules(listOf(
                UseCasesModule,
                MappersModule,
                ViewModelModule,
                DataModule,
                PresentationModule
            ))
        }
    }
}