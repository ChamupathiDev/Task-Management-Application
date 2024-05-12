package com.example.taskmaster.dao

import androidx.room.*
import com.example.taskmaster.models.Pick
import kotlinx.coroutines.flow.Flow

@Dao
interface PickDao {


    @Query(
        """SELECT * FROM Pick ORDER BY
        CASE WHEN :isAsc = 1 THEN taskTitle END ASC, 
        CASE WHEN :isAsc = 0 THEN taskTitle END DESC"""
    )
    fun getPickListSortByPick(isAsc: Boolean) : Flow<List<Pick>>

    @Query(
        """SELECT * FROM Pick ORDER BY
        CASE WHEN :isAsc = 1 THEN date END ASC, 
        CASE WHEN :isAsc = 0 THEN date END DESC"""
    )
    fun getPickListSortByPickDate(isAsc: Boolean) : Flow<List<Pick>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPick(pick: Pick): Long


    // First way
    @Delete
    suspend fun deletePick(pick: Pick) : Int


    // Second Way
    @Query("DELETE FROM Pick WHERE taskId == :taskId")
    suspend fun deletePickUsingId(taskId: String) : Int


    @Update
    suspend fun updatePick(pick: Pick): Int


    @Query("UPDATE Pick SET taskTitle=:title, description = :description WHERE taskId = :taskId")
    suspend fun updatePickPaticularField(taskId:String,title:String,description:String): Int


    @Query("SELECT * FROM Pick WHERE taskTitle LIKE :query ORDER BY date DESC")
    fun searchPickList(query: String) : Flow<List<Pick>>
}