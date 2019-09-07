package com.f0x1d.notes.utils.theme;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class ThemingViewModel extends ViewModel {

    public final MutableLiveData<Integer> background = new MutableLiveData<>();
    public final MutableLiveData<Integer> statusBarColor = new MutableLiveData<>();
    public final MutableLiveData<Integer> navBarColor = new MutableLiveData<>();
    public final MutableLiveData<Integer> textColor = new MutableLiveData<>();
    public final MutableLiveData<Integer> accentColor = new MutableLiveData<>();
    public final MutableLiveData<Integer> iconsColor = new MutableLiveData<>();
    public final MutableLiveData<Integer> textHintColor = new MutableLiveData<>();
    public final MutableLiveData<Integer> toolbarColor = new MutableLiveData<>();
    public final MutableLiveData<Integer> toolbarTextColor = new MutableLiveData<>();
    public final MutableLiveData<Integer> fabColor = new MutableLiveData<>();
    public final MutableLiveData<Integer> fabIconColor = new MutableLiveData<>();
    public final MutableLiveData<Integer> defaultNoteColor = new MutableLiveData<>();
    public final MutableLiveData<Integer> lightColorTextColor = new MutableLiveData<>();
    public final MutableLiveData<Integer> darkColorTextColor = new MutableLiveData<>();
    public final MutableLiveData<Integer> lightColorIconColor = new MutableLiveData<>();
    public final MutableLiveData<Integer> darkColorIconColor = new MutableLiveData<>();
    public final MutableLiveData<Integer> seekBarColor = new MutableLiveData<>();
    public final MutableLiveData<Integer> seekBarThumbColor = new MutableLiveData<>();

    public ThemingViewModel() {
        super();
    }
}
