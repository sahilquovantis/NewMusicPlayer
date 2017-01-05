package com.quovantis.music.helper;

/**
 * This Class helps in maintaining current Position of the queue.
 */
class CurrentPositionHelper {

    private int mCurrentPosition;
    private int mPreviousPosition;
    private boolean mShouldBeInPauseState;

    CurrentPositionHelper() {
        mCurrentPosition = 0;
        mPreviousPosition = -1;
        mShouldBeInPauseState = false;
    }

    int getCurrentPosition() {
        return mCurrentPosition;
    }

    int getPreviousPosition() {
        return mPreviousPosition;
    }

    void setCurrentPosition(int mCurrentPosition) {
        setPreviousPosition(this.mCurrentPosition);
        this.mCurrentPosition = mCurrentPosition;
    }

    private void setPreviousPosition(int mCurrentPosition) {
        this.mPreviousPosition = mCurrentPosition;
    }

    int getPreviousSong(int size) {
        setPreviousPosition(mCurrentPosition);
        if (mCurrentPosition == 0 || mCurrentPosition < 0)
            mCurrentPosition = size - 1;
        else
            mCurrentPosition -= 1;
        return mCurrentPosition;
    }

    int getNextSong(int size) {
        setPreviousPosition(mCurrentPosition);
       /* if (AppMusicKeys.REPEAT_STATE == AppMusicKeys.REPEAT_ON) {
            return mCurrentPosition;
        }
        if (AppMusicKeys.SHUFFLE_STATE == AppMusicKeys.SHUFFLE_ON && size > 0) {
            Random random = new Random();
            mCurrentPosition = random.nextInt(size);
            return mCurrentPosition;
        }*/
        if (mCurrentPosition == size - 1 || mCurrentPosition >= size) {
            mCurrentPosition = 0;
            mShouldBeInPauseState = true;
        } else {
            mShouldBeInPauseState = false;
            mCurrentPosition += 1;
        }
        return mCurrentPosition;
    }

    boolean shouldBeInPauseState(){
        return mShouldBeInPauseState;
    }
}