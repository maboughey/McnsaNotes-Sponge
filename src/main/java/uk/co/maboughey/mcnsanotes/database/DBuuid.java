package uk.co.maboughey.mcnsanotes.database;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import uk.co.maboughey.mcnsanotes.McnsaNotes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class DBuuid {
    public static String getUUID(String player)
    {
        String uuid = getUUIDFromName(player);
        if (uuid !=null) {
            return uuid;
        }
        Connection connect = DatabaseManager.getConnection();
        try
        {
            PreparedStatement statement = connect.prepareStatement("SELECT uuid FROM knownUsernames WHERE name LIKE ? ORDER BY id DESC LIMIT 1");
            statement.setString(1, "%" + player + "%");
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                String result = results.getString("uuid");
                DatabaseManager.close();
                return result;
            }
        }
        catch (SQLException e)
        {
            McnsaNotes.log.error("Database Error getting uuid: " + e.getMessage());
        }
        return null;
    }

    public static String getUsername(String UUID) {

        String user = getNameFromUUID(UUID);
        if (user !=null) {
            return user;
        }

        Connection connect = DatabaseManager.getConnection();

        try {
            PreparedStatement statement = connect.prepareStatement("SELECT name FROM knownUsernames WHERE uuid=? ORDER BY id DESC LIMIT 1");
            statement.setString(1, UUID);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return results.getString("name");
            }
        } catch (SQLException e) {
            McnsaNotes.log.error("Database Error getting name from UUID: "+e.getLocalizedMessage());
        }
        return null;
    }
    public static String getNameFromUUID(String uuid) {
        String name = null;

        //Try getting user from server of players that have logged in before
        UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        Optional<User> oUser = uss.get(UUID.fromString(uuid));
        if (oUser.isPresent()) {
            name = oUser.get().getName();
        }
        return name;
    }
    public static String getUUIDFromName(String name) {
        String uuid = null;

        //Try getting user from server of players that have logged in before
        UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        Optional<User> oUser = uss.get(name);
        if (oUser.isPresent()) {
            uuid = oUser.get().getUniqueId().toString();
        }
        return uuid;
    }
}
