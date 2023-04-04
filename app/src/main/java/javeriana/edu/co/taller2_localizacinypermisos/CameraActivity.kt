package javeriana.edu.co.taller2_localizacinypermisos

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import javeriana.edu.co.taller2_localizacinypermisos.databinding.ActivityCameraBinding
import java.io.File

class CameraActivity : AppCompatActivity()
{
    // Binding
    private lateinit var bindingCamera: ActivityCameraBinding

    // Request Gallery
    private val galleryRequest = registerForActivityResult(ActivityResultContracts.GetContent(), ActivityResultCallback { imagePath : Uri? -> loadImage(imagePath) })

    // Request Camera
    private lateinit var cameraPath : Uri
    private val cameraRequest = registerForActivityResult(ActivityResultContracts.TakePicture(), ActivityResultCallback { loadImage( cameraPath ) })
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingCamera = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(bindingCamera.root)

        initializeElements()
    }

    private fun initializeElements()
    {

        initializeFileFromCamera()


        bindingCamera.galleryButton.setOnClickListener {
            // Open gallery
            galleryRequest.launch("image/*")
        }

        bindingCamera.cameraButton.setOnClickListener {
            cameraRequest.launch( cameraPath )
        }
    }

    private fun initializeFileFromCamera()
    {
        val fileToLoad = File(filesDir, "fileFromCamera")
        cameraPath = FileProvider.getUriForFile( this, applicationContext.packageName + ".fileprovider", fileToLoad)
    }



    // Load the image in the activity (from gallery or camera)
    private fun loadImage( imagePath : Uri? )
    {
        val imageStream = contentResolver.openInputStream( imagePath!! )
        val image = BitmapFactory.decodeStream( imageStream )

        bindingCamera.cameraImageView.setImageBitmap( image )
    }

}