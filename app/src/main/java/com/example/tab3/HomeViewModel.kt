package com.example.tab3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import android.util.Log

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _contacts = MutableLiveData<List<MyItem>>()
    val contacts: LiveData<List<MyItem>> = _contacts

    private val fileName = "contacts.json"
    private val file = File(application.filesDir, fileName)
    private val idName = "maxId.txt"
    private val idFile = File(application.filesDir, idName)


    init {
        loadContacts()
    }

    private fun loadContacts() {
        if (file.exists()) {
            FileReader(file).use { reader ->
                val contactListType = object : TypeToken<List<MyItem>>() {}.type
                val contactList: List<MyItem>? = Gson().fromJson(reader, contactListType)
                _contacts.value = contactList ?: emptyList()
            }
        } else {
            _contacts.value = emptyList()
        }
    }
    fun saveMaxId(id: Int) {
        idFile.writeText(id.toString())
    }
    fun readMaxId(): Int {
        if (!idFile.exists()) {
            // 파일이 없으면 파일을 생성하고 0을 씁니다.
            idFile.writeText("0")
            return 0
        }
        return idFile.readText().toIntOrNull() ?: 0
    }
    fun addMaxId() {
        saveMaxId(readMaxId()+1)
    }
    fun setZeroId(){
        saveMaxId( 0)
    }

    fun addContact(contact: MyItem) {
        val updatedContacts = _contacts.value?.toMutableList() ?: mutableListOf()
        val contactExists = updatedContacts.any { it.name == contact.name && it.number == contact.number }
        if (!contactExists) {
            updatedContacts.add(contact)
            _contacts.value = updatedContacts
            saveContacts()
        }
    }
    fun updateContact(contact: MyItem) {
        Log.d("MaaaaaaaaaaUpdate", "$contact")
        val updatedContacts = _contacts.value?.toMutableList() ?: mutableListOf()
        val index = updatedContacts.indexOfFirst { it.profile == contact.profile }
        if (index != -1) {
            updatedContacts[index] = contact
            _contacts.value = updatedContacts
            saveContacts()
        }
    }
    private fun saveContacts() {
        FileWriter(file).use { writer ->
            Gson().toJson(_contacts.value, writer)
        }
    }
    fun deleteContact(contactId: Int) {
        val updatedContacts = _contacts.value?.toMutableList() ?: mutableListOf()
        val index = updatedContacts.indexOfFirst { it.profile == contactId }
        if (index != -1) {
            updatedContacts.removeAt(index)
            _contacts.value = updatedContacts
            saveContacts()
        }
    }
    fun deleteAllContacts() {
        _contacts.value = emptyList()
        saveContacts()
    }
}




/*
package com.example.tab3.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}*/