package com.Centaurii.app.RatingCalculator;

import java.util.ArrayList;

import com.Centaurii.app.RatingCalculator.fragments.HomeScreenFragment;
import com.Centaurii.app.RatingCalculator.model.Profile;
import com.Centaurii.app.RatingCalculator.tasks.LoadProfiles;
import com.Centaurii.app.RatingCalculator.tasks.SaveProfiles;
import com.Centaurii.app.RatingCalculator.util.Tags;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * Controller class that sets up listeners/event handlers and interacts with Calculator.java based on user input
 * Created 9/10/12
 * Last updated 10/14/13
 * @author Gautam Narula
 *
 */

public class GameRatingCalculatorActivity extends FragmentActivity 
{
    private boolean isFirstTime;
    private ArrayList<Profile> savedProfiles;
    
    /* Saved Preference Variables*/
    public static int MAX_PROFILES, MAX_PLAYERS, DEFAULT_PROVISIONAL, DEFAULT_RATING;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        
        MAX_PROFILES = Integer.valueOf(sharedPrefs.getString(Tags.MAX_PROFILES, "20"));
        MAX_PLAYERS = Integer.valueOf(sharedPrefs.getString(Tags.MAX_PLAYERS, "6"));
        DEFAULT_PROVISIONAL = Integer.valueOf(sharedPrefs.getString(Tags.DEFAULT_PROVISIONAL, "5"));
        DEFAULT_RATING = Integer.valueOf(sharedPrefs.getString(Tags.DEFAULT_RATING, "1000"));
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        if(savedInstanceState == null)
        {
            new LoadProfiles(this).execute();
            getSupportFragmentManager().beginTransaction()
                                       .add(R.id.main_frame, new HomeScreenFragment())
                                       .commit();
        }
        else
        {
            //For now do the same thing.  In the future, change it to something else
            new LoadProfiles(this).execute();
        }
        
        Log.i("GameRating", "Max Players: " + sharedPrefs.getString("max_profiles", "20"));
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        Log.i("GameRatingCalculator", "OnPause() is running");
        new SaveProfiles(this).execute();
    }
    
    @Override
    protected void onRestart()
    {
        super.onRestart();
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        
        MAX_PROFILES = Integer.valueOf(sharedPrefs.getString(Tags.MAX_PROFILES, "20"));
        MAX_PLAYERS = Integer.valueOf(sharedPrefs.getString(Tags.MAX_PLAYERS, "6"));
        DEFAULT_PROVISIONAL = Integer.valueOf(sharedPrefs.getString(Tags.DEFAULT_PROVISIONAL, "5"));
        DEFAULT_RATING = Integer.valueOf(sharedPrefs.getString(Tags.DEFAULT_RATING, "1000"));
    }
    
    public boolean checkExternalStorage()
    {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean isFirstTime()
    {
        return isFirstTime;
    }

    public void setFirstTime(boolean isFirstTime)
    {
        this.isFirstTime = isFirstTime;
    }

    public ArrayList<Profile> getSavedProfiles()
    {
        return savedProfiles;
    }

    public void setSavedProfiles(ArrayList<Profile> savedProfiles)
    {
        this.savedProfiles = savedProfiles;
    }

    public static int MAX_PROFILES()
    {
        return MAX_PROFILES;
    }

    public static int MAX_PLAYERS()
    {
        return MAX_PLAYERS;
    }
    
    public static int DEFAULT_PROVISIONAL()
    {
        return DEFAULT_PROVISIONAL;
    }
    
    public static int DEFAULT_RATING()
    {
        return DEFAULT_RATING;
    }
}