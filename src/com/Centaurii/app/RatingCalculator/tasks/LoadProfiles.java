package com.Centaurii.app.RatingCalculator.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import com.Centaurii.app.RatingCalculator.GameRatingCalculatorActivity;
import com.Centaurii.app.RatingCalculator.R;
import com.Centaurii.app.RatingCalculator.implementations.ProfileHandler;
import com.Centaurii.app.RatingCalculator.interfaces.ProfileRetrieveAndSave;
import com.Centaurii.app.RatingCalculator.model.Profile;
import com.Centaurii.app.RatingCalculator.util.AppRater;
import com.Centaurii.app.RatingCalculator.util.Tags;

import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class LoadProfiles extends AsyncTask<Void, Void, Void>
{
    private GameRatingCalculatorActivity activity;
    private Dialog splashScreen;
    
    public LoadProfiles(GameRatingCalculatorActivity activity)
    {
        this.activity = activity;
    }
    
    @Override
    protected void onPreExecute()
    {
        LayoutInflater inflater = activity.getLayoutInflater();
        
        View view = inflater.inflate(R.layout.splash_screen, null, false);
        
        ImageView splash = (ImageView) view.findViewById(R.id.image);
        
        //If this is true, use the cat, else use the default one
        if((GameRatingCalculatorActivity.SPLASH_SCREEN()))
        {
            splash.setImageResource(R.drawable.splash_image);
        }
        else
        {
            splash.setImageResource(R.drawable.calc_splash);
        }
        
        splashScreen = new Dialog(activity, R.style.splashTheme);
        splashScreen.setCanceledOnTouchOutside(false);
        splashScreen.setCancelable(false);
        splashScreen.setContentView(view);
        splashScreen.show();
    }
    
    @Override
    protected Void doInBackground(Void... ignore)
    {
        //Check to make sure there is external storage available
        if(activity.checkExternalStorage())
        {
            String rawProfile = getFile();
            Log.i("LoadProfiles", "Contents of file:\n" + rawProfile);
            
            ProfileRetrieveAndSave prof = new ProfileHandler();
            ArrayList<Profile> listOfProfiles = prof.getProfiles(rawProfile);
            Collections.sort(listOfProfiles);
            activity.setSavedProfiles(listOfProfiles);
        }
        //External Storage is not available  
        else
        {
            //Inform user that profile information cannot be used
            Log.i("LoadProfiles", "No external storage.  You will not be able to save profiles");
        }
        
        
        try
        {
            Thread.sleep(3000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    protected void onCancelled()
    {
        if(splashScreen != null)
        {
            splashScreen.dismiss();
        }
    }
    
    @Override
    protected void onPostExecute(Void ignore)
    {
        if(splashScreen != null)
        {
            splashScreen.dismiss();
        }
        AppRater.app_launched(activity);
    }
    
    private String getFile()
    {
        File ratingsFolder = new File(Tags.RATINGS_FOLDER);
        File ratingsFile = new File(Tags.RATINGS_FOLDER, Tags.RATINGS_FILE);
        File newRatingsFile = new File(activity.getExternalFilesDir(null), Tags.RATINGS_FILE);
        
        String rawProfile = "";
        
        //Check to see if the old ratings folder already exists, get its info, then delete it
        if(ratingsFolder.exists())
        {
            BufferedReader reader = null;
            try
            {
                if(ratingsFile.exists())
                {
                    Log.i("LoadProfiles", "Reading ratings info");
                    reader = new BufferedReader(new FileReader(ratingsFile));
                    
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null)
                    {
                        builder.append(line + "\n");
                        if(isCancelled())
                        {
                            break;
                        }
                    }
                    
                    Log.i("LoadProfiles", "Deleting old ratings folder and file");
                    ratingsFile.delete();
                    ratingsFolder.delete();
                    
                    rawProfile = builder.toString();
                }
                else
                {
                    Log.i("LoadProfiles", "Deleting old ratings folder");
                    if(ratingsFolder.delete())
                    {
                        Log.i("LoadProfiles", "Success!");
                    }
                    else
                    {
                        Log.i("LoadProfiles", "Failure!");   
                    }
                }
            }
            catch (IOException e)
            {
                Log.i("LoadProfiles", "I/O: File could not be created or something else");
                e.printStackTrace();
            }
            catch (Exception e)
            {
                Log.i("LoadProfiles", "Unknown error, check stack trace");
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if(reader != null)
                    {
                        reader.close();
                    }
                } 
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        //If it doesn't exist, create it and set isFirstTime
        else if(newRatingsFile.exists())
        {
            BufferedReader reader = null;
            try
            {
                if(newRatingsFile.exists())
                {
                    Log.i("LoadProfiles", "Reading ratings info from profiles in new folder");
                    reader = new BufferedReader(new FileReader(newRatingsFile));
                    
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null)
                    {
                        builder.append(line + "\n");
                        if(isCancelled())
                        {
                            break;
                        }
                    }
                    
                    rawProfile = builder.toString();
                }
                else
                {
                    Log.i("LoadProfiles", "Deleting old ratings folder");
                    ratingsFolder.delete();
                }
            }
            catch (IOException e)
            {
                Log.i("LoadProfiles", "I/O: File could not be created or something else");
                e.printStackTrace();
            }
            catch (Exception e)
            {
                Log.i("LoadProfiles", "Unknown error, check stack trace");
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if(reader != null)
                    {
                        reader.close();
                    }
                } 
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            try
            {
                newRatingsFile.createNewFile();
            } 
            catch (IOException e)
            {
                Log.i("LoadProfiles", "File Creation Failed");
                e.printStackTrace();
            }
            activity.setFirstTime(true);
        }
        return rawProfile;
    }
}


