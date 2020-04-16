package cn.wenet.networkcomponent.utils;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import cn.wenet.networkcomponent.cache.DiskLruCache;
import cn.wenet.networkcomponent.core.WeNetWork;
import cn.wenet.networkcomponent.debug.WeDebug;

/**
 * Created to :
 *
 * @author cc.wang
 * @date 2020/4/14
 */
public class DisLruCacheUtils {


    public static boolean writer(String url, String json) {
        try {
            File cacheFile = WeNetWork.getCacheFile();
            if (null == cacheFile) {
                return false;
            }
            WeDebug.e("开始缓存： ", cacheFile.getAbsolutePath());
            DiskLruCache lruCache = DiskLruCache.open(new File(""), 1, 1, 1024 * 1024 * 6);
            DiskLruCache.Editor edit = lruCache.edit(WeNetStringUtils.stringToMd5(url));
            OutputStream outputStream = edit.newOutputStream(0);
            byte[] bytes = json.getBytes();
            outputStream.write(bytes);
            edit.commit();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            WeDebug.e("DisLruCacheUtils.writer : IOException ->", e.getMessage());
            return false;
        }


    }

}
