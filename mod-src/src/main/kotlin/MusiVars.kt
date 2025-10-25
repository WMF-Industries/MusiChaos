package musi

import arc.*
import arc.util.*
import arc.audio.*
import arc.files.*
import arc.struct.*
import mindustry.*

@Suppress("MemberVisibilityCanBePrivate", "SpellCheckingInspection")
object MusiVars {
    //cache for builtin tracks
    val ambientMusicCache = Seq<Music>()
    val darkMusicCache = Seq<Music>()
    val bossMusicCache = Seq<Music>()
    
    val customTrackCache = ObjectMap<Fi, Music?>()

    private fun addTrack(file: Fi){
        val sounds = Vars.control.sound

        when{
            file.name().startsWith("ambientmus-") -> {

                val ambi = loadMusicFile(file)

                if(ambi != null) sounds.ambientMusic.add(ambi)
            }

            file.name().startsWith("darkmus-") -> {

                val dark = loadMusicFile(file)

                if(dark != null) sounds.darkMusic.add(dark)
            }

            file.name().startsWith("bossmus-") -> {

                val boss = loadMusicFile(file)

                if(boss != null) sounds.bossMusic.add(boss)
            }

            else -> {
                Log.info("Unspecified music file: ${file.name()}")

                if(Core.settings.getBool("includeunspecifiedtracks", false)){
                    val unspec = loadMusicFile(file)
                    val builtins = arrayOf(sounds.ambientMusic, sounds.darkMusic, sounds.bossMusic)

                    builtins.random().add(unspec)
                }
            }
        }
    }
    
    private fun loadMusicFile(file: Fi): Music?{
        var mus: Music? = null

        Log.info("Loading music file: ${file.name()}")

        try{
           mus = if(customTrackCache.containsKey(file)) customTrackCache[file] else Music(file)
        }catch(err: Exception){
            Log.err("Failed to initialize music file: $file", err)
        }
        
        if(!customTrackCache.containsKey(file) && mus != null) customTrackCache.put(file, mus)
        
        return mus
    }

    private fun loadMusicPack(file: Fi){
        var pack: ZipFi? = null

        try{
            pack = ZipFi(file)
        }catch(err: ArcRuntimeException){
            Log.err("Failed to load music pack: ${file.name()}", err)
        }

        if(pack != null) Log.info("Loading music pack: ${file.name()}")

        pack?.walk{
            if(it.extension().equals("zip")){
                Log.warn("Detected a zip file in zip file ${file.name()}. These cannot be read because of technical limitations. Skipping.")
                return@walk
            }
            processFile(it)
        }
    }

    private fun loadMusicFolder(file: Fi){
        if(!file.isDirectory) return

        Log.info("Loading music folder: ${file.name()}")

        file.walk{
            processFile(it)
        }
    }

    private fun processFile(file: Fi){
        when{
            file.extension().equals("zip") -> loadMusicPack(file)
            file.extension().equals("mp3") -> addTrack(file)
            file.extension().equals("ogg") -> addTrack(file)

            file.isDirectory -> loadMusicFolder(file)
            else -> Log.warn("Unknown file: ${file.name()}. Skipping.")
        }
    }
    
    fun load(){
        Log.info("Caching original tracks...")

        ambientMusicCache.add(Vars.control.sound.ambientMusic)
        darkMusicCache.add(Vars.control.sound.darkMusic)
        bossMusicCache.add(Vars.control.sound.bossMusic)
    }
    
    fun handleTracks() {
        Log.info("Loading MusiChaos...")
        val path = Core.settings.dataDirectory.child("musichaos")
        val sounds = Vars.control.sound

        if(!path.exists())
            path.mkdirs()
        
        sounds.ambientMusic.clear().add(ambientMusicCache)
        sounds.darkMusic.clear().add(darkMusicCache)
        sounds.bossMusic.clear().add(bossMusicCache)
        
        path.walk{
            processFile(it)
        }
    }
}