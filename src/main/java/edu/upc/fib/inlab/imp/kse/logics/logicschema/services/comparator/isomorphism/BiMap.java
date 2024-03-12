package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.isomorphism;

import java.util.LinkedHashMap;

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

    void remove(K k) {
        V v = map.remove(k);
        inverseMap.remove(v);
    }

    boolean containsKey(K k) {
        return map.containsKey(k);
    }

    boolean containsValue(V v) {
        return inverseMap.containsKey(v);
    }

}