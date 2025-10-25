package musi

import arc.*
import mindustry.*

object MusiSettings {
    fun load(){
        Vars.ui.settings.addCategory("MusiChaos"){t ->
            t.checkPref("includeunspecifiedtracks", false)

            t.checkPref("reloadtracks", false, {
                MusiVars.handleTracks()
                Core.settings.put("reloadtracks", false)
                t.rebuild()
            } )
        }
    }
}