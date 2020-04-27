package cn.wenet.networkcomponent.life;

import android.content.Context;
import android.view.View;


/**
 * Created to :
 *
 * @author cc.wang
 * @date 2020/4/10
 */
public class RequestManagerView extends View {
    private PageLifeManager pageLifeManager;

    public RequestManagerView(Context context,WeNetLifeCircleManager manager) {
        super(context);
        pageLifeManager = new PageLifeManager(manager);
    }

    PageLifeManager getPageLifeManager() {
        return pageLifeManager;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(0,0);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(null != pageLifeManager) {
            pageLifeManager.pageDestroy();
        }
    }

}
