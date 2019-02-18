package ru.cityproblemsmap.di

import android.content.Intent
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.Toast


open class BaseFragment : Fragment() {

    // ============================================================
    // Lifecycle
    // ============================================================

    override fun onPause() {
        super.onPause()
        KeyboardHelper.hideSoftKeyboard(activity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val fragments = childFragmentManager.fragments
        for (fragment in fragments) {
            fragment?.onActivityResult(requestCode, resultCode, data)
        }
    }

    // ============================================================
    // Toast
    // ============================================================

    protected fun showSnackBar(
        message: String,
        actionText: String? = null,
        action: View.OnClickListener? = null,
        container: View? = null
    ) {
        (activity as BaseActivity?)?.showSnackBar(message, actionText, action, container)
    }

    protected fun showToastMessage(message: String?) {
        if (message == null)
            return

        if (activity == null)
            return

        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    protected fun showToastMessage(@StringRes stringResId: Int) {
        if (activity == null)
            return

        Toast.makeText(activity, stringResId, Toast.LENGTH_SHORT).show()
    }

    // ============================================================
    // Toolbar
    // ============================================================

    fun setupToolbar(
        inflatedView: View, titleId: Int?, homeIconId: Int?, backButtonEnabled: Boolean,
        toolbarNavigationButtonClickListener: View.OnClickListener?
    ) {
        setupToolbar(
            inflatedView,
            titleId?.let { resources.getString(it) },
            homeIconId,
            backButtonEnabled,
            toolbarNavigationButtonClickListener
        )
    }

    private fun setupToolbar(
        inflatedView: View, title: String?, homeIconId: Int?, backButtonEnabled: Boolean,
        toolbarNavigationButtonClickListener: View.OnClickListener?
    ) {
        val toolbar = inflatedView.findViewById<Toolbar>(R.id.toolbar) ?: return

        val activity = activity as? AppCompatActivity ?: return

        activity.setSupportActionBar(toolbar)
        activity.supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar.title = title ?: ""
        when {
            homeIconId != null ->
                toolbar.setNavigationIcon(homeIconId)
            backButtonEnabled ->
                activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            else -> {
                activity.supportActionBar!!.setDisplayShowHomeEnabled(false)
                toolbar.setContentInsetsRelative(
                    toolbar.contentInsetLeft + DimensHelper.dpToPx(context!!, 0.0f).toInt(),
                    toolbar.contentInsetRight
                )
            }
        }

        activity.setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener(toolbarNavigationButtonClickListener)
    }

    protected fun positionToolbarBelowStatusBar(inflatedView: View) {
        val toolbar = inflatedView.findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            toolbar.layoutParams = (toolbar.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                topMargin = DeviceUtils.getStatusBarHeight(resources)
            }
        }
    }
}