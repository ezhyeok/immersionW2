package com.example.tab3

import android.net.Uri
import android.util.Log
import androidx.room.*

@Dao
interface DiaryDao {
    @Query("SELECT * FROM tb_diary WHERE year = :year AND month = :month AND date = :date")
    fun getWorkoutByDate(year: Int, month: Int, date: Int): List<Diary>?

    @Query("SELECT * FROM tb_diary ORDER BY year, month, date ASC")
    fun getAll(): List<Diary>

    @Insert
    fun insertAll(vararg workout: Diary)

    @Delete
    fun delete(workout: Diary)

    @Update
    fun update(workout: Diary)

    @Transaction
    fun insertOrUpdate(workout: Diary) {
        val existingWorkout = getWorkoutByDate(workout.year, workout.month, workout.date)
        var check: Diary? = existingWorkout?.firstOrNull()
        if (check != null) {
            // Workout data already exists, update it
            update(workout)
        } else {
            // Workout data does not exist, insert a new one
            insertAll(workout)
        }
    }
}