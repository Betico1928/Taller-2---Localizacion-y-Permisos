package javeriana.edu.co.taller2_localizacinypermisos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import javeriana.edu.co.taller2_localizacinypermisos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity()
{
    private lateinit var bindingMain : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)
    }
}