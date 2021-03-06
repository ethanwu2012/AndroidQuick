package la.xiong.androidquick.demo.architecture.mvvm.activity;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import la.xiong.androidquick.demo.module.network.retrofit.GankRes;
import la.xiong.androidquick.module.network.retrofit.exeception.ApiException;
import la.xiong.androidquick.module.rxjava.BaseObserver;
import la.xiong.androidquick.tool.RxUtil;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class MVVMViewModel extends ViewModel {

    private MVVMRepository repository;
    private LifecycleProvider<ActivityEvent> lifecycleProvider;

    final MutableLiveData<List<String>> liveData = new MutableLiveData<>();

    public MVVMViewModel(MVVMRepository repository, LifecycleProvider<ActivityEvent> activityEventLifecycleProvider) {
        this.repository = repository;
        this.lifecycleProvider = activityEventLifecycleProvider;
    }

    public MutableLiveData<List<String>> getData() {
        return liveData;
    }

    public void getGankResData() {
        repository.getGankResData()
                .flatMap(new Function<GankRes<List<String>>, ObservableSource<List<String>>>() {
                    @Override
                    public ObservableSource<List<String>> apply(GankRes<List<String>> s) throws Exception {
                        Log.d("RxJava", "flatMap1 " + Thread.currentThread().getName());
                        return Observable.just(s.getResults());
                    }
                })
                .compose(RxUtil.applySchedulers())
                .compose(lifecycleProvider.bindToLifecycle())
                .subscribe(new BaseObserver<List<String>>() {
                    @Override
                    public void onError(ApiException exception) {
                        Log.e("tag", "error" + exception.getMessage());
                        liveData.setValue(new ArrayList<>());
                    }

                    @Override
                    public void onSuccess(List<String> s) {
                        Log.e("tag", "onSuccess");
                        liveData.setValue(s);
                    }
                });
    }
}
