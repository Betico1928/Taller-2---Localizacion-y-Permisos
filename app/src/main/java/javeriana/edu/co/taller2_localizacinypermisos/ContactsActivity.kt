package javeriana.edu.co.taller2_localizacinypermisos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import javeriana.edu.co.taller2_localizacinypermisos.databinding.ActivityContactsBinding

class ContactsActivity : AppCompatActivity()
{
    private lateinit var bindingContacts : ActivityContactsBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingContacts = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(bindingContacts.root)
    }
}