package com.f0x1d.notes.fragment.lock;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.f0x1d.notes.R;
import com.f0x1d.notes.fragment.main.Notes;
import com.f0x1d.notes.utils.ThemesEngine;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.view.theming.MyButton;
import com.mattprecious.swirl.SwirlView;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.view.View.GONE;

public class LockTickerScreen extends Fragment {

    private static final String KEY_NAME = "notes";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private Callback callback;

    private SwirlView swirlView;

    public static LockScreen newInstance(Callback callback) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("callback", callback);

        LockScreen myFragment = new LockScreen();
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if (UselessUtils.ifCustomTheme()) {
            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            getActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            getActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);
        }

        return inflater.inflate(R.layout.screen_lock, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        callback = (Callback) getArguments().get("callback");

        MyButton odin = view.findViewById(R.id.odin);
        MyButton dva = view.findViewById(R.id.dva);
        MyButton tri = view.findViewById(R.id.tri);

        MyButton cheture = view.findViewById(R.id.cheture);
        MyButton pat = view.findViewById(R.id.pat);
        MyButton shest = view.findViewById(R.id.shest);

        MyButton sem = view.findViewById(R.id.sem);
        MyButton vosem = view.findViewById(R.id.vosem);
        MyButton devat = view.findViewById(R.id.devat);

        MyButton nol = view.findViewById(R.id.nol);

        MyButton back = view.findViewById(R.id.back);

        swirlView = view.findViewById(R.id.swirlView);

        final EditText pass = view.findViewById(R.id.pass);
        pass.setRawInputType(0x00000000);

        View.OnClickListener oclBtn = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.odin:
                        pass.setText(pass.getText().toString() + "1");
                        break;
                    case R.id.dva:
                        pass.setText(pass.getText().toString() + "2");
                        break;
                    case R.id.tri:
                        pass.setText(pass.getText().toString() + "3");
                        break;
                    case R.id.cheture:
                        pass.setText(pass.getText().toString() + "4");
                        break;
                    case R.id.pat:
                        pass.setText(pass.getText().toString() + "5");
                        break;
                    case R.id.shest:
                        pass.setText(pass.getText().toString() + "6");
                        break;
                    case R.id.sem:
                        pass.setText(pass.getText().toString() + "7");
                        break;
                    case R.id.vosem:
                        pass.setText(pass.getText().toString() + "8");
                        break;
                    case R.id.devat:
                        pass.setText(pass.getText().toString() + "9");
                        break;
                    case R.id.nol:
                        pass.setText(pass.getText().toString() + "0");
                        break;
                    case R.id.back:
                        if (!pass.getText().toString().isEmpty()) {
                            pass.setText("");
                        }
                        break;
                }
            }
        };

        odin.setOnClickListener(oclBtn);
        dva.setOnClickListener(oclBtn);
        tri.setOnClickListener(oclBtn);

        cheture.setOnClickListener(oclBtn);
        pat.setOnClickListener(oclBtn);
        shest.setOnClickListener(oclBtn);

        sem.setOnClickListener(oclBtn);
        vosem.setOnClickListener(oclBtn);
        devat.setOnClickListener(oclBtn);

        nol.setOnClickListener(oclBtn);

        back.setOnClickListener(oclBtn);

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up);
        animation.setDuration(400);
        back.startAnimation(animation);

        Animation animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.push_down);
        animation2.setDuration(400);

        ImageView icon = view.findViewById(R.id.icon);
        icon.startAnimation(animation2);

        swirlView.setState(SwirlView.State.ON, true);

        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("night", true)) {
            odin.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            dva.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            tri.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            cheture.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            pat.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            shest.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            sem.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            vosem.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            devat.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            nol.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
            back.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.statusbar)));
        }

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (pass.getText().toString().equals(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("pass", ""))) {
                    swirlView.setState(SwirlView.State.OFF, true);
                    callback.onSuccess(LockTickerScreen.this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("finger", false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                keyguardManager =
                        (KeyguardManager) getActivity().getSystemService(KEYGUARD_SERVICE);
                fingerprintManager =
                        (FingerprintManager) getActivity().getSystemService(FINGERPRINT_SERVICE);

                if (!keyguardManager.isKeyguardSecure()) {
                } else {
                    try {
                        generateKey();
                    } catch (FingerprintException e) {
                        e.printStackTrace();
                    }

                    if (initCipher()) {
                        cryptoObject = new FingerprintManager.CryptoObject(cipher);

                        Helper helper = new Helper(getActivity());
                        helper.startAuth(fingerprintManager, cryptoObject);
                    }
                }
            }
        } else {
            swirlView.setVisibility(GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            try {
                throw new FingerprintException(exc);
            } catch (FingerprintException e) {
                e.printStackTrace();
            }
        }
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean initCipher() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    class Helper extends FingerprintManager.AuthenticationCallback {

        private CancellationSignal cancellationSignal;
        private Context context;

        public Helper(Context mContext) {
            context = mContext;
        }

        public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

            cancellationSignal = new CancellationSignal();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        }

        @Override
        public void onAuthenticationFailed() {
            try {
                swirlView.setState(SwirlView.State.ERROR, true);
                Toast.makeText(context, getString(R.string.fingerprint_error), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
            }
        }

        @Override
        public void onAuthenticationSucceeded(
                FingerprintManager.AuthenticationResult result) {

            try {
                swirlView.setState(SwirlView.State.OFF, true);
                callback.onSuccess(LockTickerScreen.this);
            } catch (Exception e) {
            }
        }
    }

    public interface Callback extends Parcelable {
        void onSuccess(LockTickerScreen screen);
    }
}
