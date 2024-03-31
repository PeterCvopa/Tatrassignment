package com.goodrequest.hiring.di

import com.goodrequest.hiring.api.PokemonManager
import com.goodrequest.hiring.api.PokemonRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PokemonModule {
    @Binds
    abstract fun providePokemonRepository(repository: PokemonRepositoryImpl): PokemonManager
}