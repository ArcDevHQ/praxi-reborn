package gg.arcdev.practice.core.hotbar

import lombok.Getter
import lombok.Setter
import java.util.regex.Pattern

enum class HotbarItem(val command: String?) {
    QUEUE_JOIN_RANKED(null),
    QUEUE_JOIN_UNRANKED(null),
    QUEUE_LEAVE(null),
    PARTY_EVENTS(null),
    PARTY_CREATE("party create"),
    PARTY_DISBAND("party disband"),
    PARTY_LEAVE("party leave"),
    PARTY_INFORMATION("party info"),
    OTHER_PARTIES(null),
    KIT_EDITOR(null),
    SPECTATE_STOP("stopspectating"),
    VIEW_INVENTORY(null),
    EVENT_JOIN("event join"),
    EVENT_LEAVE("event leave"),
    MAP_SELECTION(null),
    REMATCH_REQUEST("rematch"),
    REMATCH_ACCEPT("rematch"),
    KIT_SELECTION(null);

    var pattern: Pattern? = null
}