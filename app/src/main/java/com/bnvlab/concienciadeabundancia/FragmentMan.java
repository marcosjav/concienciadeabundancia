package com.bnvlab.concienciadeabundancia;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.bnvlab.concienciadeabundancia.fragments.MainFragment;

/**
 * Created by Marcos on 23/03/2017.
 */

public class FragmentMan {

    public static Fragment actualFragment;
    /**
     * This method allow us to change, or add if is the case, a fragment in the FragmentMannager. The fragments cant be repeated
     *
     * @param activity      The running activity
     * @param fragmentClass The class of the fragment to show, add.
     */
    public static void changeFragment(FragmentActivity activity, Class fragmentClass) {
        changeFragment(activity, fragmentClass, false);
    }

    /**
     * This method allow us to change, or add if is the case, a fragment in the FragmentMannager. The fragments cant be repeated
     *
     * @param activity      The running activity
     * @param fragmentClass The class of the fragment to show, add.
     * @param clearAll      if true all the other fragments will be erased
     */
    public static void changeFragment(FragmentActivity activity, Class fragmentClass, boolean clearAll) {

        // ERASE ALL FRAGMENTS
       /* if (clearAll) {
            FragmentManager fm = activity.getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
        }*/

        FragmentManager fm = activity.getSupportFragmentManager();
        /*List<Fragment> fragmentsList = fm.getFragments();
        if (fragmentsList != null)
            for (Fragment f :
                    fragmentsList) {
                if (f != null && f.getView() != null)
                    f.getView().setVisibility(View.INVISIBLE);
            }*/

        if (Fragment.class.isAssignableFrom(fragmentClass)) {

            String className = fragmentClass.getSimpleName();
            Fragment fragment = null;

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                Log.d("CDA_EXCEPTION", "EXCEPTION: " + e.getMessage());
            }
            if (fragment != null) {
                    actualFragment = fragment;
                    if (className != MainFragment.class.getSimpleName()) {
                        activity
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)  //VA ANTES DEL ADD
                                .add(R.id.fragment_main, fragment, className)
                                .addToBackStack(className)
                                .commit();
                    }else
                    {
                        activity
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)  //VA ANTES DEL ADD
                                .add(R.id.fragment_main, fragment, className)
                                .commit();
                    }

            }

            /*if (fragment != null) {
                if (fm.findFragmentByTag(className) != null)
                {
                    actualFragment = fm.findFragmentByTag(className);
                    if (className != MainFragment.class.getSimpleName()) {
                        fm.beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                                .show(actualFragment)
                                .addToBackStack(className)
                                .commit();
                    }else{
                        fm.beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                                .show(actualFragment)
                                .commit();
                    }
                    View v = fm.findFragmentByTag(className).getView();
                    if (v != null)
                        v.setVisibility(View.VISIBLE);
                } else {
                    actualFragment = fragment;
                    if (className != MainFragment.class.getSimpleName()) {
                        activity
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)  //VA ANTES DEL ADD
                                .add(R.id.fragment_main, fragment, className)
                                .addToBackStack(className)
                                .commit();
                    }else
                    {
                        activity
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)  //VA ANTES DEL ADD
                                .add(R.id.fragment_main, fragment, className)
                                .commit();
                    }
                }
            }*/
        }
    }

    public static void removeFragment(FragmentActivity activity, Class fragmentClass) {
        if (Fragment.class.isAssignableFrom(fragmentClass)) {
            Fragment fragment = null;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                Log.d("CDA_EXCEPTION", "EXCEPTION: " + e.getMessage());
            }

            if (fragment != null)
                if (fragment.isAdded()) {
                    activity
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)  //VA ANTES DEL ADD
                            .remove(fragment)
                            .commit();
                }
        }
    }

    /*public static void changeRemoveFragment(FragmentActivity activity, Class showFragment, Class removeFragment) {
        if (Fragment.class.isAssignableFrom(showFragment)
                && Fragment.class.isAssignableFrom(removeFragment)) {

            String showClassName = showFragment.getSimpleName(),
                    removeClassName = removeFragment.getSimpleName();
            Fragment newFragment = null,
                    oldFragment = null;

            try {
                newFragment = (Fragment) showFragment.newInstance();
                oldFragment = (Fragment) removeFragment.newInstance();
            } catch (Exception e) {

            }

            if (oldFragment != null && oldFragment.isAdded())
                activity
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)  //VA ANTES DEL ADD
                        .remove(oldFragment)
                        .commit();

            if (newFragment != null && newFragment.isAdded())
                activity
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)  //VA ANTES DEL ADD
                        .add(R.id.fragment_main, newFragment, showClassName)
                        .addToBackStack("")
                        .commit();
        }
    }*/

}
