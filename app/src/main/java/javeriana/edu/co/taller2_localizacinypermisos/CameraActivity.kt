package javeriana.edu.co.taller2_localizacinypermisos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import javeriana.edu.co.taller2_localizacinypermisos.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity()
{
    // Binding
    private lateinit var bindingCamera: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingCamera = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(bindingCamera.root)
    }
}