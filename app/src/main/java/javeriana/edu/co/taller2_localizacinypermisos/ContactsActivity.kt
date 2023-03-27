package javeriana.edu.co.taller2_localizacinypermisos

import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import javeriana.edu.co.taller2_localizacinypermisos.databinding.ActivityContactsBinding

class ContactsActivity : AppCompatActivity()
{
    // Binding
    private lateinit var bindingContacts : ActivityContactsBinding

    // Lo que se va a mostrar en la lista de contactos
    private val contactsProjection = arrayOf(ContactsContract.Profile._ID, ContactsContract.Profile.DISPLAY_NAME_PRIMARY)
    // Cursor
    private lateinit var contactsCursor : Cursor

    // Solicitud de permiso
    private var contactsPermission = registerForActivityResult(

        // Request permission
        ActivityResultContracts.RequestPermission(),
        ActivityResultCallback{ contactPermissionStatus ->

            Log.i("Taller 2", "Contacts Permission: $contactPermissionStatus")

            initializeElements(contactPermissionStatus)
        }
    )


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingContacts = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(bindingContacts.root)

        // Mostrar los elementos de la UI
        bindingContacts.PermissionLinearLayout.visibility = View.VISIBLE
        bindingContacts.statusContacts.visibility = View.VISIBLE
        bindingContacts.contactsPermissionButton.visibility = View.VISIBLE

        getContactsPermission()
    }

    private fun getContactsPermission()
    {
        // Si el permiso NO ha sido aceptado:
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            // Mostrar el porque se necesita el permiso.
            bindingContacts.statusContacts.text = "The app needs the contacts in order to show them"

            /*
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CONTACTS))
            {
                Toast.makeText(this, "The app needs the contacts in order to show them", Toast.LENGTH_LONG).show()
            }
            */

            bindingContacts.contactsPermissionButton.setOnClickListener {
                // Perdir el permiso (variable de registerForActivityResult)
                contactsPermission.launch(android.Manifest.permission.READ_CONTACTS)
            }
        }

        // Si el permiso ya ha sido aceptado.
        else
        {
            initializeElements(true)
        }

    }



    private fun initializeElements(contactPermissionStatus: Boolean)
    {
        // Esconder el boton de OK
        bindingContacts.contactsPermissionButton.visibility = View.GONE

        if (contactPermissionStatus)
        {
            // Hide PermissionLinearLayout
            bindingContacts.PermissionLinearLayout.visibility = View.GONE


        }
        else
        {
            bindingContacts.statusContacts.text = "Permission Denied"
            bindingContacts.statusContacts.setTextColor(Color.RED)
        }
    }
}