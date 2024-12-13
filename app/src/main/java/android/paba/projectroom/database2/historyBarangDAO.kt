package android.paba.projectroom.database2

import android.paba.projectroom.database.daftarBelanja
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface historyBarangDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(daftar: historyBarang)

    @Query("SELECT * FROM historyBarang ORDER BY id asc")
    fun selectAll(): MutableList<historyBarang>

    @Query("SELECT * FROM historyBarang WHERE id=:isi_id")
    suspend fun getItem(isi_id : Int) : historyBarang
}