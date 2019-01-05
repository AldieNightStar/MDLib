package haxidenti.mdlib;

import java.util.ArrayDeque;

class MDPool {
    private ArrayDeque<PoolEntity> poolArray = new ArrayDeque<>();

    public void put(String serviceName, MDMessage msg, MDCallBack cb) {
        PoolEntity ent = new PoolEntity();
        ent.serviceName = serviceName;
        ent.message = msg;
        ent.callBack = cb;
        try {
            poolArray.push(ent);
        } catch (Exception e) {
            throw new MDException(e.getMessage());
        }
    }

    public PoolEntity poll() {
        try {
            return poolArray.pollLast();
        } catch (Exception e) {
            return null;
        }
    }

    public static class PoolEntity {
        public String serviceName;
        public MDMessage message;
        public MDCallBack callBack;
    }
}
