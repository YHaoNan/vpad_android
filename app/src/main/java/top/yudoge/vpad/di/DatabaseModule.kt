package top.yudoge.vpad.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import top.yudoge.vpad.db.VPadDatabase
import top.yudoge.vpad.db.dao.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

//    @Provides
//    @Singleton
//    fun provideDatabase(
//        @ApplicationContext context: Context
//    ): VPadDatabase {
//        return VPadDatabase.getInstance(context)
//    }


}