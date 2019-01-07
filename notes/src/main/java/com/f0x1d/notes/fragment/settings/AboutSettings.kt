package com.f0x1d.notes.fragment.settings

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.f0x1d.notes.R
import com.f0x1d.notes.utils.ThemesEngine
import com.f0x1d.notes.utils.UselessUtils
import com.f0x1d.notes.view.CenteredToolbar

class AboutSettings : PreferenceFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater?.inflate(R.layout.settings, container, false)

        val toolbar: CenteredToolbar = v?.findViewById<View>(R.id.toolbar) as CenteredToolbar
            toolbar.setTitle(getString(R.string.about))
        activity.setActionBar(toolbar)

        if (UselessUtils.ifCustomTheme()){
            activity.window.setBackgroundDrawable(ColorDrawable(ThemesEngine.background))
            activity.window.statusBarColor = ThemesEngine.statusBarColor
            activity.window.navigationBarColor = ThemesEngine.navBarColor

            if (ThemesEngine.toolbarTransparent) {
                toolbar.setBackgroundColor(ThemesEngine.toolbarColor)
            }
        }

        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.about)
    }

    override fun onResume() {
        super.onResume()
        if(view != null){
            val list = view.findViewById<ListView>(android.R.id.list)
            list.setPadding(0, 0, 0, 0)
            list.divider = null
        }
    }
}