package com.example.taskmaster.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel

import com.example.taskmaster.models.Pick
import com.example.taskmaster.repository.PickRepo

class PickView(application: Application) : AndroidViewModel(application) {

    private val pickRepo = PickRepo(application)
    val tSFlow get() =  pickRepo.tSFlow
    val sLiData get() =  pickRepo.sLiData
    val sBLData get() =  pickRepo.sBLData

    fun setSortBy(sort:Pair<String,Boolean>){
        pickRepo.setSortBy(sort)
    }

    fun getTaskList(isAsc : Boolean, sortByName:String) {
        pickRepo.getTaskList(isAsc, sortByName)
    }

    fun insertTask(pick: Pick){
        pickRepo.insertTask(pick)
    }

    fun deleteTask(pick: Pick) {
        pickRepo.deleteTask(pick)
    }

    fun deleteTaskUsingId(taskId: String){
        pickRepo.deleteTaskUsingId(taskId)
    }

    fun updateTask(pick: Pick) {
        pickRepo.updateTask(pick)
    }

    fun updateTaskPaticularField(taskId: String,title:String,description:String) {
        pickRepo.updateTaskPaticularField(taskId, title, description)
    }
    fun searchTaskList(query: String){
        pickRepo.searchTaskList(query)
    }
}