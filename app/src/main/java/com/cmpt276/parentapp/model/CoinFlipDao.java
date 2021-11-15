package com.cmpt276.parentapp.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.SkipQueryVerification;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

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

    @Query("SELECT * FROM CoinFlip")
    Single<List<CoinFlip>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(CoinFlip... coinFlips);

    @Delete
    Completable delete(CoinFlip... coinFlips);

    @Update
    Completable update(CoinFlip coinFlip);
}
