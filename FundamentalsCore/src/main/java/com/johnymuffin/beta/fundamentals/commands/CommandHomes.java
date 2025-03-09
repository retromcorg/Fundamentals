package com.johnymuffin.beta.fundamentals.commands;

import static com.johnymuffin.beta.fundamentals.util.Utils.formatColor;
import static com.johnymuffin.beta.fundamentals.util.Utils.getUUIDFromUsername;
import static org.bukkit.craftbukkit.TextWrapper.CHAT_WINDOW_WIDTH;
import static org.bukkit.craftbukkit.TextWrapper.widthInPixels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.johnymuffin.beta.fundamentals.FundamentalsPlayerMap;
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsConfig;
import com.johnymuffin.beta.fundamentals.settings.FundamentalsLanguage;

public class CommandHomes implements CommandExecutor {
    private final String PERMISSION_NODE = "fundamentals.homes";
    private final String PERMISSION_NODE_OTHERS = "fundamentals.homes.others";

    private final String MINIMUM_CELL_PADDING = " ";

    @Override
    public boolean onCommand(
        CommandSender sender,
        Command command,
        String label,
        String[] args
    ) {
        if(!validateCommandSender(sender))
            return true;

        Player senderPlayer = (Player) sender;
        boolean canSeeOtherPlayerHomes = canSeeOtherPlayerHomes(senderPlayer);

        switch(args.length) {
            case 0: {
                listSendersHomes(senderPlayer, 1);

                return true;
            }
            case 1: {
                String arg = args[0];

                try {
                    int pageToView = Integer.parseInt(arg);

                    listSendersHomes(senderPlayer, pageToView);
                } catch (NumberFormatException e) {
                    if(!canSeeOtherPlayerHomes) {
                        printUsage(senderPlayer, false);
                        return true;
                    }

                    listOtherPlayersHomes(senderPlayer, arg, 1);
                }

                return true;
            }
            case 2: {
                if(!canSeeOtherPlayerHomes) {
                    printUsage(senderPlayer, false);
                    return true;
                }

                String otherPlayer = args[0];
                String page = args[1];

                try {
                    int pageToView = Integer.parseInt(page);
                    listOtherPlayersHomes(senderPlayer, otherPlayer, pageToView);

                    return true;
                } catch (NumberFormatException e) {
                    // the implementation for this would be the same as the logic after the switch, so this is empty
                }
            }
        }

        printUsage(senderPlayer, canSeeOtherPlayerHomes);

        return true;
    }

    private boolean canUseCommand(CommandSender sender) {
        return (
            sender.hasPermission(PERMISSION_NODE) ||
            sender.isOp()
        );
    }

    private boolean validateCommandSender(CommandSender sender) {
        if (!canUseCommand(sender)) {
            sender.sendMessage(getMessage("no_permission"));
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("unavailable_to_console"));
            return false;
        }

        return true;
    }

    private boolean canSeeOtherPlayerHomes(Player sender) {
        return (
            sender.hasPermission(PERMISSION_NODE_OTHERS) ||
            sender.isOp()
        );
    }

    private void printUsage(Player sender, boolean canSeeOtherPlayerHomes) {
        if(canSeeOtherPlayerHomes)
            sender.sendMessage(getMessage("homes_usage_staff"));
        else
            sender.sendMessage(getMessage("homes_usage"));
    }

    private void listSendersHomes(Player sender, int pageToView) {
        FundamentalsPlayer fundamentalsPlayer = FundamentalsPlayerMap.getInstance().getPlayer(sender);

        String noHomesError = getMessage("homes_non_recorded");
        listHomes(sender, fundamentalsPlayer, pageToView, noHomesError);
    }

    private void listOtherPlayersHomes(Player sender, String targetPlayerName, int pageToView) {
        UUID targetPlayerUUID = getUUIDFromUsername(targetPlayerName);
        if(targetPlayerUUID == null) {
            String playerNotFoundMessage = getMessage("player_not_found_full");
            playerNotFoundMessage = playerNotFoundMessage.replace("%username%", targetPlayerName);
            sender.sendMessage(playerNotFoundMessage);

            return;
        }

        FundamentalsPlayer targetPlayer = FundamentalsPlayerMap.getInstance().getPlayer(targetPlayerUUID);

        String noHomesError = getMessage("homes_non_recorded_others");
        listHomes(sender, targetPlayer, pageToView, noHomesError);
    }
    
    private ArrayList<Page> getHomeLines(ArrayList<String> homes) {
        ArrayList<Page> output = new ArrayList<>();

        Page currentPage = new Page();
        output.add(currentPage);
        
        for (String home : homes) {
            if(currentPage.tryAdd(home))
                continue;
                
            // add new page since the last one is full
            currentPage = new Page();
            output.add(currentPage);
        }

        return output;
    }

    private void listHomes(Player sender, FundamentalsPlayer targetPlayer, int pageToView, String noHomesError) {
        ArrayList<String> homes = targetPlayer.getPlayerHomes();
        Collections.sort(homes);

        if(homes.size() == 0) {
            sender.sendMessage(noHomesError);
            return;
        }

        ArrayList<Page> homePages = getHomeLines(homes);
        int pageCount = homePages.size();

        pageToView = clamp(pageToView, 1, pageCount);
        Page page = homePages.get(pageToView - 1);

        String container = getHomesContainer(pageToView, pageCount);
        
        sendChatMessage(sender, page, container);
    }

    private String getHomesContainer(int pageToView, int pageCount) {
        String output = getMessage("homes_page_count");
        output = output.replace("%pageToView%", String.valueOf(pageToView));
        output = output.replace("%pageCount%", String.valueOf(pageCount));

        return output;
    }

    private void sendChatMessage(Player sender, Page page, String container) {
        sender.sendMessage(container);

        for(String line : page.lines) {
            sender.sendMessage(line);
        }

        sender.sendMessage(container);
    }

    private int clamp(int input, int min, int max) {
        input = Integer.min(input, max);
        input = Integer.max(input, min);

        return input;
    }

    private String getMessage(String key) {
        return FundamentalsLanguage.getInstance().getMessage(key);
    }

    class Page {
        final ArrayList<String> lines;
        final int spaceSize;
        final int halfSpaceSize;
        final int linesPerPage;
        final int cellSize;

        final String[] colors = {"&f", "&c", "&6", "&e", "&a", "&b", "&d"};
        int color = 0;

        public Page() {
            lines = new ArrayList<>();
            lines.add("");

            int homesPerLine = FundamentalsConfig.getInstance().getConfigInteger("settings.homes-homes-per-line");

            spaceSize = widthInPixels(" ");
            halfSpaceSize = widthInPixels(".");
            linesPerPage = FundamentalsConfig.getInstance().getConfigInteger("settings.homes-lines-per-page");
            cellSize = CHAT_WINDOW_WIDTH / homesPerLine;
        }

        public boolean tryAdd(String home) {
            String color = getColor();
            home = formatColor(color + home);

            home += MINIMUM_CELL_PADDING;
            
            int lastLineIndex = lines.size() - 1;
            String lastLine = lines.get(lastLineIndex);
    
            int homeWidth = widthInPixels(home);
            int currentLineWidth = widthInPixels(lastLine);
    
            if (currentLineWidth + homeWidth < CHAT_WINDOW_WIDTH) {
                String newLine = combineHomeWithPadding(lastLine, home);
                lines.set(lastLineIndex, newLine);
                return true;
            }

            // generate new line on the page
            if (lines.size() >= linesPerPage)
                return false;
    
            lines.add(padString(home));
            return true;
        }

        private String combineHomeWithPadding(String currentLine, String home) {
            String combined = currentLine + home;
            return padString(combined);
        }

        private String padString(String string) {
            int size = widthInPixels(string);

            int paddingInPixelsNeeded = getAdditionalPaddingSize(size);
            String padding = generatePadding(paddingInPixelsNeeded);

            return string + padding;
        }
        
        private int getAdditionalPaddingSize(int currentSize) {
            int cellSpan = (currentSize / cellSize) + 1;
            int cellWidthInPixels = cellSpan * cellSize;

            return cellWidthInPixels - currentSize;
        }

        private String generatePadding(int padding) {
            int possibleSpaces = padding / spaceSize;
            int possiblePaddingInPixels = possibleSpaces * spaceSize;
    
            int remainingPixels = padding - possiblePaddingInPixels;

            String outputPadding = String.join("", Collections.nCopies(possibleSpaces, " "));
            if(remainingPixels >= halfSpaceSize)
                outputPadding += formatColor("&0.");

            return outputPadding;
        }

        private String getColor() {
            color++;
            color = color % colors.length;

            return colors[color];
        }
    }
}
