package com.angcyo.http;

import androidx.annotation.Nullable;
import com.angcyo.http.log.LogUtil;
import rx.Subscriber;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2018/10/15
 */
public class HttpSubscriber<T> extends Subscriber<T> {
    T data = null;

    public static boolean isNetworkException(Throwable error) {
        return error instanceof NonetException ||
                error instanceof ConnectException ||
                error instanceof SocketTimeoutException ||
                error instanceof NoRouteToHostException ||
                error instanceof UnknownHostException;
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.d();
    }

    @Override
    public void onCompleted() {
        //LogUtil.d();
        onEnd(data, null);
    }

    @Override
    public void onError(Throwable e) {
        //LogUtil.e(e);
        onEnd(null, e);
    }

    @Override
    public void onNext(T t) {
        //LogUtil.w(t);
        data = t;
        onSucceed(t);
    }

    public void onSucceed(T data) {
        //LogUtil.i(data);
    }

    public void onEnd(@Nullable T data /*如果成功, 才有值*/, @Nullable Throwable error /*如果失败, 才有值*/) {
        if (error == null && data != null) {
            LogUtil.i(LogUtil.mGlobalTag, data);
        } else {
            LogUtil.e(LogUtil.mGlobalTag, data, error);
            if (error != null) {
                error.printStackTrace();
            }
        }
    }
}
