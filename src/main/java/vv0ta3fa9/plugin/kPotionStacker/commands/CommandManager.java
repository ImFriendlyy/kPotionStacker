package vv0ta3fa9.plugin.kPotionStacker.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import vv0ta3fa9.plugin.kPotionStacker.kPotionStacker;

public class CommandManager implements CommandExecutor {

    private static final int MAX_STACK_SIZE = 16;
    private final kPotionStacker plugin;

    public CommandManager(kPotionStacker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            send(sender, "&cЭта команда доступна только игрокам!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0) {
            send(sender, "&cИспользование: /kpotionstacker");
            return true;
        }

        ItemStack potionInHand = player.getInventory().getItemInMainHand();
        if (potionInHand == null || potionInHand.getType() == Material.AIR) {
            send(player, "&cВы должны держать зелье в руке!");
            return true;
        }

        if (!isPotion(potionInHand.getType())) {
            send(player, "&cВ руке должно быть обычное, взрывное или туманное зелье.");
            return true;
        }

        PlayerInventory inventory = player.getInventory();
        int heldSlot = inventory.getHeldItemSlot();
        ItemStack[] contents = inventory.getContents();

        int totalAmount = 0;
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack stack = contents[slot];
            if (stack == null || stack.getType() == Material.AIR) {
                continue;
            }

            if (!stack.isSimilar(potionInHand)) {
                continue;
            }

            totalAmount += stack.getAmount();
            if (slot != heldSlot) {
                inventory.setItem(slot, null);
            }
        }

        int duplicates = totalAmount - potionInHand.getAmount();
        if (duplicates <= 0) {
            send(player, "&eПодходящие зелья в инвентаре не найдены.");
            return true;
        }

        int stackAmount = Math.min(totalAmount, MAX_STACK_SIZE);
        int remainder = totalAmount - stackAmount;

        ItemStack stackedPotion = potionInHand.clone();
        stackedPotion.setAmount(stackAmount);
        inventory.setItem(heldSlot, stackedPotion);

        if (remainder > 0) {
            storeRemainder(player, potionInHand, remainder);
        }
        player.updateInventory();

        if (remainder > 0) {
            send(player, "&aСобрано &e" + totalAmount + " &aзелья. В руке &e" + stackAmount + " &a(лимит), остаток возвращен.");
        } else {
            send(player, "&aОбъединено &e" + totalAmount + " &aодинаковых зелий в руке.");
        }
        return true;
    }

    private boolean isPotion(Material type) {
        return type == Material.POTION || type == Material.SPLASH_POTION || type == Material.LINGERING_POTION;
    }

    private void send(CommandSender sender, String msg) {
        sender.sendMessage(plugin.getColorizer().colorize(msg));
    }

    private void storeRemainder(Player player, ItemStack basePotion, int remainder) {
        PlayerInventory inventory = player.getInventory();
        while (remainder > 0) {
            int toStore = Math.min(remainder, MAX_STACK_SIZE);
            ItemStack split = basePotion.clone();
            split.setAmount(toStore);

            int slot = inventory.firstEmpty();
            if (slot == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), split);
            } else {
                inventory.setItem(slot, split);
            }

            remainder -= toStore;
        }
    }
}

