package cn.wenet.networkcomponent.request;

import java.util.HashMap;
import java.util.Map;

/**
 * Created to :
 *
 * @author WANG
 * @date 2020/4/16
 */
public class InnerRequestProvider {

    protected Map<String, NetRequestImpl> mPatams;

    public void addRequest(String url, NetRequestImpl request) {
        if (null == mPatams) {
            mPatams = new HashMap<>(20);
        }
        mPatams.put(url, request);
    }

    public void removeRequest(String url) {
        if (null != mPatams) {
            mPatams.remove(url);
        }
    }

    public Map<String, NetRequestImpl> getParams() {
        return mPatams;
    }

}
