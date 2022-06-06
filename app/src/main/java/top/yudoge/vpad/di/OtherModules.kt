package top.yudoge.vpad.di

import android.content.Context
import android.graphics.Typeface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import top.yudoge.vpad.api.CommunicatorHolder
import top.yudoge.vpad.domain.PadSettingDomain
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Pixel

@Module
@InstallIn(SingletonComponent::class)
object OtherModules {
    @Provides
    @Singleton
    @Pixel
    fun providePixelTypeFace(
        @ApplicationContext context: Context
    ) : Typeface {
        return Typeface.createFromAsset(context.assets, "ProggyClean.ttf")
    }

}

