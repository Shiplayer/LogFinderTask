package ship.code.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LiveData<T> {
    private static final ExecutorService executor = Executors.newFixedThreadPool(20);
    private T value;
    private List<Observable<T>> observableList;

    public LiveData() {
        this.value = null;
        observableList = new ArrayList<>();
    }

    public void postValue(T value){
        this.value = value;
        executor.execute(() -> {
            for (Observable<T> anObservableList : observableList) {
                anObservableList.onChange(value);
            }
        });
    }

    public void removeObservers(){
        List<Runnable> list = executor.shutdownNow();
        observableList.clear();
    }
}
