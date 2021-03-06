package com.xiaomai.geek.presenter.github;

import com.xiaomai.geek.data.api.AccountApi;
import com.xiaomai.geek.data.module.User;
import com.xiaomai.geek.data.net.response.BaseResponseObserver;
import com.xiaomai.geek.presenter.BaseRxPresenter;
import com.xiaomai.geek.view.ILoginView;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by XiaoMai on 2017/4/26.
 */

public class LoginPresenter extends BaseRxPresenter<ILoginView> {

    private final AccountApi accountApi;

    @Inject
    public LoginPresenter(AccountApi accountApi) {
        this.accountApi = accountApi;
    }

    public void login(String userName, String password) {
        mCompositeSubscription.add(
                accountApi.login(userName, password)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                getMvpView().showLoading();
                            }
                        })
                        .doOnTerminate(new Action0() {
                            @Override
                            public void call() {
                                getMvpView().dismissLoading();
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseResponseObserver<User>() {
                    @Override
                    public void onSuccess(User user) {
                        if (user != null) {
                            getMvpView().loginSuccess(user);
                        } else {
                            getMvpView().error(new Throwable("登录失败！"));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().error(e);
                    }
                })
        );
    }
}
