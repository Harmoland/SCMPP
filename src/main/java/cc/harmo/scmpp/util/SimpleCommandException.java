package cc.harmo.scmpp.util;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class SimpleCommandException
        implements CommandExceptionType {

    private final Message message;

    public SimpleCommandException(Message message) {
        this.message = message;
    }

    public CommandSyntaxException create() {
        return new CommandSyntaxException(this, this.message);
    }

    public String toString() {
        return this.message.getString();
    }
}
