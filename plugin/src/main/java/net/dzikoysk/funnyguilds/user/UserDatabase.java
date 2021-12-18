package net.dzikoysk.funnyguilds.user;

import java.util.Set;

public interface UserDatabase {

    void saveUser(User user);

    void deleteUser(User user);

    Set<User> getAllUser();

}
