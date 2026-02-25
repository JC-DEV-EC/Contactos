package app.aplication.appproductos.core.di

import android.content.Context
import androidx.room.Room
import app.aplication.appproductos.contact.data.ContactDao
import app.aplication.appproductos.contact.data.ContactDatabase
import app.aplication.appproductos.contact.data.ContactRepositoryImpl
import app.aplication.appproductos.contact.domain.ContactRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ContactDatabase =
        Room.databaseBuilder(
            context,
            ContactDatabase::class.java,
            "contact_db"
        ).build()

    @Provides
    fun provideContactDao(db: ContactDatabase): ContactDao = db.contactDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindContactRepository(
        impl: ContactRepositoryImpl
    ): ContactRepository
}
