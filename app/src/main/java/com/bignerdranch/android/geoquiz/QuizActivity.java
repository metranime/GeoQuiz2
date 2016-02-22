package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {
    //Create constant tags
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String IS_CHEATER = "IsCheater";
    private static final int REQUEST_CODE_CHEAT = 0;
    //private variables
    private int mCurrentIndex = 0;
    private boolean mIsCheater;
    //Create Buttons
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private TextView mQuestionTextView;
    /* Set up question bank
       Form: Question(string.questionObject, boolean answer),
     */
    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    //Increment Question index
    private void incrementQuestion(){
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
    }

    //Decrement Question index
    private void decrementQuestion(){
        if(mCurrentIndex <= 0){
            mCurrentIndex = mQuestionBank.length;
        }
        mCurrentIndex = (mCurrentIndex - 1);
    }
    //Update Question
    private void updateQuestion() {
       // Log.d(TAG, "updating question text for question #" + mCurrentIndex, new Exception());
        //Get index of question by ID
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        //Set the question text view in xml
        mQuestionTextView.setText(question);
    }

    //Check Answer
    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        //If they cheated, show judgement_toast
        if(mIsCheater | mCheaterArray[mCurrentIndex]){
            messageResId = R.string.judgement_toast;
        } else{
            //Checks if user got correct answer
            if(userPressedTrue == answerIsTrue){
                messageResId = R.string.correct_toast;
            } else{
                messageResId = R.string.incorrect_toast;
            }
        }
        //Displays message on screen
        Toast.makeText(this, messageResId,Toast.LENGTH_SHORT).show();
    }

    //Array for holding mIsCheater values
    private boolean[] mCheaterArray = new boolean[mQuestionBank.length];
    private final String[] mCheaterKeyArray = new String[mQuestionBank.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        for(int i = 0; i < mQuestionBank.length; i++){
            //Init with false i.e. didn't cheat
            mCheaterKeyArray[i] = ("QUESTION_" + i);
        }
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);

        updateQuestion();

        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementQuestion();
                updateQuestion();
            }
        });
        //Initialize Button by Id(loaded from quiz activity)
        mTrueButton = (Button) findViewById(R.id.true_button);
        //Set listener for actions to take when button is clicked
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Functionality replaced with checkAnswer() function
                Toast.makeText(QuizActivity.this,
                        R.string.incorrect_toast,
                        Toast.LENGTH_SHORT).show();
                */
                checkAnswer(true);
                updateQuestion();
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Toast.makeText(QuizActivity.this,
                        R.string.incorrect_toast,
                        Toast.LENGTH_SHORT).show();
                */
                checkAnswer(false);
                updateQuestion();
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementQuestion();
                updateQuestion();
            }
        });

        //Creates previous button
        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementQuestion();
                updateQuestion();
            }
        });

        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start CheatActivity
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent i = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(i, REQUEST_CODE_CHEAT);
            }
        });

        if(savedInstanceState != null) {
            Log.d(TAG, "Init savedInstanceState");
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(IS_CHEATER, false);
            for(int i = 0; i < mCheaterArray.length; i++){
                mCheaterArray[i] = savedInstanceState.getBoolean(mCheaterKeyArray[i], false);
                if(mCheaterArray[i] == true) Log.d(TAG, "mCheaterArray[" + i + "] is true");
                else Log.d(TAG, "mCheaterArray[" + i + "] is false");
            }
        }

        //Change the question
        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null){
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
            mCheaterArray[mCurrentIndex] = mIsCheater;
        }
    }
    @Override
    public void onStart() {
        super.onStart(); //Always call super at the beginning of an overridden method
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState()");
        for(int i = 0; i < mQuestionBank.length; i++){
            savedInstanceState.putBoolean(mCheaterKeyArray[i], mCheaterArray[i]);
        }
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(IS_CHEATER, mIsCheater);
    }
}
