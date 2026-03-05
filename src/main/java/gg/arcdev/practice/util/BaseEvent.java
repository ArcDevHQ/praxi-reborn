package gg.arcdev.practice.util;

import gg.arcdev.practice.Main;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BaseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void call() {
        Main.getInstance().getServer().getPluginManager().callEvent(this);
    }

}
