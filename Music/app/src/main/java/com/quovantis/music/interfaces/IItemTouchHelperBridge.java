package com.quovantis.music.interfaces;

/**
 * Used For Swipe to Remove in RecyclerView Adapter
 */
public interface IItemTouchHelperBridge {
    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

    void onItemMoveCompleted();
}
