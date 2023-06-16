package edu.upc.fib.inlab.imp.kse.logics.services.comparator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class BiMap<K, V> {

    private LinkedHashMap<K, V> map = new LinkedHashMap<>();
    private LinkedHashMap<V, K> inverseMap = new LinkedHashMap<>();


    public BiMap(BiMap<K, V> bimap) {
        this.map = new LinkedHashMap<>(bimap.map);
        this.inverseMap = new LinkedHashMap<>(bimap.inverseMap);
    }

    public BiMap() {
    }

    void put(K k, V v) {
        map.put(k, v);
        inverseMap.put(v, k);
    }

    V get(K k) {
        return map.get(k);
    }

    public boolean containsKey(K k) {
        return map.containsKey(k);
    }

    public boolean containsValue(V v) {
        return inverseMap.containsKey(v);
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return map.entrySet();
    }
}