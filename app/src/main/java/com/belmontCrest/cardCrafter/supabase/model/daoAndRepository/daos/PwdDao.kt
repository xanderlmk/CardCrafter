package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.belmontCrest.cardCrafter.localDatabase.tables.Pwd

@Dao
interface PwdDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun insertPwd(pwd : Pwd)

    @Delete
    fun deletePwd(pwd: Pwd)

    @Query("""SELECT * from pwd""")
    fun getPwd() : Pwd?
}