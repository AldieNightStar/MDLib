package haxidenti.mdlib;

import java.util.regex.Pattern;

public class MDMessage {
    public String messageText;
    public Object messageObject;

    public MDMessage(String text, Object o) {
        if (text == null) throw new MDException("Message must to have text");
        this.messageText = text;
        this.messageObject = o;
    }

    public String[] getCommandAndArguments() {
        return messageText.split(Pattern.quote(" "), 2);
    }
}
