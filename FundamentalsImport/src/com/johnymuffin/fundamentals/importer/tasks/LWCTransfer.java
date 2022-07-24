package com.johnymuffin.fundamentals.importer.tasks;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.griefcraft.sql.Database;
import com.johnymuffin.beta.fundamentals.Fundamentals;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import org.bukkit.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;


public class LWCTransfer {

    private String newUsername;
    private String oldUsername;
    private Fundamentals fundamentals;
    private FundamentalsPlayer fundamentalsPlayer;
    private ArrayList<String> transferDebug;


    public LWCTransfer(String newUsername, String oldUsername, Fundamentals fundamentals, FundamentalsPlayer fundamentalsPlayer, ArrayList<String> transferDebug) {
        this.newUsername = newUsername;
        this.oldUsername = oldUsername;
        this.fundamentals = fundamentals;
        this.transferDebug = transferDebug;
        this.fundamentalsPlayer = fundamentalsPlayer;
    }


    public void runTransfer() {
        String where = "owner = '" + oldUsername + "'";
        LWC lwc = LWC.getInstance();

        List<Integer> toRemove = new LinkedList<Integer>();
        int totalProtections = lwc.getPhysicalDatabase().getProtectionCount();
        int count = 0;
        lwc.getUpdateThread().flush();
        if (where != null || !where.trim().isEmpty())
            where = " WHERE " + where.trim();
        Fundamentals.getPlugin().debugLogger(Level.INFO, "[LWC-TRANSFER] Loading protections via STREAM mode", 2);
        try {
            Statement resultStatement = lwc.getPhysicalDatabase().getConnection().createStatement(1003, 1007);
            if (lwc.getPhysicalDatabase().getType() == Database.Type.MySQL)
                resultStatement.setFetchSize(-2147483648);
            String prefix = lwc.getPhysicalDatabase().getPrefix();
            ResultSet result = resultStatement.executeQuery("SELECT " + prefix + "protections.id AS protectionId, " + prefix + "protections.type AS protectionType, x, y, z, flags, blockId, world, owner, password, date, last_accessed FROM " + prefix + "protections" + where);
            while (result.next()) {
                Protection protection = lwc.getPhysicalDatabase().resolveProtectionNoRights(result);
                World world = protection.getBukkitWorld();
                if (protection.hasFlag(Protection.Flag.EXEMPTION))
                    continue;
                count++;
                if (count % 100000 == 0 || count == totalProtections || count == 1)
//                    sender.sendMessage("+ count + " / " + totalProtections);
                    if (world == null)
                        continue;
                toRemove.add(Integer.valueOf(protection.getId()));
            }
            result.close();
            resultStatement.close();
            fullRemoveProtections(toRemove);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        lwc.getPhysicalDatabase().precache();

    }


    private void fullRemoveProtections(List<Integer> toRemove) throws SQLException {
        LWC lwc = LWC.getInstance();

        StringBuilder builder = new StringBuilder();
        int total = toRemove.size();
        int count = 0;
        Iterator<Integer> iter = toRemove.iterator();
        String prefix = lwc.getPhysicalDatabase().getPrefix();
        Statement statement = lwc.getPhysicalDatabase().getConnection().createStatement();
        while (iter.hasNext()) {
            int protectionId = ((Integer) iter.next()).intValue();
            if (count % 100000 == 0) {
//                builder.append("DELETE FROM " + prefix + "protections WHERE id IN (" + protectionId);
                builder.append("UPDATE  " + prefix + "protections SET owner = \"" + newUsername + "\"WHERE id IN (" + protectionId);
            } else {
                builder.append("," + protectionId);
            }
            if (count % 100000 == 99999 || count == total - 1) {
                builder.append(")");
                statement.executeUpdate(builder.toString());
                builder.setLength(0);
//                sender.sendMessage("" + (count + 1) + " / " + total);
                Fundamentals.getPlugin().debugLogger(Level.INFO, "[LWC-TRANSFER] " + (count + 1) + " / " + total, 2);
                transferDebug.add("Transferred (" + (count + 1) + ") LWC protections");
            }
            count++;
        }
        statement.close();
    }


}
