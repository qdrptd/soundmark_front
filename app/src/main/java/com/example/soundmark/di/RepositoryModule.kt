package com.example.soundmark.di

import com.example.soundmark.data.repository.map.MapRepository
import com.example.soundmark.data.repository.map.MapRepositoryImpl
import com.example.soundmark.data.repository.profile.MarkRepository
import com.example.soundmark.data.repository.profile.MarkRepositoryImpl
import com.example.soundmark.data.repository.profile.UserRepository
import com.example.soundmark.data.repository.profile.UserRepositoryImpl
import com.example.soundmark.data.repository.songDetail.SoundMarkDetailRepository
import com.example.soundmark.data.repository.songDetail.SoundMarkDetailRepositoryImpl
import com.example.soundmark.data.repository.spotify.SpotifyRepository
import com.example.soundmark.data.repository.spotify.SpotifyRepositoryImpl
import com.example.soundmark.data.repository.user.AuthRepository
import com.example.soundmark.data.repository.user.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSpotifyRepository(
        spotifyRepositoryImpl: SpotifyRepositoryImpl
    ): SpotifyRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds

    @Singleton
    abstract fun bindMarkRepository(
        markRepositoryImpl: MarkRepositoryImpl
    ): MarkRepository

    @Binds
    @Singleton
    abstract fun bindSoundMarkDetailRepository(
        soundMarkDetailRepositoryImpl: SoundMarkDetailRepositoryImpl
    ): SoundMarkDetailRepository


    @Binds
    @Singleton
    abstract fun bindMapRepository(
        mapRepositoryImpl: MapRepositoryImpl
    ): MapRepository
}
