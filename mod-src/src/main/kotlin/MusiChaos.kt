package musi

import arc.*
import arc.util.*
import mindustry.game.EventType.*
import mindustry.mod.*

class MusiChaos : Mod() {
	init {
        Events.on(ClientLoadEvent::class.java) {
			Time.run(120f){
			    MusiVars.load()
			    MusiVars.handleTracks()
			}
			
			MusiSettings.load()
		}
	}
}
