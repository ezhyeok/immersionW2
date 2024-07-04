package com.example.tab3.ui.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.lifecycle.map

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val _images = MutableLiveData<List<Uri>>()
    val images: LiveData<List<Uri>> = _images
    private val fileName = "img.json"
    private val file = File(application.filesDir, fileName)
    private val idName = "maxImgId.txt"
    private val idFile = File(application.filesDir, idName)


    init {
        loadImages()
    }

    fun loadImages() {
        if (file.exists()) {
            FileReader(file).use { reader ->
                val imageStringsType = object : TypeToken<List<String>>() {}.type
                val imageStrings: List<String> = Gson().fromJson(reader, imageStringsType)
                _images.value = imageStrings.map { Uri.parse(it) }
            }
        } else {
            _images.value = emptyList()
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

    fun addImages(contact: List<Uri>) {
        val updatedContacts = _images.value?.toMutableList() ?: mutableListOf()
        //val contactExists = updatedContacts.any { it.name == contact.name && it.number == contact.number }
        Log.d("updateImages_model", "$contact")
        updatedContacts.addAll(contact)
        Log.d("updateImages_model2", "$updatedContacts")

        _images.value = updatedContacts
        saveImages()
    }
    fun updateContact(contact: Uri) {
        val updatedContacts = _images.value?.toMutableList() ?: mutableListOf()
        //val index = updatedContacts.indexOfFirst { it.profile == contact.profile }
        //updatedContacts[index] = contact
        //_contacts.value = updatedContacts
        //saveContacts()
    }
    private fun saveImages() {
        FileWriter(file).use { writer ->

            val imageStrings = _images.value?.map { it.toString() } ?: emptyList()
            // 리스트를 JSON으로 변환하여 파일에 작성
            Log.d("updateImages_model3", "$imageStrings")
            Gson().toJson(imageStrings, writer)
        }
    }
    fun deleteImage(index: Int) {

        // _images의 현재 값을 읽어옴. null이면 빈 리스트를 사용
        val currentImages = _images.value.orEmpty()
        val updatedContacts = currentImages.toMutableList()
        updatedContacts.removeAt(index)
        if (updatedContacts.isEmpty()) {
            _images.value = emptyList()
        }else {
            _images.value = updatedContacts
        }
        saveImages()
    }
    fun deleteAllImages() {
        _images.value = emptyList()
        saveImages()
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