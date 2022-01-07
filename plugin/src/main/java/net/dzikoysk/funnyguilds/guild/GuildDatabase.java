package net.dzikoysk.funnyguilds.guild;

import panda.std.reactive.Completable;

import java.util.Set;

public interface GuildDatabase {

    Completable<Boolean> saveGuild(Guild guild);

    Completable<Boolean> deleteGuild(Guild guild);

    Completable<Set<Guild>> getAllGuilds();

}
