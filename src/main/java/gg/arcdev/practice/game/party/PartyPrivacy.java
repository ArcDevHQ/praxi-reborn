package gg.arcdev.practice.game.party;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PartyPrivacy {

	OPEN("Open"),
	CLOSED("Closed");

	private String readable;

}
