package ship.code.utils;

public interface Observable<T> {
    void onChange(T value);
}
