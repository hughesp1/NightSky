package com.example.android.opengl;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
 
public class parser extends Activity 
{
	static Context rootView;
    public static void parse(float ra[], float dec[], float mag[], int offSet) throws FileNotFoundException 
    {
    	
    	Scanner scanner = new Scanner(new File("stars.csv"));
    	scanner.useDelimiter(",");
    	int i =0;
        while (i<offSet)//scanner.hasNext()) 
        {
        	ra[i] = scanner.nextFloat();
        	dec[i] = scanner.nextFloat();
        	mag[i] = scanner.nextFloat();
            i++;
        } 
        scanner.close();
    }
}
