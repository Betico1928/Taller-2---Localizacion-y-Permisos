package javeriana.edu.co.taller2_localizacinypermisos

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import javeriana.edu.co.taller2_localizacinypermisos.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity()
{
    // Binding
    private lateinit var bindingCamera: ActivityCameraBinding

    // Request Gallery
    val galleryRequest = registerForActivityResult(ActivityResultContracts.GetContent(), ActivityResultCallback { imagePath : Uri? -> loadImage(imagePath) })

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingCamera = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(bindingCamera.root)

        initializeElements()
    }

    private fun initializeElements()
    {
        bindingCamera.galleryButton.setOnClickListener {
            // Open gallery
            galleryRequest.launch("image/*")
        }
    }

    // Load the image in the activity
    private fun loadImage( imagePath : Uri? )
    {
        val imageStream = contentResolver.openInputStream( imagePath!! )
        val image = BitmapFactory.decodeStream( imageStream )

        bindingCamera.cameraImageView.setImageBitmap( image )
    }
}