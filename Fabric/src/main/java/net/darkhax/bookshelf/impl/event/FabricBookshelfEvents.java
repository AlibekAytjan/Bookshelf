package net.darkhax.bookshelf.impl.event;

import net.darkhax.bookshelf.api.event.block.IFarmlandTrampleListener;
import net.darkhax.bookshelf.api.event.client.IRecipeSyncEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class FabricBookshelfEvents {

    /**
     * This event is posted on the client when it finishes processing a recipe update packet.
     */
    public static final Event<IRecipeSyncEvent> RECIPE_SYNC = EventFactory.createArrayBacked(IRecipeSyncEvent.class, callbacks -> (recipeManager) -> {

        for (IRecipeSyncEvent callback : callbacks) {

            callback.apply(recipeManager);
        }
    });

    /**
     * This event is posted when an entity tramples on farmland.
     */
    public static final Event<IFarmlandTrampleListener> FARMLAND_TRAMPLE_EVENT = EventFactory.createArrayBacked(IFarmlandTrampleListener.class, callbacks -> (player, pos, state) -> {

        for (IFarmlandTrampleListener callback : callbacks) {

            if (callback.apply(player, pos, state)) {

                return true;
            }
        }

        return false;
    });
}