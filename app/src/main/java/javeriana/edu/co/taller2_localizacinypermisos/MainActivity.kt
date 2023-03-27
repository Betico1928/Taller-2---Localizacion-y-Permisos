package javeriana.edu.co.taller2_localizacinypermisos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import javeriana.edu.co.taller2_localizacinypermisos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity()
{
    private lateinit var bindingMain : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)

        initializeElements()

        Log.i("Taller 2", "MainActivity created")
    }

    private fun initializeElements()
    {
        // Camera button:
        bindingMain.imageButtonCamera.setOnClickListener{

            Log.i("Taller 2", "Camera button pressed.")
        }


        // Contacts button:
        bindingMain.imageButtonContacts.setOnClickListener{

            Log.i("Taller 2", "Contacts button pressed.")

            //val goToCameraActivity = Intent
        }


        // OSM button:
        bindingMain.imageButtonOSM.setOnClickListener {

            Log.i("Taller 2", "OSM button pressed.")

            //val goToOSMActivity = Intent
        }


        // Google Maps button:
        bindingMain.imageButtonOSM.setOnClickListener {

            Log.i("Taller 2", "Google Maps button pressed.")

            //val goToGoogleMapsActivity = Intent
        }
    }
}