package com.bnvlab.concienciadeabundancia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.bnvlab.concienciadeabundancia.auxiliaries.ICallback;
import com.bnvlab.concienciadeabundancia.auxiliaries.References;
import com.bnvlab.concienciadeabundancia.fragments.MainFragment;
import com.bnvlab.concienciadeabundancia.fragments.RateFragment;

import java.util.List;

/**
 * Created by Marcos on 23/03/2017.
 */

public class FragmentMan {

    public static Fragment actualFragment;
    private static FragmentManager fm;

    public static void changeFragment(FragmentActivity activity, Class fragmentClass, String tag, String secondTag) {
        changeFragment(activity, fragmentClass, false, tag, secondTag);
    }

    /**
     * This method allow us to change, or add if is the case, a fragment in the FragmentMannager. The fragments cant be repeated
     *
     * @param activity      The running activity
     * @param fragmentClass The class of the fragment to show, add.
     */
    public static void changeFragment(FragmentActivity activity, Class fragmentClass, String tag) {
        changeFragment(activity, fragmentClass, false, tag, null);
    }

    public static void changeFragment(FragmentActivity activity, Class fragmentClass) {
        changeFragment(activity, fragmentClass, false, null, null);
    }

    public static void changeFragment(FragmentActivity activity, Class fragmentClass, boolean clear) {
        changeFragment(activity, fragmentClass, clear, null, null);
    }

    /**
     * This method allow us to change, or add if is the case, a fragment in the FragmentMannager. The fragments cant be repeated
     *
     * @param activity      The running activity
     * @param fragmentClass The class of the fragment to show, add.
     * @param clearAll      if true all the other fragments will be erased
     */
    public static void changeFragment(FragmentActivity activity, Class fragmentClass, boolean clearAll, String tag, String secondTag) {
        boolean exist = false;
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentMan.fm = fm;

        if (fragmentClass.equals(RateFragment.class)) {
            List<Fragment> list = fm.getFragments();
            if (list != null)
                for (Fragment f : list) {
                    if (fragmentClass.equals(f.getClass()))
                        exist = true;

                    Log.i("DEBUG - FRAGMENT", "Found fragment: " + f.getClass());
                }
        }
        if (Fragment.class.isAssignableFrom(fragmentClass) && !exist) {

            String className = fragmentClass.getSimpleName();
            Fragment fragment = null;

            try {
                fragment = (Fragment) fragmentClass.newInstance();
                if (tag != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("tag", tag);
                    if (secondTag != null)
                        bundle.putString("secondTag", secondTag);
                    fragment.setArguments(bundle);
                }
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

                    if (fragment instanceof ICallback) {
                        ((ICallback) fragment).callback();
                    }
                } else {
                    activity
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)  //VA ANTES DEL ADD
                            .add(R.id.fragment_main, fragment, className)
                            .commit();
                }

            }
        }
    }
    public static void changeFragment(FragmentActivity activity, Class newFragment, Class oldFragment) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment nf;

        Fragment f = fm.findFragmentByTag(oldFragment.toString());

        if(f != null) {
            ft.remove(f);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        }

        try {
            nf = (Fragment) newFragment.newInstance();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_main, nf, newFragment.toString())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        } catch (Exception e) {
            Log.d(References.ERROR_LOG, "FragmentMan - " + e.getMessage());
        }
    }

    public static void changeFragment(Class newFragment, FragmentActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment nf;

        try {
            nf = (Fragment) newFragment.newInstance();
            activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_main, nf, newFragment.toString())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        } catch (Exception e) {
            Log.d(References.ERROR_LOG, "FragmentMan - " + e.getMessage());
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

    public static void eraseAll(FragmentActivity activity) {
        // ERASE ALL FRAGMENTS
        FragmentManager fm = activity.getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
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

    public static FragmentManager getFragmentManager() {
        return fm;
    }

}
