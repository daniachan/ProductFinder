package com.dania.productfinder.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.dania.productfinder.MainActivity

@Suppress("unused")
@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
}