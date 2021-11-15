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
    @Query("SELECT * from ChildCoinFlip")
    Single<List<ChildCoinFlip>> GetAllChildCoinFlips();

    @Transaction
    @Query("SELECT * from ChildCoinFlip ORDER BY coinFlipUid DESC LIMIT 1")
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
