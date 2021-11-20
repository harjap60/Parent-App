package com.cmpt276.parentapp.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.rxjava3.EmptyResultSetException;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.databinding.ActivityFlipBinding;
import com.cmpt276.parentapp.model.Child;
import com.cmpt276.parentapp.model.ChildCoinFlip;
import com.cmpt276.parentapp.model.ChildDao;
import com.cmpt276.parentapp.model.CoinFlip;
import com.cmpt276.parentapp.model.CoinFlipDao;
import com.cmpt276.parentapp.model.ParentAppDatabase;

import java.time.LocalDateTime;
import java.util.Random;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Flip Activity provides the UI for the Coin Flip.
 */
public class FlipActivity extends AppCompatActivity {

    private static final float SET_BUTTON_TO_ENABLE = 1f;
    private ActivityFlipBinding binding;

    private CoinFlip.Choice userChoice;

    private Child currentChild;
    private AnimatorSet animatorSet;

    private CoinFlipDao coinFlipDao;
    private ChildDao childDao;

    public static Intent getIntent(Context context) {
        return new Intent(context, FlipActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFlipBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        coinFlipDao = ParentAppDatabase
                .getInstance(this)
                .coinFlipDao();

        childDao = ParentAppDatabase
                .getInstance(this)
                .childDao();

        setupToolbar();
        setupPreviousChild();
        setupCurrentChild();
        setupHistoryButton();
        setupChoiceButtons();
        setupCoinFlipButton();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (animatorSet != null && animatorSet.isRunning()) {
            animatorSet.cancel();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.flip_activity_toolbar_label);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupHistoryButton() {
        binding.flipHistoryButton.setOnClickListener(view ->
                startActivity(FlipHistoryActivity.getIntent(this)));
    }

    private void setupChoiceButtons() {
        binding.userChoiceHeadsButton.setOnClickListener(view -> {
            userChoice = CoinFlip.Choice.HEADS;
            enableFlipCoinButton();
        });

        binding.userChoiceTailsButton.setOnClickListener(view -> {
            userChoice = CoinFlip.Choice.TAILS;
            enableFlipCoinButton();
        });
    }

    private void setupCoinFlipButton() {
        binding.flipCoinImageButton.setOnClickListener(view -> {
            binding.headsTailsTextAfterFlip.setVisibility(View.INVISIBLE);

            setButtonState(binding.userChoiceHeadsButton, false);
            setButtonState(binding.userChoiceTailsButton, false);
            setButtonState(binding.flipCoinImageButton, false);
            setButtonState(binding.flipHistoryButton, false);

            flipCoin();
        });
    }

    private void enableFlipCoinButton() {
        binding.headsTailsTextAfterFlip.setVisibility(View.INVISIBLE);
        setButtonState(binding.flipCoinImageButton, true);
        setButtonState(binding.userChoiceHeadsButton, false);
        setButtonState(binding.userChoiceTailsButton, false);
    }

    private void setupButtonWithChild() {
        setButtonState(binding.userChoiceHeadsButton, true);
        setButtonState(binding.userChoiceTailsButton, true);
        setButtonState(binding.flipHistoryButton, true);
        setButtonState(binding.flipCoinImageButton, false);
    }

    private void setupButtonsNoChild() {
        setButtonState(binding.userChoiceHeadsButton, false);
        setButtonState(binding.userChoiceTailsButton, false);
        setButtonState(binding.flipCoinImageButton, true);
        setButtonState(binding.flipHistoryButton, true);
    }

    private void setButtonState(View btn, boolean isEnabled) {
        btn.setEnabled(isEnabled);
        float SET_BUTTON_TO_DISABLE = .5f;
        btn.setAlpha(isEnabled ? SET_BUTTON_TO_ENABLE : SET_BUTTON_TO_DISABLE);
    }

    private void flipCoin() {
        playCoinFlipSound();

        //Source: https://stackoverflow.com/questions/46111262/card-flip-animation-in-android

        animatorSet = new AnimatorSet();

        final ObjectAnimator firstAnimation = ObjectAnimator.ofFloat(binding.coinImageView, "scaleY", 1f, 0f);
        final ObjectAnimator secondAnimation = ObjectAnimator.ofFloat(binding.coinImageView, "scaleY", 0f, 1f);
        secondAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        int ANIMATION_DURATION = 50;
        firstAnimation.setDuration(ANIMATION_DURATION);
        int ANIMATION_REPEAT_COUNT = 100;
        firstAnimation.setRepeatCount(ANIMATION_REPEAT_COUNT);
        firstAnimation.setInterpolator(new DecelerateInterpolator());
        firstAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                CoinFlip.Choice winningChoice = determineSide();

                binding.coinImageView.setImageResource(
                        winningChoice == CoinFlip.Choice.HEADS ?
                                R.drawable.heads :
                                R.drawable.tails
                );

                binding.headsTailsTextAfterFlip.setText(winningChoice.toString());
                binding.headsTailsTextAfterFlip.setVisibility(View.VISIBLE);

                saveFlip(winningChoice);
            }
        });

        animatorSet.playSequentially(firstAnimation, secondAnimation);
        animatorSet.start();
    }

    private void saveFlip(CoinFlip.Choice choice) {
        if (currentChild != null) {
            Thread thread = new Thread(() -> {

                CoinFlip flip = new CoinFlip(
                        currentChild.getUid(),
                        userChoice,
                        userChoice == choice,
                        LocalDateTime.now()
                );

                coinFlipDao.insertAll(flip).blockingAwait();

                int currentOrder = currentChild.getCoinFlipOrder();

                //Pushing child to end of the list
                int order = childDao.getNextCoinFlipOrder().blockingGet();
                currentChild.setCoinFlipOrder(order);
                childDao.update(currentChild).blockingAwait();

                //Moving everyone behind this child up by one.
                childDao.decrementCoinFlipOrder(currentOrder).blockingAwait();

                setupCurrentChild();
                setupPreviousChild();
            });
            thread.start();
        } else {
            setupCurrentChild();
            setupPreviousChild();
        }
    }

    private void playCoinFlipSound() {
        MediaPlayer coinFlip = MediaPlayer.create(FlipActivity.this, R.raw.coinflip);
        coinFlip.start();
    }

    private void setupCurrentChild() {

        childDao.getChildForNextFlip()
                .subscribeOn(Schedulers.newThread())
                .subscribe(new SingleObserver<Child>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Child child) {
                        currentChild = child;

                        binding.currentChildTv.setText(getString(
                                R.string.current_child_tv_string,
                                child.getName()));

                        runOnUiThread(() -> setupButtonWithChild());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        binding.currentChildTv.setText(getString(
                                R.string.current_child_tv_string,
                                getString(R.string.no_child_string)));

                        runOnUiThread(() -> setupButtonsNoChild());
                    }
                });
    }

    private void setupPreviousChild() {
        coinFlipDao.getLastFlip()
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<ChildCoinFlip>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onSuccess(@NonNull ChildCoinFlip flip) {
                        binding.previousChildTv.setText(getString(
                                R.string.previous_child_tv_string,
                                flip.getChild().getName()
                        ));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (e.getClass() == EmptyResultSetException.class) {
                            binding.previousChildTv.setText(getString(
                                    R.string.previous_child_tv_string,
                                    getString(R.string.no_child_string)));
                        }
                    }
                });
    }


    //Determine the side of the coin which will be shown
    private CoinFlip.Choice determineSide() {
        Random random = new Random();

        CoinFlip.Choice[] choices = CoinFlip.Choice.values();
        int randomNum = random.nextInt(choices.length);

        return choices[randomNum];
    }
}