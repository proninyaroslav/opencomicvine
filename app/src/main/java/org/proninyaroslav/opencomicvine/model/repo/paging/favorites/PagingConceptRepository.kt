package org.proninyaroslav.opencomicvine.model.repo.paging.favorites

import androidx.paging.PagingSource
import androidx.room.withTransaction
import org.proninyaroslav.opencomicvine.data.paging.favorites.FavoritesConceptItemRemoteKeys
import org.proninyaroslav.opencomicvine.data.paging.favorites.PagingFavoritesConceptItem
import org.proninyaroslav.opencomicvine.model.db.AppDatabase
import org.proninyaroslav.opencomicvine.model.repo.paging.ComicVinePagingRepository
import java.io.IOException
import javax.inject.Inject

interface PagingConceptRepository :
    FavoritesPagingRepository<PagingFavoritesConceptItem, FavoritesConceptItemRemoteKeys>

class PagingConceptRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
) : PagingConceptRepository {

    private val conceptsDao = appDatabase.favoritesConceptsDao()
    private val conceptsRemoteKeysDao = appDatabase.favoritesConceptsRemoteKeysDao()

    override suspend fun getRemoteKeysById(id: Int): ComicVinePagingRepository.Result<FavoritesConceptItemRemoteKeys?> {
        return try {
            ComicVinePagingRepository.Result.Success(conceptsRemoteKeysDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun getItemById(id: Int): ComicVinePagingRepository.Result<PagingFavoritesConceptItem?> {
        return try {
            ComicVinePagingRepository.Result.Success(conceptsDao.getById(id))
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override fun getAllSavedItems(): PagingSource<Int, PagingFavoritesConceptItem> =
        conceptsDao.getAll()

    override fun getSavedItems(count: Int): PagingSource<Int, PagingFavoritesConceptItem> =
        conceptsDao.get(count)

    override suspend fun deleteByIdList(idList: List<Int>): ComicVinePagingRepository.Result<Unit> {
        return try {
            conceptsDao.deleteList(idList)
            ComicVinePagingRepository.Result.Success(Unit)
        } catch (e: IOException) {
            ComicVinePagingRepository.Result.Failed.IO(e)
        }
    }

    override suspend fun saveItems(
        items: List<PagingFavoritesConceptItem>,
        remoteKeys: List<FavoritesConceptItemRemoteKeys>,
        clearBeforeSave: Boolean,
    ): ComicVinePagingRepository.Result<Unit> = try {
        appDatabase.withTransaction {
            if (clearBeforeSave) {
                conceptsDao.deleteAll()
                conceptsRemoteKeysDao.deleteAll()
            }
            conceptsRemoteKeysDao.insertList(remoteKeys)
            conceptsDao.insertList(items)
        }
        ComicVinePagingRepository.Result.Success(Unit)
    } catch (e: IOException) {
        ComicVinePagingRepository.Result.Failed.IO(e)
    }
}