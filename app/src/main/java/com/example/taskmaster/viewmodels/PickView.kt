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

    fun getPickList(isAsc : Boolean, sortByName:String) {
        pickRepo.getPickList(isAsc, sortByName)
    }

    fun insertPick(pick: Pick){
        pickRepo.insertPick(pick)
    }

    fun deletePick(pick: Pick) {
        pickRepo.deletePick(pick)
    }

    fun deletePickUsingId(taskId: String){
        pickRepo.deletePickUsingId(taskId)
    }

    fun updatePick(pick: Pick) {
        pickRepo.updatePick(pick)
    }

    fun updatePickPaticularField(taskId: String,title:String,description:String) {
        pickRepo.updatePickPaticularField(taskId, title, description)
    }
    fun searchPickList(query: String){
        pickRepo.searchPickList(query)
    }
}