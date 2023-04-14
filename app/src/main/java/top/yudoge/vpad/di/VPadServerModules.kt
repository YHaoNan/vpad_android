package top.yudoge.vpad.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import top.yudoge.vpad.api.LANIPGenerator
import top.yudoge.vpad.api.NetworkInterfacceIPGenerator
import top.yudoge.vpad.api.VPadServerScanner
import top.yudoge.vpadapi.DefaultVPadClient
import top.yudoge.vpadapi.VPadClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VPadServerModules {

    @Singleton
    @Provides
    fun provideLanIPGenerator(): LANIPGenerator {
        return NetworkInterfacceIPGenerator()
    }
    @Provides
    fun provideVPadClient(): VPadClient {
        return DefaultVPadClient()
    }

}