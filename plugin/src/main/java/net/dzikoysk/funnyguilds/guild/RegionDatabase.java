package net.dzikoysk.funnyguilds.guild;

import java.util.Set;

public interface RegionDatabase {

    void saveRegion(Region region);

    void deleteRegion(Region region);

    Set<Region> getAllRegions();

}
