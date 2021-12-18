package net.dzikoysk.funnyguilds.guild;

import java.util.Set;

public interface GuildDatabase {

    void saveGuild(Guild guild);

    void deleteGuild(Guild guild);

    Set<Guild> getAllGuilds();

}
