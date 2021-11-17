package com.cmpt276.parentapp.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

/**
 * This is a Database Access Object that handles interactions with the coinFlip table.
 */
@Dao
public interface CoinFlipDao {

    @Transaction
    @Query("SELECT " +
            "cf.uid as cf_uid, " +
            "cf.childId as cf_childId, " +
            "cf.isWinner as cf_isWinner, " +
            "cf.date as cf_date, " +
            "cf.choice as cf_choice, " +
            "c.* FROM CoinFlip cf " +
            "LEFT OUTER JOIN child c ON c.uid = cf.childId " +
            "ORDER BY cf.uid")
    Single<List<ChildCoinFlip>> GetAllChildCoinFlips();

    @Transaction
    @Query("SELECT " +
            "cf.uid as cf_uid, " +
            "cf.childId as cf_childId, " +
            "cf.isWinner as cf_isWinner, " +
            "cf.date as cf_date, " +
            "cf.choice as cf_choice, " +
            "c.* FROM CoinFlip cf " +
            "LEFT OUTER JOIN child c ON c.uid = cf.childId " +
            "ORDER BY cf.uid DESC LIMIT 1")
    Single<ChildCoinFlip> getLastFlip();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(CoinFlip... coinFlips);
}
