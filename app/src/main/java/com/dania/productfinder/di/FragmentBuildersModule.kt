package com.dania.productfinder.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.dania.productfinder.ui.search.SearchFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment
}