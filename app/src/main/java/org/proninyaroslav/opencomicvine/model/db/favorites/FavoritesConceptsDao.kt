package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesConceptItem

@Dao
interface FavoritesConceptsDao {
    @Query("SELECT * FROM PagingFavoritesConceptItem ORDER BY `index` ASC")
    fun getAll(): PagingSource<Int, PagingFavoritesConceptItem>

    @Query("SELECT * FROM PagingFavoritesConceptItem ORDER BY `index` ASC LIMIT :count")
    fun get(count: Int): PagingSource<Int, PagingFavoritesConceptItem>

    @Query("SELECT * FROM PagingFavoritesConceptItem WHERE `item_info_id` = :id")
    fun getById(id: Int): PagingFavoritesConceptItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(concepts: List<PagingFavoritesConceptItem>)

    @Query("DELETE FROM PagingFavoritesConceptItem")
    suspend fun deleteAll()

    @Query("DELETE FROM PagingFavoritesConceptItem WHERE `item_info_id` IN (:idList)")
    suspend fun deleteList(idList: List<Int>)
}