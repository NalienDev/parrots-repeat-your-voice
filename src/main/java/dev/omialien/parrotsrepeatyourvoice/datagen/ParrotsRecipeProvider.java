package dev.omialien.parrotsrepeatyourvoice.datagen;

import dev.omialien.parrotsrepeatyourvoice.ParrotsRepeatYourVoice;
import dev.omialien.parrotsrepeatyourvoice.registry.ParrotDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ParrotsRecipeProvider extends RecipeProvider {
    public ParrotsRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput p_recipeOutput, HolderLookup.@NotNull Provider holderLookup) {
        super.buildRecipes(p_recipeOutput, holderLookup);
        sugary(p_recipeOutput);
    }

    private static void sugary(RecipeOutput output) {
        seedAction(output,
                "veggie_seeds",
                Component.literal("Veggie Seeds"),
                List.of(Component.literal("Feed this to your parrot to make it remember its voicelines!").withStyle(ChatFormatting.AQUA)),
                Items.CARROT,
                ParrotDataComponents.SeedActions.REMEMBER
        );
        seedAction(output,
                "choco_seeds",
                Component.literal("Choccy Seeds"),
                List.of(Component.literal("Feed this to your parrot to make it forget the last audio it played!").withStyle(ChatFormatting.RED)),
                Items.COCOA_BEANS,
                ParrotDataComponents.SeedActions.FORGETAUDIO
        );
        seedAction(output,
                "sweet_seeds",
                Component.literal("Sweet Seeds"),
                List.of(Component.literal("Feed this to your parrot to make it replay its last audio!").withStyle(ChatFormatting.GREEN)),
                Items.SUGAR,
                ParrotDataComponents.SeedActions.FORCEAUDIO
        );
    }

    private static void seedAction(RecipeOutput output, String filename, Component name, List<Component> lore, ItemLike craftItem, ParrotDataComponents.SeedActions action) {
        ItemStack item = new ItemStack(
                Items.WHEAT_SEEDS,
                1
        );
        item.set(DataComponents.ITEM_NAME, name);
        item.set(ParrotDataComponents.SEED_ACTIONS.get(), action);
        // TODO maybe make lore somehow attached to the SeedAction instead of adding it manually in the recipe
        item.set(DataComponents.LORE, new ItemLore(lore));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, item)
                .requires(Items.WHEAT_SEEDS)
                .requires(craftItem)
                .unlockedBy("getCraftItem", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance( Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(craftItem, Items.WHEAT_SEEDS).build()))))
                .save(output, ResourceLocation.fromNamespaceAndPath(ParrotsRepeatYourVoice.MOD_ID, filename));
    }
}
