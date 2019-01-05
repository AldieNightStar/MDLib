package haxidenti.mdlib;

import java.util.HashMap;
import java.util.regex.Pattern;

public class MDContext {
    private HashMap<String, MDService> map = new HashMap<String, MDService>();
    private MDPool mdPool = new MDPool();
    private boolean ownThread = false;

    public void registerService(String k, MDService service) {
        MDService oldService = map.get(k);
        if (oldService != null) throw new MDException("Such service already exist");
        map.put(k, service);
    }

    public void unregisterService(String k) {
        MDService service = map.get(k);
        if (service == null) return;
        boolean canBeUnregistered = service.unregister();
        if (!canBeUnregistered) throw new MDException("This service do not able to unregister");
        map.remove(k);
    }

    public <R> R message(String serviceName, MDMessage message) {
        MDService service = map.get(serviceName);
        if (service == null) throw new MDException("No such service: " + serviceName);
        try {
            return (R) service.accept(message, this);
        } catch (ClassCastException e) {
            throw new MDException("Can't Cast message answer");
        }
    }

    public <R> R message(String serviceName, Object messageObject) {
        return message(serviceName, new MDMessage("", messageObject));
    }

    public void update() {
        MDPool.PoolEntity ent = mdPool.poll();
        if (ent == null) return;
        Object answer = message(ent.serviceName, ent.message);
        ent.callBack.call(answer);
    }

    /**
     * Add defferred Message, wich will be called after update
     * @param serviceName name of the service, to whom this message will be delivered
     * @param msg Message message {text, message}
     * @param cb Callback (answer)->{}
     */
    public void deferMessage(String serviceName, MDMessage msg, MDCallBack cb) {
        if (serviceName.contains(",")) {
            String[] serviceNames = serviceName.split(Pattern.quote(","));
            for (String sName : serviceNames) {
                deferMessage(sName, msg, cb);
            }
            return;
        }
        if (serviceName == null || serviceName.length() < 1) return;
        mdPool.put(serviceName, msg, cb);
    }

    /**
     * Add defferred Message, wich will be called after update
     * @param serviceName name of the service, to whom this message will be delivered
     * @param o Message Object
     * @param cb Callback (answer)->{}
     */
    public void deferMessage(String serviceName, Object o, MDCallBack cb) {
        deferMessage(serviceName, new MDMessage("", o), cb);
    }

    /**
     * Start timer Thread, which updates messages itself
     * @param interval ms between updates
     * @param daemon must Thread be Daemon or not
     */
    public void startThread(int interval, boolean daemon) {
        if (ownThread) throw new MDException("Thread already started!");
        ownThread = true;

        Runnable r = () -> {
            try {
                while (ownThread) {
                    update();
                    Thread.sleep(interval);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ownThread = false;
            }
        };
        Thread t = new Thread(r);
        t.setDaemon(daemon);
        t.start();
    }

    public void stopThread() {
        this.ownThread = false;
    }
}
