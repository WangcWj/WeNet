package cn.wenet.networkcomponent.life;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import java.util.HashMap;
import java.util.Map;
import cn.wenet.networkcomponent.base.NetBaseParam;
import cn.wenet.networkcomponent.debug.WeDebug;
import cn.wenet.networkcomponent.request.NetRequestImpl;

/**
 * Created to :
 *
 * @author cc.wang
 * @date 2020/3/27
 */
public class WeNetLifeCircleManager implements RequestLifeCircle {

    private static final String FRAGMENT_TAG = "com.wenet.manager";

    private Map<String, NetRequestImpl> mRequests = new HashMap<>();

    private Map<String, RequestManagerFragment> mLifeManager = new HashMap<>();

    private final Object LOCK = new Object();

    private PageLifeManager mApplication;

    public static String mergeHttp(String url) {
        return url.replace(NetBaseParam.HTTP, "").replace(NetBaseParam.HTTPS, "");
    }

    public void removeLifeCircleTag(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            mLifeManager.remove(tag);
        }
    }

    @Override
    public void addRequest(NetRequestImpl request) {
        if (null != request && null != request.getUrl()) {
            String key = mergeHttp(request.getUrl());
            mRequests.put(key, request);
            WeDebug.logD("addRequestParams : key = ", key, "   size = ", String.valueOf(mRequests.size()));
        }
    }

    @Override
    public NetRequestImpl getRequest(String url) {
        synchronized (LOCK) {
            if (TextUtils.isEmpty(url)) {
                return null;
            }
            String key = mergeHttp(url);
            return mRequests.get(key);
        }
    }

    @Override
    public Map<String, NetRequestImpl> getRequests() {
        return mRequests;
    }

    @Override
    public void removeRequest(String url) {
        if (!TextUtils.isEmpty(url)) {
            String key = mergeHttp(url);
            mRequests.remove(key);
            WeDebug.logD("removeRequest : key = ", key, "   size = ", String.valueOf(mRequests.size()));
        }
    }

    public PageLifeManager bindApplication() {
        if (null == mApplication) {
            mApplication = new PageLifeManager(this);
        }
        return mApplication;
    }

    public PageLifeManager bindFragment(Fragment fragment) {
        if (fragment == null || null == fragment.getActivity()) {
            throw new IllegalArgumentException("You cannot start a load on a null Fragment");
        } else {
            FragmentActivity activity = fragment.getActivity();
            if (activity.isDestroyed() || activity.isFinishing()) {
                WeDebug.logD("-----------bindFragment-is-destroy- ");
                return new PageLifeManager(null);
            }
            FragmentManager childFragmentManager = fragment.getChildFragmentManager();
            return supportFragmentGet(fragment.getContext(), childFragmentManager, fragment.getClass().getSimpleName());
        }
    }

    public PageLifeManager bindContext(Context context) {
        if (null == context) {
            throw new IllegalArgumentException("You cannot start a load on a null Context");
        } else if (context instanceof FragmentActivity) {
            return bindActivity((FragmentActivity) context);
        } else {
            WeDebug.e("bindContext(Context)方法中的Context不是Activity类型！", context.toString());
            return bindApplication();
        }
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
                frameLayout.addView(managerView = new RequestManagerView(frameLayout.getContext(), this));
            }
        }
        return null == managerView ? null : managerView.getPageLifeManager();
    }

    private PageLifeManager bindActivity(FragmentActivity fragmentActivity) {
        WeDebug.logD("-----------bindActivity-------" + fragmentActivity.getPackageName());
        if (fragmentActivity.isDestroyed() || fragmentActivity.isFinishing()) {
            WeDebug.logD("-----------bindActivity-is-destroy- ");
            return new PageLifeManager(null);
        }
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        return supportFragmentGet(fragmentActivity, supportFragmentManager, fragmentActivity.getClass().getSimpleName());
    }

    private PageLifeManager supportFragmentGet(Context context, FragmentManager fragmentManager, String stringTag) {
        RequestManagerFragment tag = (RequestManagerFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (null == tag) {
            tag = mLifeManager.get(stringTag);
            if (null == tag) {
                tag = new RequestManagerFragment(context, this);
                tag.setLifeTag(stringTag);
                fragmentManager.beginTransaction().add(tag, FRAGMENT_TAG).commitAllowingStateLoss();
                mLifeManager.put(stringTag, tag);
            } else {
                return tag.getPageLifeManager();
            }
        }
        return tag.getPageLifeManager();
    }

}
