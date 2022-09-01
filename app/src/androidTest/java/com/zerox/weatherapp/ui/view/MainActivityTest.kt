package com.zerox.weatherapp.ui.view

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.zerox.weatherapp.R
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
internal class MainActivityTest{

    @get:Rule var activityScenarioRule = activityScenarioRule<MainActivity>()

    fun checkActivityVisibility(){
        onView(withId(R.id.cl_container))
            .check(matches(isDisplayed()))
    }

    fun checkProgressBarVisibility(){
        onView(withId(R.id.pb_loading_data))
            .check(matches(isDisplayed()))
    }

    fun checkSection1Visibility(){
        onView(withId(R.id.cv_section1))
            .check(matches(isDisplayed()))
    }
    fun checkSectionWindVisibility(){
        onView(withId(R.id.cv_section_wind))
            .check(matches(isDisplayed()))
    }
    fun checkTempTextviewVisibility(){
        onView(withId(R.id.tv_temp))
            .check(matches(isDisplayed()))
    }
    fun checkCityTextviewVisibility(){
        onView(withId(R.id.tv_city))
            .check(matches(isDisplayed()))
    }


}
