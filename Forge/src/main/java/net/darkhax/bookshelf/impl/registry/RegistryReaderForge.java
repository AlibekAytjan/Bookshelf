package net.darkhax.bookshelf.impl.registry;

import net.darkhax.bookshelf.api.registry.IRegistryReader;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public record RegistryReaderForge<T>(IForgeRegistry<T> registry) implements IRegistryReader<T> {

    @Nullable
    @Override
    public T get(ResourceLocation id) {

        return this.registry.getValue(id);
    }

    @Nullable
    @Override
    public ResourceLocation getId(T value) {

        return this.registry.getKey(value);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {

        return registry.iterator();
    }
}