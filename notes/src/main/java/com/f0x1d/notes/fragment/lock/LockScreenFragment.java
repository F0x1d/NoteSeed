package com.f0x1d.notes.fragment.lock;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.fragment.editing.NoteEditFragment;
import com.f0x1d.notes.fragment.main.NotesFragment;
import com.f0x1d.notes.utils.Logger;
import com.f0x1d.notes.utils.UselessUtils;
import com.f0x1d.notes.utils.theme.ThemesEngine;
import com.f0x1d.notes.view.theming.MyButton;
import com.mattprecious.swirl.SwirlView;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.view.View.GONE;

public class LockScreenFragment extends Fragment {

    private static final String KEY_NAME = "notes";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    private SwirlView swirlView;

    private boolean toNote;
    private long noteId;

    public static LockScreenFragment newInstance(boolean toNote, long id) {
        Bundle args = new Bundle();
        args.putBoolean("toNote", toNote);
        args.putLong("id", id);

        LockScreenFragment myFragment = new LockScreenFragment();
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        toNote = getArguments().getBoolean("toNote");
        noteId = getArguments().getLong("id");

        View view = inflater.inflate(R.layout.screen_lock, container, false);
        if (UselessUtils.isCustomTheme()) {
            requireActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ThemesEngine.background));
            requireActivity().getWindow().setStatusBarColor(ThemesEngine.statusBarColor);
            requireActivity().getWindow().setNavigationBarColor(ThemesEngine.navBarColor);
        }

        ((TextView) view.findViewById(R.id.textView5)).setText(getString(R.string.pass));

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

        Animation animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.push_up);
        animation.setDuration(400);
        back.startAnimation(animation);

        Animation animation2 = AnimationUtils.loadAnimation(requireActivity(), R.anim.push_down);
        animation2.setDuration(400);

        ImageView icon = view.findViewById(R.id.icon);
        icon.startAnimation(animation2);

        swirlView.setState(SwirlView.State.ON, true);

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (pass.getText().toString().equals(App.getDefaultSharedPreferences().getString("pass", ""))) {
                    swirlView.setState(SwirlView.State.OFF, true);
                    if (toNote)
                        UselessUtils.replace((AppCompatActivity) requireActivity(),
                                NoteEditFragment.newInstance(false, noteId, null), "edit", true, "editor");
                    else
                        UselessUtils.replace((AppCompatActivity) requireActivity(), NotesFragment.newInstance("def"), "notes", false, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (App.getDefaultSharedPreferences().getBoolean("finger", false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                keyguardManager = (KeyguardManager) requireActivity().getSystemService(KEYGUARD_SERVICE);
                fingerprintManager = (FingerprintManager) requireActivity().getSystemService(FINGERPRINT_SERVICE);

                if (keyguardManager.isKeyguardSecure()) {
                    generateKey();

                    if (initCipher()) {
                        cryptoObject = new FingerprintManager.CryptoObject(cipher);

                        Helper helper = new Helper(requireActivity());
                        helper.startAuth(fingerprintManager, cryptoObject);
                    }
                }
            }
        } else {
            swirlView.setVisibility(GONE);
        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateKey() {
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

        } catch (Exception exc) {
            Logger.log(exc);
            Toast.makeText(requireActivity(), "Fingerprint initialisation error!", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean initCipher() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (Exception e) {
            Logger.log(e);
            return false;
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (Exception e) {
            Logger.log(e);
            return false;
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
            swirlView.setState(SwirlView.State.ERROR, true);
            Toast.makeText(context, getString(R.string.fingerprint_error), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            swirlView.setState(SwirlView.State.OFF, true);
            if (toNote)
                UselessUtils.replace((AppCompatActivity) requireActivity(),
                        NoteEditFragment.newInstance(false, noteId, null), "edit", true, "editor");
            else
                UselessUtils.replace((AppCompatActivity) requireActivity(), NotesFragment.newInstance("def"), "notes", false, null);
        }
    }
}
