package com.bnvlab.concienciadeabundancia.auxiliaries;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bnvlab.concienciadeabundancia.LoginActivity;
import com.bnvlab.concienciadeabundancia.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Marcos on 19/04/2017.
 */

public class Utils {
    private static Typeface typeface;

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static void shareDialog(final Context context) {
        final String invitationCode = FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                .child(References.INVITATION_CODES)
                .push()
                .getKey();

        FirebaseDatabase.getInstance().getReference(References.REFERENCE)
                .child(References.INVITATION_CODES)
                .child(invitationCode)
                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            String message = "Quiero compartir una hermosa experiencia con vos, que va a cambiar tu vida!\nPrimero instalá la app:\n\n" +
                                    "https://play.google.com/store/apps/details?id=com.bnvlab.concienciadeabundancia\n\nDespués abrí este link con la App para registrarte\n" +
                                    "http://concienciadeabundancia.com/code=" + invitationCode;

                            Notify.share(message, context);
                        } else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public static void  showLoginDelay(Activity activity){
        Thread timer = new Thread() {
            public void run(){
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        timer.start();
        showLogin(activity);
    }

    public static void  delayedAction(Activity activity, final long delay_ms, ICallback callback){
        Thread timer = new Thread() {
            public void run(){
                try {
                    sleep(delay_ms);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        timer.start();
        try {
            timer.join();
            callback.callback();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void showLogin(Activity activity) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent myIntent = new Intent(activity, LoginActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(myIntent);
            activity.finish();
        }
    }

    public static void setTypeface(TextView textView, Context context) {
        float size = textView.getTextSize();
        int style = textView.getTypeface().getStyle();
        textView.setTypeface(Utils.getTypeface(context), style);
        textView.setTextSize(size);
    }

    public static Typeface getTypeface(Context context) {
        if (typeface == null) {
            AssetManager am = context.getAssets();
            typeface = Typeface.createFromAsset(am, String.format(Locale.US, "fonts/%s", "coffee.ttf"));
        }
        return typeface;
    }

    public static void setTypeface(Context context, View view) {
        ArrayList<TextView> list = getTextViewList(view);

        for (TextView textView : list)
            setTypeface(textView, context);
    }

    public static ArrayList<TextView> getTextViewList(View view) {
        ArrayList<TextView> list = new ArrayList<>();

        if (view instanceof ViewGroup) {
            ViewGroup layout = (ViewGroup) view;
            for (int i = 0; i < layout.getChildCount(); i++) {
                View v = layout.getChildAt(i);
                if (v instanceof LinearLayout)
                    list.addAll(getTextViewList(v));
                else if (v instanceof FrameLayout)
                    list.addAll(getTextViewList(v));
                else if (v instanceof TextView)
                    list.add((TextView) v);
            }
        } else if (view instanceof TextView)
            list.add((TextView) view);

        return list;
    }

    public static void setSwitchListDisable(View view) {
        ArrayList<TextView> list = new ArrayList<>();

        if (view instanceof ViewGroup) {
            ViewGroup layout = (ViewGroup) view;
            for (int i = 0; i < layout.getChildCount(); i++) {
                View v = layout.getChildAt(i);
                if (v instanceof LinearLayout)
                    setSwitchListDisable(v);
                else if (v instanceof FrameLayout)
                    setSwitchListDisable(v);
                else if (v instanceof Switch)
                    v.setClickable(false);
            }
        } else if (view instanceof Switch)
            view.setClickable(false);

    }

    /**
     * Set the height of a listview based on his childs.
     * Usefull when the listview its inside a scrollview
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);

            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ViewPager.LayoutParams.MATCH_PARENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight()) * (listAdapter.getCount())) * 2;

        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    public static SharedPreferences getPrefs(Activity activity){
        return activity.getSharedPreferences(MainActivity.APP_SHARED_PREF_KEY + FirebaseAuth.getInstance().getCurrentUser().getUid(),
                MODE_PRIVATE);
    }

    public static void getServerTime(final ITimeCallback iCallback){
        final DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(References.APP_REFERENCE)
                .child(References.TIME_CHILD);

        ref.setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            iCallback.callback(dataSnapshot.getValue(Long.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            }
        });
    }
}
