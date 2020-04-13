package cn.wenet.networkcomponent.life;


import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import cn.wenet.networkcomponent.debug.WeDebug;

/**
 * Created to :
 *
 * @author cc.wang
 * @date 2020/3/27
 */
public class WeNetLifeCircleManager {

    private static final String FRAGMENT_TAG = "com.wenet.manager";

    public PageLifeManager bindFragment(Fragment fragment) {
        if (fragment == null) {
            throw new IllegalArgumentException("You cannot start a load on a null Fragment");
        } else {
            FragmentManager childFragmentManager = fragment.getChildFragmentManager();
            return supportFragmentGet(childFragmentManager);
        }
    }

    public PageLifeManager bindContext(Context context) {
        if (null == context) {
            throw new IllegalArgumentException("You cannot start a load on a null Context");
        } else if (context instanceof FragmentActivity) {
            return bindActivity((FragmentActivity) context);
        } else {
            WeDebug.e("bindContext(Context)方法中的Context不是Activity类型！", context.toString());
        }
        return null;
    }

    public PageLifeManager bindDialog(Dialog dialog) {
        if (dialog == null) {
            throw new IllegalArgumentException("You cannot start a load on a null Dialog");
        } else {
            return managerViewGet(dialog);
        }
    }

    private PageLifeManager managerViewGet(Dialog dialog) {
        RequestManagerView managerView = null;
        Window window = dialog.getWindow();
        if (null != window) {
            View decorView = window.getDecorView();
            View view = decorView.findViewById(android.R.id.content);
            if (view instanceof FrameLayout) {
                FrameLayout frameLayout = (FrameLayout) view;
                frameLayout.addView(managerView = new RequestManagerView(frameLayout.getContext()));
            }
        }
        return null == managerView ? null : managerView.getPageLifeManager();
    }


    private PageLifeManager bindActivity(FragmentActivity fragmentActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && fragmentActivity.isDestroyed()) {
            throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
        }
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        return supportFragmentGet(supportFragmentManager);
    }

    private PageLifeManager supportFragmentGet(FragmentManager fragmentManager) {
        RequestManagerFragment tag = (RequestManagerFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (null == tag) {
            tag = new RequestManagerFragment();
            fragmentManager.beginTransaction().add(tag, FRAGMENT_TAG).commitAllowingStateLoss();
        }
        return tag.getPageLifeManager();
    }

}
