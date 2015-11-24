package com.app.jobaloon.utils;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.app.jobaloon.main.R;

/**
 * Purpose - Class to define a method to add/replace/pop back fragment.
 *
 * @author Team Android
 */
public class FragmentActions extends Fragment {

    /**
     * Replace fragment one fragment by other
     *
     * @param fragment
     * @param frameId
     * @param addToBackStack
     * @param transition
     * @param name
     */
    FragmentActivity con;
    public void replaceFragment(Fragment fragment, int frameId,
                                boolean addToBackStack, int transition, String name) {
        FragmentTransaction ft = getFragmentTransaction();
//        ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.enter, R.anim.exit);
        ft.replace(frameId, fragment);
        ft.setTransition(transition);
        if (addToBackStack)
            ft.addToBackStack(name);
        ft.commit();
    }

    /**
     * Add fragment
     *
     * @param fragment
     * @param frameId
     * @param addToBackStack
     * @param transition
     * @param name
     */
    public void addFragment(Fragment fragment, int frameId,
                            boolean addToBackStack, int transition, String name) {
        FragmentTransaction ft = getFragmentTransaction();
//        FragmentTransaction ft = ((FragmentActivity) c)
//                .getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.enter, R.anim.exit);

        ft.add(frameId, fragment);
        ft.setTransition(transition);
        if (addToBackStack)
            ft.addToBackStack(name);
        ft.commit();
    }


    public void addFragmentFromUp(Fragment fragment, int frameId,
                                  boolean addToBackStack, int transition, String name) {
        FragmentTransaction ft = getFragmentTransaction();
        ft.setCustomAnimations(R.anim.down_from_top, R.anim.up_out_of_screen, R.anim.down_from_top, R.anim.up_out_of_screen);
        ft.add(frameId, fragment);
        ft.setTransition(transition);
        if (addToBackStack)
            ft.addToBackStack(name);
        ft.commit();
    }
    public void replaceFragmentFromUp(Fragment fragment, int frameId,
                                  boolean addToBackStack, int transition, String name) {
        FragmentTransaction ft = getFragmentTransaction();
        ft.setCustomAnimations(R.anim.down_from_top, R.anim.noanim, R.anim.noanim, R.anim.up_out_of_screen);
//        ft.setCustomAnimations(R.anim.down_from_top, R.anim.up_out_of_screen);
        ft.replace(frameId, fragment);
        ft.setTransition(transition);
        if (addToBackStack)

            ft.addToBackStack(name);
        ft.commit();
    }

    /**
     * remove fragement
     *
     * @param fragment
     */
    public void removeFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentTransaction();
        ft.remove(fragment);
        ft.commit();
    }

    /**
     * remove/popback fragments from backstack
     *
     * @param name
     */
    public void popBackFragment(String name) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }




    public void replaceFragmentNew(Fragment fragment, int frameId,
                            boolean addToBackStack, int transition, String name,FragmentActivity act) {
        FragmentTransaction transaction = act
                .getSupportFragmentManager().beginTransaction();
//        FragmentTransaction ft = ((FragmentActivity) c)
//                .getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.enter, R.anim.exit);
        transaction.replace(frameId, fragment);
        transaction.setTransition(transition);
        if (addToBackStack)
            transaction.addToBackStack(name);
        transaction.commit();
    }

    public void addFragmentNew(Fragment fragment, int frameId,
                               boolean addToBackStack, int transition, String name,FragmentActivity act) {
        FragmentTransaction transaction = act
                .getSupportFragmentManager().beginTransaction();
//        FragmentTransaction ft = ((FragmentActivity) c)
//                .getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.enter, R.anim.exit);
        transaction.add(frameId, fragment);
        transaction.setTransition(transition);
        if (addToBackStack)
            transaction.addToBackStack(name);
        transaction.commit();
    }
    /**
     * Get fragment transaction
     *
     * @return
     */
    private FragmentTransaction getFragmentTransaction() {
        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        return transaction;

    }
}
