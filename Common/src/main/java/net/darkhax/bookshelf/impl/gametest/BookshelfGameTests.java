package net.darkhax.bookshelf.impl.gametest;

import com.google.common.base.CaseFormat;
import net.darkhax.bookshelf.Constants;
import net.darkhax.bookshelf.api.serialization.Serializers;
import net.darkhax.bookshelf.api.util.ItemStackHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.gameevent.GameEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class BookshelfGameTests {

    @GameTestGenerator
    public static Collection<TestFunction> generate() {

        final Collection<TestFunction> testFunctions = new ArrayList<>();

        // Test Java Type Serializers
        testFrom(testFunctions, new TestSerialization<>("boolean", Serializers.BOOLEAN, false, true, true, false));
        testFrom(testFunctions, new TestSerialization<>("byte", Serializers.BYTE, (byte) 1, (byte) 32, (byte) 44, (byte) 0));
        testFrom(testFunctions, new TestSerialization<>("short", Serializers.SHORT, (short) 800, (short) 1337));
        testFrom(testFunctions, new TestSerialization<>("int", Serializers.INT, 54, 23, Integer.MAX_VALUE, 234234, Integer.MIN_VALUE));
        testFrom(testFunctions, new TestSerialization<>("long", Serializers.LONG, 99L, 23441322L, Long.MIN_VALUE, 93249L -234L, Long.MAX_VALUE));
        testFrom(testFunctions, new TestSerialization<>("float", Serializers.FLOAT, 8f, -23.456f, 789.01f, Float.MAX_VALUE, -11f, Float.MIN_VALUE));
        testFrom(testFunctions, new TestSerialization<>("double", Serializers.DOUBLE, 24.92d, Double.MAX_VALUE, -922321.12345d, Double.MIN_VALUE));
        testFrom(testFunctions, new TestSerialization<>("string", Serializers.STRING, "one", "two", "3", "IV", ".....", "/I", "!@#$%^&*()_-"));
        testFrom(testFunctions, new TestSerialization<>("uuid", Serializers.UUID, UUID.randomUUID(), UUID.fromString("da0317d2-e550-11ec-8fea-0242ac120002"), UUID.randomUUID()));

        // Test Minecraft Type Serializers
        testFrom(testFunctions, new TestSerialization<>("resource_location", Serializers.RESOURCE_LOCATION, new ResourceLocation("hello_world"), new ResourceLocation("test", "two"), new ResourceLocation("test_from", "stuff/things/okay_stuff")));
        testFrom(testFunctions, new TestSerialization<>("item_stack", Serializers.ITEM_STACK, ItemStackHelper::areStacksEquivalent, ItemStackHelper.getTabItems(CreativeModeTab.TAB_COMBAT)));
        testFrom(testFunctions, new TestSerialization<>("nbt_compound_tag", Serializers.COMPOUND_TAG, Arrays.stream(ItemStackHelper.getTabItems(CreativeModeTab.TAB_COMBAT)).filter(ItemStack::hasTag).map(ItemStack::getTag).toArray(CompoundTag[]::new)));
        testFrom(testFunctions, new TestSerialization<>("text_component", Serializers.TEXT, new TranslatableComponent("moon.phase.full").withStyle(ChatFormatting.DARK_AQUA), new TextComponent("Hello World"), new TextComponent("okay").withStyle(s -> s.withFont(new ResourceLocation("minecraft:alt")))));
        testFrom(testFunctions, new TestSerialization<>("block_pos", Serializers.BLOCK_POS, new BlockPos(1, 2, 3), new BlockPos(0, 0, 0), BlockPos.of(123456L)));
        testFrom(testFunctions, new TestSerialization<>("ingredient", Serializers.INGREDIENT, BookshelfGameTests::assertEqual, new Ingredient[] {Ingredient.of(Items.STONE_AXE), Ingredient.EMPTY, Ingredient.of(Items.COAL), Ingredient.of(new ItemStack(Items.ACACIA_BOAT)), Ingredient.of(ItemTags.BEDS) }));
        testFrom(testFunctions, new TestSerialization<>("block_state", Serializers.BLOCK_STATE, Blocks.STONE.defaultBlockState(), Blocks.CHEST.defaultBlockState(), Blocks.ACACIA_PRESSURE_PLATE.defaultBlockState().setValue(PressurePlateBlock.POWERED, true)));
        testFrom(testFunctions, new TestSerialization<>("attribute_modifier", Serializers.ATTRIBUTE_MODIFIER, new AttributeModifier("test", 15d, AttributeModifier.Operation.MULTIPLY_BASE), new AttributeModifier(UUID.randomUUID(), "test_2", 9.55d, AttributeModifier.Operation.ADDITION), new AttributeModifier("test3", 35d, AttributeModifier.Operation.MULTIPLY_TOTAL)));
        testFrom(testFunctions, new TestSerialization<>("effect_instance", Serializers.EFFECT_INSTANCE, new MobEffectInstance(MobEffects.ABSORPTION, 100, 10), new MobEffectInstance(MobEffects.BAD_OMEN, 10)));

        // Test Minecraft Enum Serializers
        testFrom(testFunctions, new TestSerialization<>("item_rarity", Serializers.ITEM_RARITY, Rarity.COMMON, Rarity.EPIC, Rarity.RARE, Rarity.RARE));
        testFrom(testFunctions, new TestSerialization<>("enchantment_rarity", Serializers.ENCHANTMENT_RARITY, Enchantment.Rarity.COMMON, Enchantment.Rarity.COMMON, Enchantment.Rarity.RARE, Enchantment.Rarity.UNCOMMON));
        testFrom(testFunctions, new TestSerialization<>("attribute_modifier", Serializers.ATTRIBUTE_OPERATION, AttributeModifier.Operation.ADDITION, AttributeModifier.Operation.ADDITION, AttributeModifier.Operation.MULTIPLY_BASE, AttributeModifier.Operation.MULTIPLY_TOTAL, AttributeModifier.Operation.MULTIPLY_TOTAL));

        // Test Game Registry Serializers
        testFrom(testFunctions, new TestSerialization<>("registry_block", Serializers.BLOCK, Blocks.SAND, Blocks.STONE, Blocks.KELP, Blocks.SAND));
        testFrom(testFunctions, new TestSerialization<>("registry_item", Serializers.ITEM, Items.APPLE, Items.STICK, Items.STICK, Items.COOKED_PORKCHOP));
        testFrom(testFunctions, new TestSerialization<>("registry_enchantment", Serializers.ENCHANTMENT, Enchantments.ALL_DAMAGE_PROTECTION, Enchantments.BLAST_PROTECTION, Enchantments.BLAST_PROTECTION, Enchantments.SILK_TOUCH));
        testFrom(testFunctions, new TestSerialization<>("registry_motive", Serializers.MOTIVE, Motive.SEA, Motive.SEA, Motive.AZTEC2, Motive.DONKEY_KONG));
        testFrom(testFunctions, new TestSerialization<>("registry_potion", Serializers.POTION, Potions.EMPTY, Potions.AWKWARD, Potions.AWKWARD, Potions.FIRE_RESISTANCE, Potions.HEALING));
        testFrom(testFunctions, new TestSerialization<>("registry_attribute", Serializers.ATTRIBUTE, Attributes.ARMOR, Attributes.ATTACK_SPEED, Attributes.MAX_HEALTH, Attributes.SPAWN_REINFORCEMENTS_CHANCE));
        testFrom(testFunctions, new TestSerialization<>("registry_villager_profession", Serializers.VILLAGER_PROFESSION, VillagerProfession.ARMORER, VillagerProfession.ARMORER, VillagerProfession.BUTCHER, VillagerProfession.LEATHERWORKER, VillagerProfession.WEAPONSMITH));
        testFrom(testFunctions, new TestSerialization<>("registry_villager_type", Serializers.VILLAGER_TYPE, VillagerType.SWAMP, VillagerType.JUNGLE, VillagerType.JUNGLE, VillagerType.PLAINS));
        testFrom(testFunctions, new TestSerialization<>("registry_sound_event", Serializers.SOUND_EVENT, SoundEvents.ANVIL_STEP, SoundEvents.BAMBOO_STEP, SoundEvents.AXE_STRIP, SoundEvents.DOLPHIN_JUMP));
        testFrom(testFunctions, new TestSerialization<>("registry_menu", Serializers.MENU, MenuType.ANVIL, MenuType.GENERIC_9x2, MenuType.ENCHANTMENT, MenuType.CARTOGRAPHY_TABLE));
        testFrom(testFunctions, new TestSerialization<>("registry_particle_type", Serializers.PARTICLE, ParticleTypes.BUBBLE_POP, ParticleTypes.BUBBLE_COLUMN_UP, ParticleTypes.CLOUD, ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, ParticleTypes.DRIPPING_OBSIDIAN_TEAR));
        testFrom(testFunctions, new TestSerialization<>("registry_entity_type", Serializers.ENTITY, EntityType.BAT, EntityType.ARROW, EntityType.AXOLOTL, EntityType.DRAGON_FIREBALL));
        testFrom(testFunctions, new TestSerialization<>("registry_block_entity_type", Serializers.BLOCK_ENTITY, BlockEntityType.BARREL, BlockEntityType.BEACON, BlockEntityType.BEEHIVE, BlockEntityType.JUKEBOX));
        testFrom(testFunctions, new TestSerialization<>("registry_game_event", Serializers.GAME_EVENT, GameEvent.FLAP, GameEvent.FLUID_PICKUP, GameEvent.PRIME_FUSE, GameEvent.LIGHTNING_STRIKE));
        return testFunctions;
    }

    public static <T> void testFrom(Collection<TestFunction> functions, T testImpl) {

        final String parentBatch = testImpl instanceof ITestable testObj ? testObj.getDefaultBatch() : null;

        for (Method method : testImpl.getClass().getDeclaredMethods()) {

            if (method.isAnnotationPresent(GameTest.class)) {

                final GameTest annotation = method.getAnnotation(GameTest.class);

                final String template = annotation.template().isEmpty() ? "bookshelf:empty" : annotation.template();
                final String batch = parentBatch != null && annotation.batch().equalsIgnoreCase("defaultBatch") ? parentBatch : annotation.batch();
                final String testName = batch + "." + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, method.getName());
                final Rotation rotation = StructureUtils.getRotationForRotationSteps(annotation.rotationSteps());

                functions.add(new TestFunction(batch, testName, template, rotation, annotation.timeoutTicks(), annotation.setupTicks(), annotation.required(), annotation.requiredSuccesses(), annotation.attempts(), gameTestHelper -> {

                    try {

                        method.invoke(testImpl, gameTestHelper);
                    }

                    catch (IllegalAccessException e) {

                        throw new RuntimeException("Failed to invoke test method (%s) in (%s) because %s".formatted(method.getName(), method.getDeclaringClass().getCanonicalName(), e.getMessage()), e);
                    }

                    catch (InvocationTargetException e) {

                        final RuntimeException rte = (e.getCause() instanceof RuntimeException runtimeException) ? runtimeException : new RuntimeException(e.getCause());
                        Constants.LOG.error("The test {} failed to run!", testName, e);
                        throw rte;
                    }
                }));
            }
        }
    }

    private static boolean assertEqual(Ingredient original, Ingredient result) {

        if (Objects.equals(original, result)) {

            return true;
        }

        final ItemStack[] originalStacks = original.getItems();
        final ItemStack[] resultStacks = result.getItems();

        if (originalStacks.length != resultStacks.length) {

            Constants.LOG.error("Size mismatch. original={} result={}", originalStacks.length, resultStacks.length);
            return false;
        }

        for (int index = 0; index < originalStacks.length; index++) {

            if (!ItemStackHelper.areStacksEquivalent(originalStacks[index], resultStacks[index])) {

                Constants.LOG.error("Mismatch at index {}. original={} result={}", index, originalStacks[index], resultStacks[index]);

                return false;
            }
        }

        return true;

    }
}