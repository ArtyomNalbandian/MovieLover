package com.example.movielover

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.movielover.view.activity.MainActivity
import org.junit.Rule
import org.junit.Test

class MovieCardFragmentTest {
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun checkMovieCardFragment() {
        onView(withId(R.id.movieCardCountry))
        onView(withId(R.id.movieCardYear))
        onView(withId(R.id.addToMyFavouriteBtn))
    }
}