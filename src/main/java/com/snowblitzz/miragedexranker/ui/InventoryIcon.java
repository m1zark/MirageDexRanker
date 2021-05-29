package com.snowblitzz.miragedexranker.ui;

import org.spongepowered.api.event.item.inventory.AffectSlotEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.*;
import java.util.function.Consumer;

public class InventoryIcon {
    private int slot;
    private ItemStack display;
    private Map<Class<? extends InteractInventoryEvent>, Consumer<? extends InteractInventoryEvent>> listeners;

    public InventoryIcon(int slot, ItemStack display) {
        this.slot = slot;
        this.display = display;
        this.listeners = new HashMap<>();
    }

    public int getSlot() {
        return this.slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ItemStack getDisplay() {
        return this.display;
    }

    public void setDisplay(ItemStack display) {
        this.display = display;
    }

    public <E extends InteractInventoryEvent> Map<Class<E>, Consumer<E>> getListeners() {
        return (Map)this.listeners.getClass().cast(this.listeners);
    }

    public <E extends InteractInventoryEvent> void addListener(Class<E> c, Consumer<E> listener) {
        Consumer<E> consumer = (event) -> {
            if (event instanceof AffectSlotEvent) {
                AffectSlotEvent slotEvent = (AffectSlotEvent)event;
                Iterator slotIterator = slotEvent.getTransactions().iterator();

                while(slotIterator.hasNext()) {
                    SlotTransaction transaction = (SlotTransaction)slotIterator.next();
                    Optional<SlotIndex> optIndex = transaction.getSlot().getProperty(SlotIndex.class, "slotindex");
                    if (optIndex.isPresent()) {
                        SlotIndex index = optIndex.get();
                        if (index.getValue() != null && index.getValue().equals(this.getSlot())) {
                            listener.accept(event);
                        }
                    }
                }
            } else {
                listener.accept(event);
            }
        };
        this.listeners.put(c, consumer);
    }
}