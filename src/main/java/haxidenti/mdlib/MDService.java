package haxidenti.mdlib;

public interface MDService {
    Object accept(MDMessage msg, MDContext ctx);

    default boolean unregister() {
        return true;
    }
}
