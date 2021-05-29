package com.snowblitzz.miragedexranker.ui;

import com.snowblitzz.miragedexranker.MirageDexRanker;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.Text;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InventoryManager {
    private Inventory.Builder builder;
    private Map<Integer, InventoryIcon> icons;
    private Inventory inventory;
    private int size;

    public InventoryManager(int size, Text title) {
        this.size = size;
        this.icons = new HashMap<>();
        this.builder = Inventory.builder()
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(title))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9, size))
                .of(InventoryArchetypes.MENU_GRID)
                .listener(ClickInventoryEvent.class, this::processClick);
    }

    private void processClick(ClickInventoryEvent event) {
        event.setCancelled(true);
    }

    public Inventory.Builder getInventoryBuilder() {
        return this.builder;
    }

    public void addIcon(InventoryIcon icon) {
        this.icons.put(icon.getSlot(), icon);
    }

    public Optional<InventoryIcon> getIcon(int slot) {
        return Optional.ofNullable(this.icons.get(slot));
    }

    public void clearIcons(int... slots) {
        for(int slot : slots) {
            this.icons.remove(slot);
        }
    }

    public Map<Integer, InventoryIcon> getAllIcons() {
        return this.icons;
    }

    public void updateContents(int... slots) {
        //GridInventory gridInventory = this.inventory.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
        GridInventory gridInventory = this.inventory.query(GridInventory.class);

        for(final int sl : slots) {
            Slot slot = gridInventory.getSlot(SlotIndex.of(sl)).orElseThrow(() -> new IllegalArgumentException("Invalid index: " + sl));
            if(this.icons.containsKey(sl))
                slot.set(this.icons.get(sl).getDisplay());
            else
                slot.clear();
        }
    }

    public void updateContents() {
        //GridInventory gridInventory = this.inventory.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
        GridInventory gridInventory = this.getInventory().query(GridInventory.class);
        gridInventory.clear();

        this.icons.forEach((index, inventoryIcon) -> {
            Slot slot = gridInventory.getSlot(SlotIndex.of(index)).orElseThrow(() -> new IllegalArgumentException("Invalid index: " + index));
            slot.set(inventoryIcon.getDisplay());
        });
    }

    public Inventory getInventory() {
        if (this.inventory == null) {
            this.buildInventory();
        }

        return this.inventory;
    }

    private void buildInventory() {
        this.icons.values().forEach((button) -> {
            button.getListeners().forEach((c, listener) -> {
                this.builder.listener(c, listener);
            });
        });

        this.inventory = this.builder.build(MirageDexRanker.getInstance());
        //GridInventory gridInventory = this.inventory.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
        GridInventory gridInventory = inventory.query(GridInventory.class);

        this.icons.forEach((index, inventoryIcon) -> {
            Slot slot = gridInventory.getSlot(SlotIndex.of(index)).orElseThrow(() -> new IllegalArgumentException("Invalid index: " + index));
            slot.set(inventoryIcon.getDisplay());
        });
    }
}
