package org.proninyaroslav.opencomicvine.model.db.favorites

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesPersonItem

@Dao
interface FavoritesPeopleDao {
    @Query("SELECT * FROM PagingFavoritesPersonItem ORDER BY `index` ASC")
    fun getAll(): PagingSource<Int, PagingFavoritesPersonItem>

    @Query("SELECT * FROM PagingFavoritesPersonItem ORDER BY `index` ASC LIMIT :count")
    fun get(count: Int): PagingSource<Int, PagingFavoritesPersonItem>

    @Query("SELECT * FROM PagingFavoritesPersonItem WHERE `item_info_id` = :id")
    fun getById(id: Int): PagingFavoritesPersonItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(people: List<PagingFavoritesPersonItem>)

    @Query("DELETE FROM PagingFavoritesPersonItem")
    suspend fun deleteAll()

    @Query("DELETE FROM PagingFavoritesPersonItem WHERE `item_info_id` IN (:idList)")
    suspend fun deleteList(idList: List<Int>)
}