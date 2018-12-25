package com.f0x1d.notes.fragment.settings;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.view.CenteredToolbar;

import androidx.annotation.Nullable;

public class AboutSettings extends PreferenceFragment implements BillingProcessor.IBillingHandler {

    BillingProcessor bp;

    @Override
    public void onResume() {
        super.onResume();
        View rootView = getView();
        if (rootView != null) {
            ListView list = (ListView) rootView.findViewById(android.R.id.list);
            list.setPadding(0, 0, 0, 0);
            list.setDivider(null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings, container, false);

        CenteredToolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.about));
        getActivity().setActionBar(toolbar);

        if (UselessUtils.ifCustomTheme()){
            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            getActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            getActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);

            if (ThemesEngine.toolbarTransparent){
                toolbar.setBackgroundColor(ThemesEngine.toolbarColor);
            }
        }
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.about);

        if (!BillingProcessor.isIabServiceAvailable(getActivity())) {
            Toast.makeText(getActivity(), "In-app billing service is unavailable.", Toast.LENGTH_LONG).show();
        }

        bp = new BillingProcessor(getActivity(),
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjn5fcM0N6icxSM16OhvkqB4HohYn4b2LjmBCKHXcHbxipnhR5knjIdaefaZpuD0ZmWdq9eOMBbfjA" +
                        "g0+mWhXbGvX53ZLAVtTTEz5F+Anzawh1lC9AJ0k8WUjnTH4MNKt4wYvhO0pDDqGg/XczrdUDL1oHaqofRvGlUD8wj6XU1ZIUm4yRt7OfF0xPuYbBUOk1a0Q2xpGMZVp6c0znLR+Fhxr94LwqvAZoF9" +
                        "97szaPFWzWB5wwFVScfYwf1WpIIRP1dd9ZrDmIAjbBUh+6XR6oEn0nsA92kwQPenLghZFL3kaB1izSATTFwdcLCXBV7T833GPEUxIIXQscw1rZXhNGQIDAQAB", this);

        bp.initialize();

        Preference donate = findPreference("donate");
            donate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    donate("donate");
                    return false;
                }
            });
    }

    @Override
    public void onPurchaseHistoryRestored() {
        // Так как информация о ранее приобретённых товарах в моём приложении не нужна, то данный метод оставляю пустым.
        // при необходимости, тут должен быть код для получения ранее купленных товаров и сохранения их в приложении на устройстве
    }

    @Override
    public void onBillingInitialized() {
        Log.d("LOG", "On Billing Initialaized"); // Просто пишем в лог
    }


    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Toast.makeText(getActivity(), "Thanks for Your donate.", Toast.LENGTH_LONG).show(); // Благодарим за пожертвование
        bp.consumePurchase(productId); // И сразу после успешного завершения покупки сразу реализуем приобретённый товар, чтобы его можно было купить снова.
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.d("LOG", "On Billing Error"+Integer.toString(errorCode)); // Пишем ошибку в лог
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(this, "On Activity Result", Toast.LENGTH_LONG).show();
        Log.d("LOG", "On Activity Result");
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Добавляю дополнительный метод, который буду вызывать для инициации процесса покупки
    public void donate(String ProductID) {
        bp.purchase(getActivity(), ProductID);  // Отправляем запрос на покупку товара
    }
}
